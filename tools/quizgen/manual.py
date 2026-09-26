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
from curriculum import COUNTRIES, COUNTRY_ORDER, CURRICULUM  # noqa: E402
from exams import EXAM_ORDER, exam_key, is_exam_key  # noqa: E402

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


def clean_questions(raw, expected, warn, langs=None):
    langs = langs or ALL_LANGS
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
        for lang in langs:
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


def clean_texts(raw, name, warn, html=False, langs=None):
    langs = langs or ALL_LANGS
    if not isinstance(raw, dict):
        raise Invalid(f"'{name}' must map language code -> text")
    out = {}
    for lang in langs:
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


def build_quiz_doc(data, key, date, warn=print, langs=None):
    questions = clean_questions(data.get("questions"), QUESTIONS, warn, langs)
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


def build_study_doc(data, key, topic, date, warn=print, langs=None):
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
        "title": clean_texts(data.get("title"), "title", warn, langs=langs),
        "category": clean_texts(data.get("category"), "category", warn, langs=langs),
        "summary": clean_texts(data.get("summary"), "summary", warn, langs=langs),
        "lesson": clean_texts(data.get("lesson"), "lesson", warn, html=True, langs=langs),
        "minutes": max(1, minutes),
        "questions": g.assign_ids(topic[0], clean_questions(data.get("questions"), QUESTIONS, warn, langs)),
        "translatedBy": "manual", "model": "manual", "verifiedBy": "manual", "grounded": False,
    }



# --------------------------------------------------------------------------- bundle: everything in one go

def all_targets():
    """Every country, then every exam, in product-priority order."""
    return list(COUNTRY_ORDER) + [exam_key(e) for e in EXAM_ORDER]


def target_id(key):
    """The id used inside a bundle: a country code, or 'exam:<id>'."""
    return "exam:" + COUNTRIES[key]["exam"] if is_exam_key(key) else key


def key_of(target):
    if isinstance(target, str) and target.startswith("exam:"):
        return exam_key(target[5:])
    return target.upper() if isinstance(target, str) else None


def _rules_for(langs):
    names = ", ".join(f"{c} = {g.LANG_NAMES.get(c, 'English')}" for c in langs)
    return RULES.replace(_language_names(), names)


def bundle_prompt(start, days, langs, with_study):
    blocks, n = [], 0
    for key in all_targets():
        for d in range(days):
            date = start + dt.timedelta(days=d)
            slots = "\n".join(
                f"   {i + 1}. {t[1]} | anchor: {t[2]} | difficulty: {g.DIFFICULTY[i % len(g.DIFFICULTY)]}"
                for i, t in enumerate(_topics_for(key, date, QUESTIONS)))
            n += 1
            blocks.append(f'[{n}] target "{target_id(key)}" | date {date.isoformat()}\n   {g._meta(key)}\n{slots}')
    lang_list = ", ".join(langs)
    question_json = ('{"topic":"<topic title>","correct_index":<0-3>,' + ",".join(
        f'"{lang}":{{"question":"...","options":["...","...","...","..."],"explanation":"..."}}' for lang in langs) + "}")
    text = f"""You are a senior law professor writing multiple-choice questions for a legal-education app that serves law
students and bar-exam candidates in many countries. One reply must contain the daily quizzes for ALL targets below.

TARGETS ({n} quizzes, {QUESTIONS} questions each, in the order given). A target is a jurisdiction (US, UK, ...) or an
exam ("exam:us-ube", ...); exam questions must feel like that exam (a rule applied to a short fact pattern).

""" + "\n\n".join(blocks) + f"""

{_rules_for(langs)}

OUTPUT FORMAT (very important)
- Reply with ONLY JSON in code blocks, nothing else. Minified (no indentation, no line breaks inside the JSON) to save space.
- Shape: {{"quizzes":[{{"target":"US","date":"YYYY-MM-DD","questions":[Q1,Q2,Q3,Q4,Q5]}}, ...]}}
- Each question Q: {question_json}
- Every question has ALL of these languages: {lang_list}. Keep the same question, option order and correct_index in each.
- Copy "target" and "date" exactly as written above. One quiz object per numbered target.
- If your reply would exceed your output limit, STOP after a COMPLETE quiz object, close the JSON properly, end the reply
  with the line "PART 1 DONE - next target: [number]" and wait. When I write "continue", start a NEW code block with a
  new valid JSON object of the same shape containing the following targets, and so on until all {n} are done.
- Before finishing, silently re-check every answer key and every legal statement; replace any question you are not sure of."""
    if with_study:
        study = []
        for key in COUNTRY_ORDER:
            topic = g.study_topic_candidates(key, start)[0]
            study.append(f'- target "{key}" | topic "{topic[0]}": {topic[1]} | anchor: {topic[2]}\n   {g._meta(key)}')
        same = "{" + ",".join(f'"{lang}":"..."' for lang in langs) + "}"
        text += f"""

ALSO (after all quizzes, as another code block): one study lesson per jurisdiction below, shape
{{"studySets":[{{"target":"US","topic":"<slug>","title":{same},"category":{same},"summary":{same},"lesson":{same},"minutes":4,"questions":[5 questions of the same Q shape]}}]}}
Lesson = 350-500 words per language of simple HTML using ONLY <h3>,<h4>,<p>,<ul>,<li>,<b>,<i>: a hook, <h3>The rule</h3>,
<h3>How it works</h3>, <h3>Limits and exceptions</h3>, <h3>Landmark examples</h3>, <h3>Key takeaways</h3>.
The 5 questions test different aspects of the lesson, easy to hard.

""" + "\n".join(study)
    return text


def _blocks(text):
    """All JSON objects in a reply: every code block, or the whole text if there are none."""
    return re.findall(r"```(?:json)?\s*(.*?)```", text, re.S) or [text]


def load_bundle_files(paths):
    quizzes, studies, problems = [], [], []
    for path in paths:
        for i, block in enumerate(_blocks(open(path, encoding="utf-8").read().lstrip("\ufeff")), 1):
            try:
                data = load_json(block)
            except Invalid as e:
                problems.append(f"{os.path.basename(path)} block {i}: {e}")
                continue
            if isinstance(data.get("quizzes"), list):
                quizzes += data["quizzes"]
            if isinstance(data.get("studySets"), list):
                studies += data["studySets"]
    return quizzes, studies, problems


def bundle_docs(quizzes, studies, warn, langs=None):
    """Returns (docs, rejected): docs = [(kind, key, doc)]. One bad item never blocks the rest."""
    docs, rejected, seen = [], [], set()
    today = dt.datetime.now(dt.timezone.utc).date()
    for item in quizzes:
        if not isinstance(item, dict):
            rejected.append("quiz ?: not an object")
            continue
        label = f"quiz {item.get('target')} {item.get('date')}"
        try:
            key = key_of(item.get("target"))
            if key not in CURRICULUM:
                raise Invalid(f"unknown target {item.get('target')!r}")
            try:
                date = dt.date.fromisoformat(str(item.get("date")))
            except ValueError:
                raise Invalid("date must be yyyy-mm-dd")
            if (key, date) in seen:
                raise Invalid("duplicate target/date in the answer")
            seen.add((key, date))
            docs.append(("quiz", key, build_quiz_doc(item, key, date, lambda m, lb=label: warn(f"{lb}: {m}"), langs)))
        except Invalid as e:
            rejected.append(f"{label}: {e}")
    for item in studies:
        if not isinstance(item, dict):
            rejected.append("study ?: not an object")
            continue
        label = f"study {item.get('target')} {item.get('topic')}"
        try:
            key = key_of(item.get("target"))
            if key not in CURRICULUM or is_exam_key(key):
                raise Invalid(f"unknown target {item.get('target')!r}")
            topic = next((t for t in CURRICULUM[key] if t[0] == item.get("topic")), None)
            if topic is None:
                raise Invalid(f"unknown topic {item.get('topic')!r}")
            docs.append(("study", key, build_study_doc(item, key, topic, today, lambda m, lb=label: warn(f"{lb}: {m}"), langs)))
        except Invalid as e:
            rejected.append(f"{label}: {e}")
    return docs, rejected


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
    bp = sub.add_parser("bundle-prompt", help="ONE prompt for every country and exam")
    bp.add_argument("--date", help="first day, yyyy-mm-dd (default: tomorrow, UTC)")
    bp.add_argument("--days", type=int, default=1, help="how many days per target (default 1)")
    bp.add_argument("--langs", default=",".join(ALL_LANGS), help="languages, en and tr required (default: all 6)")
    bp.add_argument("--study", action="store_true", help="also ask for one study lesson per country")
    bi = sub.add_parser("bundle-import", help="validate and publish the answer(s) to the bundle prompt")
    bi.add_argument("files", nargs="+", help="one or more answer files (every JSON code block is read)")
    bi.add_argument("--dry-run", action="store_true")
    bi.add_argument("--overwrite", action="store_true")
    args = ap.parse_args()

    if args.cmd == "bundle-prompt":
        langs = [x.strip() for x in args.langs.split(",") if x.strip()]
        if not {"en", "tr"} <= set(langs) or any(x not in ALL_LANGS for x in langs):
            sys.exit(f"--langs must include en and tr and only use {list(ALL_LANGS)}")
        start = dt.date.fromisoformat(args.date) if args.date else dt.datetime.now(dt.timezone.utc).date() + dt.timedelta(days=1)
        text = bundle_prompt(start, args.days, langs, args.study)
        name = f"prompt-bundle-{start}.txt"
        open(name, "w", encoding="utf-8").write(text)
        print(text)
        print(f"\n--- saved to {name} ({len(text)} characters)", file=sys.stderr)
        return

    if args.cmd == "bundle-import":
        quizzes, studies, problems = load_bundle_files(args.files)
        docs, rejected = bundle_docs(quizzes, studies, lambda m: print("warning:", m))
        bad = problems + rejected
        for msg in bad:
            print("REJECTED:", msg)
        print(f"{len(docs)} valid item(s), {len(bad)} rejected")
        if args.dry_run or not docs:
            return
        store = g.Store(False)
        published = skipped = 0
        for kind, key, doc in docs:
            if kind == "quiz":
                ref, label = store.quiz_ref(key, dt.date.fromisoformat(doc["date"])), f"quiz {target_id(key)} {doc['date']}"
            else:
                ref, label = store.sets_ref(key).document(doc["slug"]), f"study {key} {doc['slug']}"
            if ref.get().exists and not args.overwrite:
                print(f"skipped (already exists, use --overwrite): {label}")
                skipped += 1
                continue
            (store.save_quiz if kind == "quiz" else store.save_study)(doc)
            print("published:", label)
            published += 1
        print(f"done: {published} published, {skipped} skipped, {len(bad)} rejected")
        return

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
