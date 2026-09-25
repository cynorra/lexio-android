#!/usr/bin/env python3
"""Generates daily law quizzes and constitution study sets and pushes them to Firestore.

Fully automatic: run it on a schedule (see .github/workflows/quizgen.yml). It is idempotent -
content that already exists for a date is skipped unless --force is passed - and it generates
a few days ahead so a failed run never leaves the app without a quiz.

Pipeline for every topic
  1. RESEARCH   Reference excerpts from Wikipedia (English + the country's own language, free, no
                billing; see sources.py), filtered for relevance. Cached in Firestore (researchNotes/)
                for 30 days, so a topic is researched about once a month.
  2. WRITE      Questions / lessons written ONLY from those notes, in English.
  3. CHECK      An independent pass against the notes. Only material errors block publishing
                (wrong answer key, false legal statement, ambiguous answer); style or nuance is ignored.
  3b. TRANSLATE The verified English is translated to Turkish, German, French, Spanish and Italian by a dedicated engine (DeepL / Google
                Cloud Translation, see translate.py), not by the LLM.
  4. REPAIR     Failing items are rewritten with the checker's feedback, not thrown away. A topic
                that still cannot be made correct is swapped for another one, so every day gets
                content and a single hard topic never blocks a run.

Firestore layout (read-only for clients, written here with the Admin SDK):

  dailyQuizzes/{CC}/days/{YYYY-MM-DD}   -> {country, date, questions[], model, generatedAt}
  studySets/{CC}/sets/{slug}            -> {country, slug, date, publishedAt, title{}, category{},
                                            summary{}, lesson{}, reference, minutes, questions[]}
  researchNotes/{CC}__{slug}            -> {notes, updatedAt}   (internal cache, no client access)

Every stored question is {id, topic, correct_index, en:{question, options[4], explanation}, tr:{...}}.

Environment:
  GEMINI_API_KEY                    required (Google AI Studio key)
  GOOGLE_APPLICATION_CREDENTIALS    path to a Firebase service-account JSON (required unless --dry-run)
  QUIZGEN_MODEL                     optional, model that writes the content (default gemini-3.5-flash-lite)
  QUIZGEN_VERIFY_MODEL              optional, model that checks it (default: same as QUIZGEN_MODEL)
  QUIZGEN_MIN_INTERVAL              optional, seconds between model calls (default 4; 0 on a paid key)
  QUIZGEN_RESEARCH                  optional, "off" skips the research step (content then relies on the model alone)
  DEEPL_API_KEY                     optional, Turkish translation via DeepL (see translate.py for alternatives)
"""
import argparse
import concurrent.futures
import datetime as dt
import json
import os
import random
import re
import sys
import threading
import time

from google import genai

from curriculum import COUNTRIES, COUNTRY_ORDER, CURRICULUM
import sources
from translate import Translator

# `or` (not a get() default) so an empty value passed by the workflow falls back to the default.
MODEL = os.environ.get("QUIZGEN_MODEL") or "gemini-3.5-flash-lite"
VERIFY_MODEL = os.environ.get("QUIZGEN_VERIFY_MODEL") or MODEL
RESEARCH_ENABLED = (os.environ.get("QUIZGEN_RESEARCH") or "on").lower() != "off"

# Languages every question/lesson is translated into (English is always the source). Turkish is required,
# the others are best effort. Override with e.g. QUIZGEN_LANGS=tr,de to save translation quota.
LANG_NAMES = {"tr": "Turkish", "de": "German", "fr": "French", "es": "Spanish", "it": "Italian"}
LANGS = tuple(l for l in (os.environ.get("QUIZGEN_LANGS") or "tr,de,fr,es,it").replace(" ", "").split(",")
              if l in LANG_NAMES) or ("tr",)
QUESTIONS_PER_QUIZ = 5
QUESTIONS_PER_STUDY_SET = 5
MIN_QUESTIONS = 4        # a quiz/study set with fewer verified questions than this is not published
MAX_ATTEMPTS = 4         # write/check/repair rounds per set of questions
LESSON_REVISIONS = 2     # targeted lesson rewrites before falling back to a conservative lesson
STUDY_TOPIC_FALLBACKS = 3  # how many different topics to try for one study slot
NOTES_TTL_DAYS = 30
CALL_ATTEMPTS = 5
# Minimum seconds between model calls across all threads. Free-tier keys have per-minute limits
# (a few requests/minute), so calls are paced instead of fired in bursts. Set 0 on a paid key.
MIN_INTERVAL = float(os.environ.get("QUIZGEN_MIN_INTERVAL") or 4)
_pace_lock = threading.Lock()
_last_call = [0.0]

client = genai.Client()  # reads GEMINI_API_KEY (or GOOGLE_API_KEY) from the environment

DIFFICULTY = ["easy", "easy-to-medium", "medium", "medium-to-hard", "hard"]


class QuotaExhausted(Exception):
    """The API key's daily quota is used up; retrying cannot help until it resets or billing is enabled."""


class Rejected(Exception):
    """Content could not be made correct; the caller falls back to another topic."""


def log(msg):
    print(msg, flush=True)


# --------------------------------------------------------------------------- model access

CALLS = {"requests": 0}  # every request sent, including retries: this is what counts against the daily quota


def _pace():
    with _pace_lock:
        wait = _last_call[0] + MIN_INTERVAL - time.monotonic()
        if wait > 0:
            time.sleep(wait)
        _last_call[0] = time.monotonic()
        CALLS["requests"] += 1


def _call(system, user, *, schema=None, tools=None, thinking="medium", max_tokens=16000, model=None, parse=None,
          attempts=None):
    """One model call with retries. `parse` runs inside the retry loop, so truncated or malformed
    output is retried like any other transient failure. Daily-quota errors fail fast."""
    attempts = attempts or CALL_ATTEMPTS
    last_error = None
    for attempt in range(attempts):
        try:
            _pace()
            kwargs = dict(
                model=model or MODEL,
                system_instruction=system,
                input=user,
                generation_config={"temperature": 0.6, "max_output_tokens": max_tokens,
                                   **({"thinking_level": thinking} if thinking else {})},
                store=False,  # stateless: nothing here needs to be kept on Google's side
            )
            if schema is not None:
                kwargs["response_format"] = {"type": "text", "mime_type": "application/json", "schema": schema}
            if tools:
                kwargs["tools"] = tools
            text = client.interactions.create(**kwargs).output_text
            if not text:
                raise ValueError("empty response (blocked or no output)")
            return parse(text) if parse else text
        except Exception as e:  # SDK error classes vary by version; treat all as retryable
            last_error = e
            message = str(e)
            if re.search(r"per day|PerDay", message, re.IGNORECASE):
                raise QuotaExhausted(message[:300]) from e
            delay = min(2 ** attempt * 3 + random.uniform(0, 2), 90)
            hint = re.search(r"retry in (\d+(?:\.\d+)?)s", message)  # the API says how long to wait
            if hint:
                delay = min(float(hint.group(1)) + 2, 120)
            log(f"  model call failed ({type(e).__name__}: {message[:200]}); retry {attempt + 1}/{attempts} in {delay:.0f}s")
            time.sleep(delay)
    raise RuntimeError(f"model call failed after {attempts} attempts: {last_error}")


def call_json(system, user, schema, **kw):
    return _call(system, user, schema=schema, parse=json.loads, **kw)


# --------------------------------------------------------------------------- JSON schemas

def _question_schema():
    return {
        "type": "object",
        "properties": {
            "topic": {"type": "string"},
            "correct_index": {"type": "integer"},
            "question": {"type": "string"},
            "options": {"type": "array", "items": {"type": "string"}},
            "explanation": {"type": "string"},
        },
        "required": ["topic", "correct_index", "question", "options", "explanation"],
        "additionalProperties": False,
    }


QUESTIONS_SCHEMA = {
    "type": "object",
    "properties": {"questions": {"type": "array", "items": _question_schema()}},
    "required": ["questions"],
    "additionalProperties": False,
}

LESSON_SCHEMA = {
    "type": "object",
    "properties": {
        "title": {"type": "string"},
        "category": {"type": "string"},
        "summary": {"type": "string"},
        "lesson": {"type": "string"},
        "minutes": {"type": "integer"},
    },
    "required": ["title", "category", "summary", "lesson", "minutes"],
    "additionalProperties": False,
}

RELEVANCE_SCHEMA = {
    "type": "object",
    "properties": {"keep": {"type": "array", "items": {"type": "integer"}}},
    "required": ["keep"],
    "additionalProperties": False,
}

VERIFY_SCHEMA = {
    "type": "object",
    "properties": {
        "results": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "index": {"type": "integer"},
                    "verdict": {"type": "string", "enum": ["ok", "minor", "error"]},
                    "problem": {"type": "string"},
                },
                "required": ["index", "verdict", "problem"],
                "additionalProperties": False,
            },
        }
    },
    "required": ["results"],
    "additionalProperties": False,
}

# --------------------------------------------------------------------------- prompts

WRITER_RULES = """You are a senior law professor writing for a legal-education app used by students and curious adults.

Quality bar
- Precision first. Every legal statement must be true and must be supported by the reference notes you are
  given. Never add a fact, article number, year or case that is not in the notes. If the notes do not cover
  something, leave it out.
- Questions test understanding, not memorised trivia: what a provision protects, what a doctrine requires,
  how a rule applies to a short realistic scenario, which body decides. Vary the styles across a set
  (definition, scenario, "which provision", cause/effect). Avoid "which of the following is NOT" and any
  trick wording.
- Exactly 4 options, exactly ONE unambiguously correct. Distractors are plausible misconceptions a student
  might really hold, similar in length and grammar to the right answer. No "all/none of the above", no letters
  or numbering in options, and do not make the correct option systematically the longest or in a fixed position.
- The explanation is 2-3 sentences: why the answer is right, why the tempting wrong answer fails, and the
  provision or case by name.
- Do not depend on facts that change quickly (office holders, statistics, pending cases).

Write in clear American English. Turkish is produced separately by a translation engine, so keep
sentences simple and unambiguous and avoid idioms, puns and wordplay that translate badly.

This is educational content, not legal advice."""


def _meta(country):
    m = COUNTRIES[country]
    return f"Jurisdiction: {m['name']} ({m['system']}). Primary sources: {m['sources']}."


def relevance_prompt(country, topic, candidates):
    listing = "\n\n".join(f"[{i}] ({c['lang']}) {c['title']}\n{c['text'][:500]}" for i, c in enumerate(candidates))
    return (
        f"{_meta(country)}\nTopic to teach: {topic[1]}\nAnchor: {topic[2]}\n\n"
        "Below are candidate encyclopedia articles found by search. Return the indices of the articles that are "
        "genuinely ABOUT this topic in this jurisdiction and would help someone write accurate lesson material on "
        "it. Drop articles about a different subject, a different country, or that only mention the topic in "
        "passing. Return an empty list if none qualify.\n\n" + listing
    )


def _notes_block(notes):
    if not notes:
        return ("(No reference notes are available for this topic. Rely only on facts you are completely certain "
                "of, and prefer questions about well-established core principles.)")
    return ("REFERENCE NOTES (excerpts from public encyclopedia articles; base factual claims on them. They can "
            "contain mistakes or be out of date: if a point contradicts settled law you know well, leave that point "
            "out rather than repeat it):\n" + notes)


def questions_prompt(country, slots, notes_by_slot, feedback):
    """slots: list of (label, topic). One question per slot, in order."""
    lines = []
    for i, (label, topic) in enumerate(slots):
        lines.append(f"{i + 1}. {label}\n   Topic: {topic[1]} (anchor: {topic[2]})\n"
                     f"   {_notes_block(notes_by_slot[i])}")
        if feedback.get(i):
            lines.append("   A previous attempt at this slot was rejected by the fact-checker for: "
                         + " | ".join(feedback[i]) + "\n   Write a completely NEW question that avoids these problems.")
    return (
        f"{_meta(country)}\n\nWrite exactly {len(slots)} questions, one per numbered slot below, in this order. "
        "Set each question's 'topic' to the topic title.\n\n" + "\n\n".join(lines)
    )


def lesson_prompt(country, topic, notes, problems=None, previous=None, conservative=False):
    text = (
        f"{_meta(country)}\n\nWrite a study lesson on: {topic[1]}\nAnchor: {topic[2]}\n\n{_notes_block(notes)}\n\n"
        "Return:\n"
        "- title and category (e.g. 'Constitutional Law').\n"
        "- summary: 1-2 engaging sentences for a list card.\n"
        "- lesson: simple HTML using only <h3>, <h4>, <p>, <ul>, <li>, <b>, <i>. Structure: a short "
        "hook (why this matters), <h3>The rule</h3>, <h3>How it works</h3>, <h3>Limits and exceptions</h3>, "
        "<h3>Landmark examples</h3>, <h3>Key takeaways</h3> (3-4 bullets). Short paragraphs, concrete examples, "
        "cite provisions and cases by name.\n"
        "- minutes: reading time in whole minutes.\n"
    )
    if conservative:
        text += ("\nCONSERVATIVE MODE: write 200-300 words and state ONLY claims that appear "
                 "verbatim or nearly verbatim in the notes. Omit anything you cannot ground.\n")
    else:
        text += "\nLength: 350-500 words.\n"
    if problems:
        text += ("\nA fact-checker rejected the previous version of this lesson for the problems below. Fix each one "
                 "(remove the claim if the notes do not support it) and keep everything else:\n- "
                 + "\n- ".join(problems) + "\n\nPrevious version:\n" + (previous or "")[:6000])
    return text


def verify_questions_prompt(country, questions, notes_by_index):
    payload = [
        {
            "index": i,
            "topic": q["topic"],
            "question": q["question"],
            "options": q["options"],
            "claimed_correct_index": q["correct_index"],
            "explanation": q["explanation"],
            "reference_notes": notes_by_index[i][:4000],
        }
        for i, q in enumerate(questions)
    ]
    return (
        f"You are a meticulous legal fact-checker for {COUNTRIES[country]['name']} law. Check every question "
        "against its reference notes and your own knowledge of the law.\n\n"
        "Verdicts:\n"
        "- \"error\": the claimed answer is wrong; another option is also correct or none is; a stated provision, "
        "article number, year or case is wrong; or the question is materially ambiguous.\n"
        "- \"minor\": style, wording preferences, missing nuance, a distractor that could be better. These do NOT block "
        "publishing.\n"
        "- \"ok\": no issue.\n"
        "Only use \"error\" for a real, concrete problem you can name in one sentence. Do not reject a question "
        "because you would have phrased it differently. If notes are empty, judge by your own certain knowledge.\n\n"
        f"Questions:\n{json.dumps(payload, ensure_ascii=False, indent=1)}"
    )


def verify_lesson_prompt(country, title, lesson_html, notes):
    return (
        f"You are a meticulous legal fact-checker for {COUNTRIES[country]['name']} law. Check this lesson titled "
        f"'{title}' against the reference notes and your own knowledge.\n"
        "Return exactly one result with index 0. verdict \"error\" only for a concrete material mistake: a false "
        "legal statement, a wrong article number / year / court / case name, a misdescribed holding. Missing nuance, "
        "style or optional detail is \"minor\" or \"ok\". Name each error in the 'problem' field (one sentence each, "
        "separated by ' | ').\n\n"
        f"REFERENCE NOTES:\n{notes[:6000] or '(none)'}\n\nLESSON:\n{lesson_html[:12000]}"
    )


# --------------------------------------------------------------------------- validation

def structurally_valid(q):
    try:
        if not isinstance(q["correct_index"], int) or not 0 <= q["correct_index"] <= 3:
            return False
        options = q["options"]
        if len(options) != 4 or len({o.strip().lower() for o in options}) != 4:
            return False
        if any(not o.strip() for o in options):
            return False
        return bool(q["question"].strip()) and bool(q["explanation"].strip())
    except (KeyError, TypeError, AttributeError):
        return False


# --------------------------------------------------------------------------- research (grounded)

class Research:
    """Search-grounded notes per (country, topic), cached in memory and in Firestore."""

    def __init__(self, store):
        self.store = store
        self.grounding = RESEARCH_ENABLED
        self._memory = {}
        self._lock = threading.Lock()

    def notes(self, country, topic, today):
        key = f"{country}__{topic[0]}"
        with self._lock:
            if key in self._memory:
                return self._memory[key]
        cached = self.store.get_notes(key)
        if cached is None and self.grounding:
            cached = self._fetch(country, topic, today)
            if cached:
                self.store.save_notes(key, cached)
        with self._lock:
            self._memory[key] = cached or ""
        return cached or ""

    def _fetch(self, country, topic, today):
        """Wikipedia excerpts (no LLM, no billing) filtered for relevance by one cheap model call."""
        try:
            candidates = sources.fetch_candidates(COUNTRIES[country]["name"], sources.NATIVE_LANGUAGE.get(country), topic)
            if not candidates:
                log(f"  [{country}] no reference articles found for '{topic[0]}'; writing without notes")
                return ""
            keep = call_json("You select reference material for legal writers.",
                             relevance_prompt(country, topic, candidates), RELEVANCE_SCHEMA,
                             thinking="low", max_tokens=1000, attempts=2)["keep"]
            chosen = [candidates[i] for i in keep if isinstance(i, int) and 0 <= i < len(candidates)]
            if not chosen:
                log(f"  [{country}] none of the found articles fit '{topic[0]}'; writing without notes")
                return ""
            notes = "\n\n".join(f"[Source: Wikipedia ({c['lang']}): {c['title']} - {c['url']}]\n{c['text']}"
                                for c in chosen)
            return notes[:14000]
        except QuotaExhausted:
            raise
        except Exception as e:  # research is best-effort: never let it block the run
            log(f"  [{country}] research skipped for '{topic[0]}' ({type(e).__name__}: {str(e)[:160]})")
            return ""


# --------------------------------------------------------------------------- write / check / repair

def check_questions(country, questions, notes_by_index):
    """Returns {index: problem} for questions with a material error."""
    data = call_json(
        "You verify legal exam questions. Be strict about facts, lenient about style.",
        verify_questions_prompt(country, questions, notes_by_index),
        VERIFY_SCHEMA, thinking="high", max_tokens=8000, model=VERIFY_MODEL)
    verdicts = {r["index"]: r for r in data["results"]}
    failed = {}
    for i in range(len(questions)):
        r = verdicts.get(i)
        if r is None:
            failed[i] = "not confirmed by the fact-check"
        elif r["verdict"] == "error":
            failed[i] = r["problem"] or "unspecified error"
    return failed


def gather_questions(country, slots, notes_for, alternatives):
    """Fills every slot with a verified question.

    slots        list of (label, topic)
    notes_for    fn(topic) -> reference notes
    alternatives fn(topic) -> another topic to use if a slot keeps failing (or None)
    Failing slots are rewritten with the checker's feedback; from the 3rd attempt on they switch to a
    different topic, so one stubborn topic cannot block the set.
    """
    slots = list(slots)
    result = [None] * len(slots)
    feedback = {}
    for attempt in range(1, MAX_ATTEMPTS + 1):
        pending = [i for i, q in enumerate(result) if q is None]
        if not pending:
            break
        if attempt >= 3:
            for i in pending:
                alt = alternatives(slots[i][1])
                if alt is not None:
                    slots[i] = (slots[i][0], alt)
                    feedback.pop(i, None)
        sub = [slots[i] for i in pending]
        sub_notes = [notes_for(t) for _, t in sub]
        sub_feedback = {k: feedback.get(i, []) for k, i in enumerate(pending)}
        data = call_json(WRITER_RULES, questions_prompt(country, sub, sub_notes, sub_feedback), QUESTIONS_SCHEMA,
                         thinking="medium", max_tokens=16000)
        candidates = data["questions"]
        if len(candidates) != len(sub):  # misaligned output: try again rather than guessing which is which
            log(f"  [{country}] attempt {attempt}: model returned {len(candidates)} questions for {len(sub)} slots")
            continue
        valid = [k for k, q in enumerate(candidates) if structurally_valid(q)]
        for k in range(len(sub)):
            if k not in valid:
                feedback.setdefault(pending[k], []).append("malformed output; follow the format exactly")
        if not valid:
            continue
        failed = check_questions(country, [candidates[k] for k in valid], [sub_notes[k] for k in valid])
        for n, k in enumerate(valid):
            slot_index = pending[k]
            if n in failed:
                feedback.setdefault(slot_index, []).append(failed[n])
            else:
                result[slot_index] = candidates[k]
        if failed:
            log(f"  [{country}] attempt {attempt}: {len(failed)} question(s) sent back for repair")
    return [q for q in result if q is not None]


def make_lesson(country, topic, notes):
    """Writes and verifies a lesson. Targeted rewrites first, then a conservative version."""
    problems, previous = None, None
    modes = [False] * (1 + LESSON_REVISIONS) + [True]  # normal, revise, revise, conservative
    for round_no, conservative in enumerate(modes):
        lesson = call_json(WRITER_RULES, lesson_prompt(country, topic, notes, problems, previous, conservative),
                           LESSON_SCHEMA, thinking="medium", max_tokens=16000)
        if not lesson["lesson"].strip():
            problems = ["the lesson text was empty"]
            continue
        check = call_json(
            "You verify legal teaching material. Be strict about facts, lenient about style.",
            verify_lesson_prompt(country, lesson["title"], lesson["lesson"], notes),
            VERIFY_SCHEMA, thinking="high", max_tokens=4000, model=VERIFY_MODEL)["results"]
        errors = [r["problem"] for r in check if r["verdict"] == "error"]
        if not errors:
            return lesson
        problems, previous = errors, lesson["lesson"]
        log(f"  [{country}] lesson '{topic[0]}' round {round_no + 1}: {len(errors)} error(s), repairing")
    raise Rejected(f"lesson for {topic[0]} could not be made correct: {problems}")


# --------------------------------------------------------------------------- translation

def gemini_translate(texts, is_html, lang="tr"):
    """Fallback translator: the LLM, one JSON list in, one JSON list out."""
    language = LANG_NAMES[lang]
    schema = {"type": "object", "properties": {"translations": {"type": "array", "items": {"type": "string"}}},
              "required": ["translations"], "additionalProperties": False}
    kind = "HTML fragments (keep every tag exactly as is)" if is_html else "plain strings"
    data = call_json(
        f"You are a professional English-to-{language} legal translator. Use the official {language} legal "
        "terminology of the country's own legal system where one exists. Keep the friendly, clear tone of a study "
        "app. Return one translation per input, same order.",
        f"Translate these {len(texts)} {kind} into natural {language}:\n{json.dumps(texts, ensure_ascii=False)}",
        schema, thinking="low", max_tokens=16000)
    if len(data["translations"]) != len(texts):
        raise ValueError("translation count mismatch")
    return data["translations"]


def gemini_translate_multi(texts, is_html, langs):
    """All target languages in ONE request: {lang: [translation per input]}."""
    schema = {"type": "object", "properties": {lang: {"type": "array", "items": {"type": "string"}} for lang in langs},
              "required": list(langs), "additionalProperties": False}
    kind = "HTML fragments (keep every tag exactly as is)" if is_html else "plain strings"
    names = ", ".join(f"{lang} = {LANG_NAMES[lang]}" for lang in langs)
    data = call_json(
        "You are a professional English-to-multilingual legal translator. Use the official legal terminology of each "
        "target language's own legal system where one exists. Keep the friendly, clear tone of a study app. For every "
        "language key return exactly one translation per input, in the same order.",
        f"Translate these {len(texts)} {kind} into each of these languages ({names}). Return an object keyed by "
        f"language code.\n{json.dumps(texts, ensure_ascii=False)}",
        schema, thinking="low", max_tokens=32000)
    return {lang: data[lang] for lang in langs if isinstance(data.get(lang), list)}


def translate_many(translator, texts, is_html=False, langs=None):
    """{lang: translations} for every target language. Turkish is required; another language that fails
    for a non-quota reason is skipped (the app falls back to English and a later run backfills it)."""
    langs = list(langs if langs is not None else LANGS)
    if hasattr(translator, "translate_multi"):
        out = translator.translate_multi(texts, is_html, langs)
        if "tr" in langs and "tr" not in out:
            raise ValueError("Turkish translation missing")
        for lang in langs:
            if lang not in out:
                log(f"  translation to {lang} missing; skipping it for now")
        return out
    out = {}
    for lang in langs:
        try:
            out[lang] = translator.translate(texts, is_html, lang)
        except QuotaExhausted:
            raise
        except Exception as e:
            if lang == "tr":
                raise
            log(f"  translation to {lang} failed ({type(e).__name__}: {e}); skipping it for now")
    return out


def translate_questions(translator, questions, langs=None):
    """English questions -> stored form {topic, correct_index, en:{...}, tr:{...}, de:{...}, ...}."""
    flat = []
    for q in questions:
        flat += [q["question"], *q["options"], q["explanation"]]
    translated = translate_many(translator, flat, False, langs)
    out, n = [], 6  # question + 4 options + explanation per question
    for k, q in enumerate(questions):
        item = {
            "topic": q["topic"],
            "correct_index": q["correct_index"],
            "en": {"question": q["question"], "options": q["options"], "explanation": q["explanation"]},
        }
        for lang, texts in translated.items():
            chunk = texts[k * n:(k + 1) * n]
            item[lang] = {"question": chunk[0], "options": chunk[1:5], "explanation": chunk[5]}
        out.append(item)
    return out


def localized_fields(translator, fields, lesson_html, langs=None):
    """{'title': {'en':..,'tr':..}, ...} for the short fields plus the HTML lesson."""
    keys = list(fields)
    result = {k: {"en": fields[k]} for k in keys}
    result["lesson"] = {"en": lesson_html}
    short = translate_many(translator, [fields[k] for k in keys], False, langs)
    long = translate_many(translator, [lesson_html], True, langs)
    for lang in short:
        if lang not in long:
            continue  # only publish a language whose short texts AND lesson both arrived
        for k, text in zip(keys, short[lang]):
            result[k][lang] = text
        result["lesson"][lang] = long[lang][0]
    return result


def missing_langs(item):
    return [lang for lang in LANGS if lang not in item]


def complete_quiz_languages(doc, translator):
    """Adds translations a stored quiz lacks (new language, or one that failed earlier). True if changed."""
    questions = doc.get("questions", [])
    langs = sorted({lang for q in questions for lang in missing_langs(q)}, key=LANGS.index)
    if not langs:
        return False
    fresh = translate_questions(translator, [dict(q["en"], topic=q.get("topic"), correct_index=q["correct_index"])
                                             for q in questions], langs)
    changed = False
    for q, f in zip(questions, fresh):
        for lang in langs:
            if lang in f and lang not in q:
                q[lang] = f[lang]
                changed = True
    return changed


def complete_study_languages(doc, translator):
    """Same for a stored study set (lesson fields and its questions). True if changed."""
    fields = ("title", "category", "summary")
    langs = [lang for lang in LANGS if any(lang not in doc[k] for k in (*fields, "lesson"))]
    changed = False
    if langs:
        fresh = localized_fields(translator, {k: doc[k]["en"] for k in fields}, doc["lesson"]["en"], langs)
        for k in (*fields, "lesson"):
            for lang in langs:
                if lang in fresh[k] and lang not in doc[k]:
                    doc[k][lang] = fresh[k][lang]
                    changed = True
    return complete_quiz_languages(doc, translator) or changed


# --------------------------------------------------------------------------- content builders

def day_number(date):
    return (date - dt.date(2026, 1, 1)).days


def topics_for_day(country, date, count):
    topics = CURRICULUM[country]
    base = day_number(date) * count
    return [topics[(base + k) % len(topics)] for k in range(count)]


def study_topic_candidates(country, date):
    """The day's study topic first, then the next ones in the curriculum as fallbacks."""
    topics = CURRICULUM[country]
    start = (day_number(date) * 3 + 1) % len(topics)
    return [topics[(start + k) % len(topics)] for k in range(STUDY_TOPIC_FALLBACKS)]


def _alternative_picker(country, used):
    topics = CURRICULUM[country]
    used = set(used)

    def pick(current):
        start = next((i for i, t in enumerate(topics) if t[0] == current[0]), 0)
        for step in range(1, len(topics)):
            cand = topics[(start + step * 7) % len(topics)]  # jump around so alternatives differ in subject
            if cand[0] not in used:
                used.add(cand[0])
                return cand
        return None

    return pick


def assign_ids(prefix, questions):
    for i, q in enumerate(questions):
        q["id"] = f"{prefix}-{i + 1}"
    return questions


def build_daily_quiz(country, date, research, today, translator):
    topics = topics_for_day(country, date, QUESTIONS_PER_QUIZ)
    slots = [(f"Difficulty: {DIFFICULTY[i % len(DIFFICULTY)]}", t) for i, t in enumerate(topics)]
    pick = _alternative_picker(country, [t[0] for t in topics])
    questions = gather_questions(country, slots, lambda t: research.notes(country, t, today), pick)
    if len(questions) < MIN_QUESTIONS:
        raise Rejected(f"only {len(questions)} verified question(s) for {country} {date}")
    return {
        "country": country,
        "date": date.isoformat(),
        "questions": assign_ids(f"{country}-{date.isoformat()}", translate_questions(translator, questions)),
        "translatedBy": translator.name,
        "model": MODEL,
        "verifiedBy": VERIFY_MODEL,
        "grounded": all(research.notes(country, t, today) for t in topics),  # every topic had reference notes
    }


def build_study_set(country, date, research, today, translator):
    last_error = None
    for topic in study_topic_candidates(country, date):
        try:
            notes = research.notes(country, topic, today)
            lesson = make_lesson(country, topic, notes)
            slots = [(f"Question {i + 1} of {QUESTIONS_PER_STUDY_SET}, difficulty {DIFFICULTY[i]}. Test a DIFFERENT "
                      "aspect of the topic than the other questions.", topic) for i in range(QUESTIONS_PER_STUDY_SET)]
            questions = gather_questions(country, slots, lambda t: notes, lambda t: None)
            if len(questions) < MIN_QUESTIONS:
                raise Rejected(f"only {len(questions)} verified question(s) for study set {topic[0]}")
            texts = localized_fields(translator, {"title": lesson["title"], "category": lesson["category"],
                                                  "summary": lesson["summary"]}, lesson["lesson"])
            published_at = int(dt.datetime(date.year, date.month, date.day, tzinfo=dt.timezone.utc).timestamp() * 1000)
            return {
                "country": country,
                "slug": topic[0],
                "date": date.isoformat(),
                "publishedAt": published_at,
                "reference": topic[2],
                "title": texts["title"],
                "category": texts["category"],
                "summary": texts["summary"],
                "lesson": texts["lesson"],
                "minutes": max(1, int(lesson["minutes"])),
                "questions": assign_ids(topic[0], translate_questions(translator, questions)),
                "translatedBy": translator.name,
                "model": MODEL,
                "verifiedBy": VERIFY_MODEL,
                "grounded": bool(notes),
            }
        except Rejected as e:
            last_error = e
            log(f"  [{country}] topic {topic[0]} rejected ({e}); trying the next topic")
    raise Rejected(f"no study topic passed for {country} {date}: {last_error}")


# --------------------------------------------------------------------------- firestore

class Store:
    def __init__(self, dry_run):
        self.dry_run = dry_run
        self.db = None
        if not dry_run:
            import firebase_admin
            from firebase_admin import credentials, firestore

            firebase_admin.initialize_app(credentials.ApplicationDefault())
            self.db = firestore.client()
            self._server_ts = firestore.SERVER_TIMESTAMP

    def quiz_ref(self, country, date):
        return self.db.collection("dailyQuizzes").document(country).collection("days").document(date.isoformat())

    def sets_ref(self, country):
        return self.db.collection("studySets").document(country).collection("sets")

    def quiz_exists(self, country, date):
        return not self.dry_run and self.quiz_ref(country, date).get().exists

    def get_quiz(self, country, date):
        if self.dry_run:
            return None
        snap = self.quiz_ref(country, date).get()
        return snap.to_dict() if snap.exists else None

    def get_study_for(self, country, date):
        if self.dry_run:
            return None
        for d in self.sets_ref(country).where("date", "==", date.isoformat()).limit(1).get():
            return d.to_dict()
        return None

    def latest_study_date(self, country):
        if self.dry_run:
            return None
        docs = self.sets_ref(country).order_by("publishedAt", direction="DESCENDING").limit(1).get()
        for d in docs:
            return d.get("date")
        return None

    def get_notes(self, key):
        if self.dry_run:
            return None
        snap = self.db.collection("researchNotes").document(key).get()
        if not snap.exists:
            return None
        data = snap.to_dict()
        age_days = (time.time() * 1000 - data.get("updatedAt", 0)) / 86_400_000
        return data.get("notes") if age_days < NOTES_TTL_DAYS and data.get("notes") else None

    def save_notes(self, key, notes):
        if not self.dry_run:
            self.db.collection("researchNotes").document(key).set({"notes": notes, "updatedAt": int(time.time() * 1000)})

    def save_quiz(self, doc):
        if self.dry_run:
            print(json.dumps(doc, ensure_ascii=False, indent=1))
            return
        doc = dict(doc, generatedAt=self._server_ts)
        self.quiz_ref(doc["country"], dt.date.fromisoformat(doc["date"])).set(doc)

    def save_study(self, doc):
        if self.dry_run:
            print(json.dumps(doc, ensure_ascii=False, indent=1))
            return
        doc = dict(doc, generatedAt=self._server_ts)
        self.sets_ref(doc["country"]).document(doc["slug"]).set(doc)


# --------------------------------------------------------------------------- driver

def run_job(kind, country, date, store, research, translator, today, force):
    label = f"{kind} {country} {date}"
    try:
        if kind == "quiz":
            existing = None if force else store.get_quiz(country, date)
            if existing is not None:
                if not complete_quiz_languages(existing, translator):
                    return label, "skipped (exists)"
                existing.pop("generatedAt", None)
                store.save_quiz(existing)
                return label, "ok (added missing languages)"
            store.save_quiz(build_daily_quiz(country, date, research, today, translator))
        else:
            existing = None if force else store.get_study_for(country, date)
            if existing is not None:
                if not complete_study_languages(existing, translator):
                    return label, "skipped (exists)"
                existing.pop("generatedAt", None)
                store.save_study(existing)
                return label, "ok (added missing languages)"
            store.save_study(build_study_set(country, date, research, today, translator))
        return label, "ok"
    except QuotaExhausted as e:
        return label, f"QUOTA EXHAUSTED: {e}"
    except Exception as e:  # keep going: one bad country/day must not block the rest
        return label, f"FAILED: {type(e).__name__}: {e}"


def coverage_problems(store, countries, today):
    """What the app would be missing right now. Quizzes for today/tomorrow are critical (the app
    asks for the user's local date); study sets are only a warning, the app lists the last 30."""
    critical, warnings = [], []
    if store.dry_run:
        return critical, warnings
    for country in countries:
        for offset in (0, 1):
            d = today + dt.timedelta(days=offset)
            if not store.quiz_exists(country, d):
                critical.append(f"{country}: no daily quiz for {d}")
        latest = store.latest_study_date(country)
        if latest is None or latest < (today - dt.timedelta(days=2)).isoformat():
            warnings.append(f"{country}: newest study set is {latest}")
    return critical, warnings


def _valid_localized_question(q):
    """Mirrors what the Android app requires (ContentRepository.parseQuestions)."""
    try:
        if not isinstance(q["correct_index"], (int, float)) or not 0 <= int(q["correct_index"]) <= 3:
            return False
        for lang in ("en", "tr"):
            loc = q[lang]
            if not (isinstance(loc["question"], str) and loc["question"].strip()):
                return False
            if len(loc["options"]) != 4 or not all(isinstance(o, str) and o.strip() for o in loc["options"]):
                return False
        return isinstance(q["id"], str)
    except (KeyError, TypeError):
        return False


def check_content(store, countries, today):
    """Read-only audit of what the app will actually receive. Returns the number of problems."""
    problems = 0
    for country in countries:
        for offset in (0, 1):
            day = today + dt.timedelta(days=offset)
            snap = store.quiz_ref(country, day).get()
            if not snap.exists:
                log(f"[{country}] quiz {day}: MISSING")
                problems += 1
                continue
            doc = snap.to_dict()
            questions = doc.get("questions", [])
            bad = [q.get("id") for q in questions if not _valid_localized_question(q)]
            have = [lang for lang in LANGS if questions and all(lang in q for q in questions)]
            log(f"[{country}] quiz {day}: {len(questions)} questions, invalid={len(bad)}, model={doc.get('model')}, "
                f"translatedBy={doc.get('translatedBy')}, grounded={doc.get('grounded')}, languages=en,{','.join(have)}")
            problems += bool(bad) + (len(questions) < MIN_QUESTIONS)
            if offset == 0 and questions and country in ("US", "TR"):
                q = questions[0]
                log(f"   sample EN: {q['en']['question']} | {q['en']['options']} | answer={q['correct_index']}")
                log(f"   sample TR: {q['tr']['question']} | {q['tr']['options']}")
        sets = list(store.sets_ref(country).order_by("publishedAt", direction="DESCENDING").limit(5).get())
        if not sets:
            log(f"[{country}] study sets: NONE")
            problems += 1
        for s in sets:
            d = s.to_dict()
            try:
                ok = (all(d[k]["en"].strip() and d[k]["tr"].strip() for k in ("title", "lesson", "category", "summary"))
                      and isinstance(d["minutes"], (int, float)) and isinstance(d["publishedAt"], (int, float))
                      and len(d["questions"]) >= MIN_QUESTIONS and all(_valid_localized_question(q) for q in d["questions"]))
            except (KeyError, TypeError, AttributeError):
                ok = False
            log(f"[{country}] study {d.get('date')} {s.id}: valid={ok}, lesson EN/TR chars="
                f"{len(d.get('lesson', {}).get('en', ''))}/{len(d.get('lesson', {}).get('tr', ''))}, "
                f"questions={len(d.get('questions', []))}, grounded={d.get('grounded')}")
            problems += not ok
    log(f"content check finished: {problems} problem(s)")
    return problems


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--check", action="store_true", help="read-only audit of the stored content, no generation")
    ap.add_argument("--countries", default=",".join(COUNTRY_ORDER), help="comma-separated codes")
    ap.add_argument("--days-ahead", type=int, default=2, help="also generate quizzes for the next N days")
    ap.add_argument("--days-back", type=int, default=1, help="backfill quizzes/study sets for the last N days")
    ap.add_argument("--force", action="store_true", help="regenerate even if content exists")
    ap.add_argument("--dry-run", action="store_true", help="print documents instead of writing to Firestore")
    ap.add_argument("--workers", type=int, default=3)
    args = ap.parse_args()

    countries = [c.strip().upper() for c in args.countries.split(",") if c.strip()]
    unknown = [c for c in countries if c not in CURRICULUM]
    if unknown:
        sys.exit(f"unknown country code(s): {unknown}")
    today = dt.datetime.now(dt.timezone.utc).date()
    if args.check:
        sys.exit(1 if check_content(Store(False), countries, today) else 0)
    if not (os.environ.get("GEMINI_API_KEY") or os.environ.get("GOOGLE_API_KEY")):
        sys.exit("GEMINI_API_KEY is not set")

    store = Store(args.dry_run)
    research = Research(store)
    translator = Translator(gemini_translate, log, gemini_translate_multi, fatal=(QuotaExhausted,))

    jobs = []
    for country in countries:
        for offset in range(-args.days_back, args.days_ahead + 1):
            jobs.append(("quiz", country, today + dt.timedelta(days=offset)))
        for offset in range(-args.days_back, 1):  # study sets are only published for today and backfill
            jobs.append(("study", country, today + dt.timedelta(days=offset)))

    log(f"model={MODEL} verify_model={VERIFY_MODEL} research={'on' if RESEARCH_ENABLED else 'OFF'} translator={translator.name} jobs={len(jobs)} today(UTC)={today}")
    started = time.time()
    failures, quota_hit = 0, False
    with concurrent.futures.ThreadPoolExecutor(max_workers=args.workers) as pool:
        futures = [pool.submit(run_job, k, c, d, store, research, translator, today.isoformat(), args.force) for k, c, d in jobs]
        for f in concurrent.futures.as_completed(futures):
            label, status = f.result()
            log(f"{label}: {status}")
            failures += status.startswith(("FAILED", "QUOTA"))
            quota_hit = quota_hit or status.startswith("QUOTA")

    ok_jobs = len(jobs) - failures
    log(f"done in {time.time() - started:.0f}s, {failures} job failure(s), research={'on' if research.grounding else 'OFF'}")
    log(f"Gemini requests used this run: {CALLS['requests']} (~{CALLS['requests'] / max(ok_jobs, 1):.1f} per finished job; "
        "the free tier allows 500/day for flash-lite)")
    if quota_hit:
        log("The Gemini API quota is exhausted. The free tier allows only a small number of requests per day: "
            "enable billing on the Google Cloud project behind GEMINI_API_KEY.")

    # Individual failed jobs are retried by the next run. The run only turns red when the app would
    # actually be missing today's or tomorrow's quiz, so a red run always means "act on this".
    critical, warnings = coverage_problems(store, countries, today)
    for w in warnings:
        log(f"warning: {w}")
    for c in critical:
        log(f"CRITICAL: {c}")
    sys.exit(1 if critical or (quota_hit and not store.dry_run) else 0)


if __name__ == "__main__":
    main()
