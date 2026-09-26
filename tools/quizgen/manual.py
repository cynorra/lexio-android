#!/usr/bin/env python3
"""Manual content: write the prompt for a Gemini *chat*, paste the answer back, publish it to Firestore.

The automatic pipeline (generate.py) is limited by the free API quota. The Gemini chat app has its own,
larger allowance, so this tool lets a person do the writing there and only uses Firestore (no Gemini API):

  1. python manual.py prompt quiz  --country TR --date 2026-09-28        -> prompt text (also saved to a file)
     python manual.py prompt quiz  --exam tr-hmgs --date 2026-09-28
     python manual.py prompt study --country TR --topic tr-sovereignty   (omit --topic: the day's topic)
  2. Paste the prompt into the Gemini chat, save its JSON answer to a file (e.g. answer.json).
  3. python manual.py import quiz  answer.json --country TR --date 2026-09-28      (or --exam <id>)
     python manual.py import study answer.json --country TR --topic tr-sovereignty
     add --dry-run to validate only, --overwrite to replace an existing document.

Documents get the same shape as the automatic ones, so the app cannot tell them apart
(model / translatedBy / verifiedBy are set to "manual"). Needs GOOGLE_APPLICATION_CREDENTIALS for `import`.
"""
import argparse
import datetime as dt
import json
import os
import re
import sys

os.environ.setdefault("GEMINI_API_KEY", "unused")  # generate.py builds a client at import; we never call it

import generate as g  # noqa: E402
from curriculum import COUNTRIES, CURRICULUM  # noqa: E402
from exams import exam_key, is_exam_key  # noqa: E402

ALL_LANGS = ("en",) + tuple(g.LANGS)
ALLOWED_TAGS = {"h3", "h4", "p", "ul", "li", "b", "i"}
QUESTIONS = 5


class Invalid(Exception):
    pass


# --------------------------------------------------------------------------- prompts

def _language_names():
    return ", ".join(f"{code} = {name}" for code, name in [("en", "English")] + list(g.LANG_NAMES.items())
                     if code in ALL_LANGS)


QUESTION_FORMAT = """{
  "topic": "<the topic title given for this question>",
  "correct_index": <0-3, position of the ONE correct option>,
  "en": {"question": "...", "options": ["...", "...", "...", "..."], "explanation": "..."},
  "tr": {"question": "...", "options": ["...", "...", "...", "..."], "explanation": "..."},
  "de": {...}, "fr": {...}, "es": {...}, "it": {...}
}"""

RULES = f"""Quality rules
- Precision first: every legal statement must be true. Only use rules, articles, years and cases you are completely
  sure of. If unsure about a detail, pick a different question. Do not depend on facts that change quickly.
- Test understanding, not trivia: what a provision protects, what a doctrine requires, how a rule applies to a short
  realistic scenario. Vary the styles. No "which is NOT" and no trick wording. Every question ends with a real question
  (a question mark or an explicit "which/what/how" request).
- Exactly 4 options, exactly ONE unambiguously correct. Distractors are plausible misconceptions of similar length.
  No "all/none of the above", no letters or numbering in options, and the correct option must not always be the
  longest or in the same position (vary correct_index).
- The explanation is 2-3 sentences: why it is right, why the tempting wrong answer fails, the provision or case by name.
- Write every language natively (not word-for-word): {_language_names()}. Keep the SAME question, the SAME option
  order and the SAME correct_index in every language. Use each country's official terms.
- Educational content, not legal advice."""


def _key(country, exam):
    return exam_key(exam) if exam else country


def _topics_for(key, date, count):
    return g.topics_for_day(key, date, count)


def quiz_prompt(key, date):
    topics = _topics_for(key, date, QUESTIONS)
    slots = "\n".join(
        f"{i + 1}. Topic: {t[1]}\n   Anchor: {t[2]}\n   Difficulty: {g.DIFFICULTY[i % len(g.DIFFICULTY)]}"
        for i, t in enumerate(topics))
    return f"""You are a senior law professor writing multiple-choice questions for a legal-education app.

{g._meta(key)}

Write exactly {QUESTIONS} questions, one per numbered slot, in this order.

{slots}

{RULES}

Output: ONLY one JSON object in a single code block, nothing before or after it:
{{"questions": [
{QUESTION_FORMAT}
]}}
(exactly {QUESTIONS} objects in "questions"; in every question all of: {", ".join(ALL_LANGS)}.)"""


def study_prompt(key, topic):
    return f"""You are a senior law professor writing a short study lesson plus self-check questions for a legal-education app.

{g._meta(key)}

Topic: {topic[1]}
Anchor: {topic[2]}

Lesson (350-500 words per language), simple HTML using ONLY <h3>, <h4>, <p>, <ul>, <li>, <b>, <i>. Structure: a short
hook (why this matters), <h3>The rule</h3>, <h3>How it works</h3>, <h3>Limits and exceptions</h3>,
<h3>Landmark examples</h3>, <h3>Key takeaways</h3> (3-4 bullets). Short paragraphs, concrete examples, provisions and
cases by name. Only state what you are sure of.
Also give: a title, a category (e.g. "Constitutional Law"), a 1-2 sentence summary for a list card, and the reading
time in whole minutes (a number, the same for all languages).

Then write exactly {QUESTIONS} self-check questions about DIFFERENT aspects of this lesson, from easy to hard.

{RULES}

Output: ONLY one JSON object in a single code block, nothing before or after it:
{{
  "title":    {{"en": "...", "tr": "...", "de": "...", "fr": "...", "es": "...", "it": "..."}},
  "category": {{"en": "...", "tr": "...", "de": "...", "fr": "...", "es": "...", "it": "..."}},
  "summary":  {{"en": "...", "tr": "...", "de": "...", "fr": "...", "es": "...", "it": "..."}},
  "lesson":   {{"en": "<h3>...", "tr": "...", "de": "...", "fr": "...", "es": "...", "it": "..."}},
  "minutes": 4,
  "questions": [
{QUESTION_FORMAT}
  ]
}}"""


# --------------------------------------------------------------------------- validation

def load_json(text):
    """Accepts the raw answer: a JSON object, optionally wrapped in a ``` fence or surrounded by chatter."""
    text = text.strip().lstrip("﻿")
    m = re.search(r"```(?:json)?\s*(.*?)```", text, re.S)
    if m:
        text = m.group(1).strip()
    start, end = text.find("{"), text.rfind("}")
    if start < 0 or end < start:
        raise Invalid("no JSON object found in the file")
    try:
        return json.loads(text[start:end + 1])
    except json.JSONDecodeError as e:
        raise Invalid(f"the JSON is not valid: {e}")


def _clean_localized(loc):
    """Returns the localized question if it is well-formed, else None."""
    try:
        options = loc["options"]
        if len(options) != 4 or not all(isinstance(o, str) and o.strip() for o in options):
            return None
        if len({o.strip().lower() for o in options}) != 4:
            return None
        if not (isinstance(loc["question"], str) and loc["question"].strip()):
            return None
        if not (isinstance(loc["explanation"], str) and loc["explanation"].strip()):
            return None
        return {"question": loc["question"].strip(), "options": [o.strip() for o in options],
                "explanation": loc["explanation"].strip()}
    except (KeyError, TypeError, AttributeError):
        return None


def clean_questions(raw, expected, warn):
    if not isinstance(raw, list):
        raise Invalid("'questions' must be a list")
    if len(raw) != expected:
        raise Invalid(f"expected {expected} questions, got {len(raw)}")
    out = []
    for i, q in enumerate(raw, 1):
        if not isinstance(q, dict):
            raise Invalid(f"question {i} is not an object")
        ci = q.get("correct_index")
        if isinstance(ci, bool) or not isinstance(ci, int) or not 0 <= ci <= 3:
            raise Invalid(f"question {i}: correct_index must be 0-3")
        item = {"topic": str(q.get("topic", "")).strip(), "correct_index": ci}
        for lang in ALL_LANGS:
            loc = _clean_localized(q.get(lang))
            if loc is None:
                if lang in ("en", "tr"):
                    raise Invalid(f"question {i}: '{lang}' is missing or malformed (need question, 4 different options, explanation)")
                warn(f"question {i}: '{lang}' skipped (missing or malformed); the app falls back to English")
                continue
            item[lang] = loc
        out.append(item)
    return out


def check_html(html, where):
    for tag in re.findall(r"</?\s*([a-zA-Z0-9]+)", html):
        if tag.lower() not in ALLOWED_TAGS:
            raise Invalid(f"{where}: HTML tag <{tag}> is not allowed (use only {sorted(ALLOWED_TAGS)})")


def clean_texts(raw, name, warn, html=False):
    if not isinstance(raw, dict):
        raise Invalid(f"'{name}' must map language code -> text")
    out = {}
    for lang in ALL_LANGS:
        v = raw.get(lang)
        if not (isinstance(v, str) and v.strip()):
            if lang in ("en", "tr"):
                raise Invalid(f"'{name}' needs '{lang}'")
            warn(f"'{name}': '{lang}' skipped")
            continue
        if html:
            check_html(v, f"lesson[{lang}]")
        out[lang] = v.strip()
    return out


def build_quiz_doc(data, key, date, warn=print):
    questions = clean_questions(data.get("questions"), QUESTIONS, warn)
    meta = COUNTRIES[key]
    doc = {
        "country": meta.get("country", key),
        "date": date.isoformat(),
        "questions": g.assign_ids(f"{key}-{date.isoformat()}", questions),
        "translatedBy": "manual", "model": "manual", "verifiedBy": "manual", "grounded": False,
    }
    if is_exam_key(key):
        doc["exam"] = meta["exam"]
    return doc


def build_study_doc(data, key, topic, date, warn=print):
    if is_exam_key(key):
        raise Invalid("exams have daily quizzes only, no study sets")
    try:
        minutes = int(data["minutes"])
    except (KeyError, TypeError, ValueError):
        raise Invalid("'minutes' must be a whole number")
    published_at = int(dt.datetime(date.year, date.month, date.day, tzinfo=dt.timezone.utc).timestamp() * 1000)
    return {
        "country": key, "slug": topic[0], "date": date.isoformat(), "publishedAt": published_at,
        "reference": topic[2],
        "title": clean_texts(data.get("title"), "title", warn),
        "category": clean_texts(data.get("category"), "category", warn),
        "summary": clean_texts(data.get("summary"), "summary", warn),
        "lesson": clean_texts(data.get("lesson"), "lesson", warn, html=True),
        "minutes": max(1, minutes),
        "questions": g.assign_ids(topic[0], clean_questions(data.get("questions"), QUESTIONS, warn)),
        "translatedBy": "manual", "model": "manual", "verifiedBy": "manual", "grounded": False,
    }


# --------------------------------------------------------------------------- CLI

def _resolve(args):
    if args.exam:
        key = exam_key(args.exam.lower())
        if key not in CURRICULUM:
            sys.exit(f"unknown exam id: {args.exam}")
        return key
    key = (args.country or "").upper()
    if key not in CURRICULUM or is_exam_key(key):
        sys.exit("give --country <US|UK|DE|FR|ES|IT|TR> or --exam <id>")
    return key


def _date(args):
    return dt.date.fromisoformat(args.date) if args.date else dt.datetime.now(dt.timezone.utc).date()


def _study_topic(key, args, date):
    if args.topic:
        for t in CURRICULUM[key]:
            if t[0] == args.topic:
                return t
        sys.exit(f"unknown topic '{args.topic}' for {key}; known: {[t[0] for t in CURRICULUM[key]]}")
    return g.study_topic_candidates(key, date)[0]


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = ap.add_subparsers(dest="cmd", required=True)
    for name in ("prompt", "import"):
        p = sub.add_parser(name)
        p.add_argument("kind", choices=["quiz", "study"])
        if name == "import":
            p.add_argument("file")
            p.add_argument("--dry-run", action="store_true", help="validate only, write nothing")
            p.add_argument("--overwrite", action="store_true", help="replace an existing document")
        p.add_argument("--country")
        p.add_argument("--exam")
        p.add_argument("--date", help="yyyy-mm-dd (default: today, UTC)")
        p.add_argument("--topic", help="study topic slug")
    args = ap.parse_args()
    key, date = _resolve(args), _date(args)

    if args.cmd == "prompt":
        if args.kind == "quiz":
            text, name = quiz_prompt(key, date), f"prompt-quiz-{key}-{date}.txt"
        else:
            topic = _study_topic(key, args, date)
            text, name = study_prompt(key, topic), f"prompt-study-{topic[0]}.txt"
        with open(name, "w", encoding="utf-8") as f:
            f.write(text)
        print(text)
        print(f"\n--- saved to {name}", file=sys.stderr)
        return

    try:
        data = load_json(open(args.file, encoding="utf-8").read())
        warn = lambda m: print("warning:", m)  # noqa: E731
        if args.kind == "quiz":
            doc = build_quiz_doc(data, key, date, warn)
            target = f"{key} quiz {date}"
        else:
            topic = _study_topic(key, args, date)
            doc = build_study_doc(data, key, topic, date, warn)
            target = f"{key} study set {topic[0]}"
    except Invalid as e:
        sys.exit(f"REJECTED: {e}")

    print(f"valid: {target}")
    if args.dry_run:
        return
    store = g.Store(False)
    if args.kind == "quiz":
        exists = store.quiz_ref(key, date).get().exists
    else:
        exists = store.sets_ref(key).document(doc["slug"]).get().exists
    if exists and not args.overwrite:
        sys.exit("a document already exists there; add --overwrite to replace it")
    (store.save_quiz if args.kind == "quiz" else store.save_study)(doc)
    print("published.")


if __name__ == "__main__":
    main()
