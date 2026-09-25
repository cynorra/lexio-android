#!/usr/bin/env python3
"""Generates daily law quizzes and constitution study sets and pushes them to Firestore.

Fully automatic: run it on a schedule (see .github/workflows/quizgen.yml). It is idempotent -
content that already exists for a date is skipped unless --force is passed - and it generates
a few days ahead so a failed run never leaves the app without a quiz.

Firestore layout (read-only for clients, written here with the Admin SDK):

  dailyQuizzes/{CC}/days/{YYYY-MM-DD}   -> {country, date, questions[], model, generatedAt}
  studySets/{CC}/sets/{slug}            -> {country, slug, date, publishedAt, title{}, category{},
                                            summary{}, lesson{}, reference, minutes, questions[]}

Every question is {id, topic, correct_index, en:{question, options[4], explanation}, tr:{...}}.
Every question is generated, then independently fact-checked by a second model call; questions
that fail the check are regenerated, and dropped if they still fail.

Environment:
  GEMINI_API_KEY                    required (Google AI Studio key)
  GOOGLE_APPLICATION_CREDENTIALS    path to a Firebase service-account JSON (required unless --dry-run)
  QUIZGEN_MODEL                     optional, model that writes the content (default gemini-3.8-flash)
  QUIZGEN_VERIFY_MODEL              optional, model that fact-checks it (default: same as QUIZGEN_MODEL;
                                    a stronger, different model such as gemini-3.1-pro-preview is better)
"""
import argparse
import concurrent.futures
import datetime as dt
import json
import os
import random
import re
import sys
import time

from google import genai

from curriculum import COUNTRIES, COUNTRY_ORDER, CURRICULUM

MODEL = os.environ.get("QUIZGEN_MODEL", "gemini-3.8-flash")
VERIFY_MODEL = os.environ.get("QUIZGEN_VERIFY_MODEL", MODEL)
QUESTIONS_PER_QUIZ = 5
QUESTIONS_PER_STUDY_SET = 5
MIN_QUESTIONS = 4  # a quiz with fewer surviving questions than this is rejected
MAX_ATTEMPTS = 3

client = genai.Client()  # reads GEMINI_API_KEY (or GOOGLE_API_KEY) from the environment
CALL_ATTEMPTS = 5


class QuotaExhausted(Exception):
    """The API key's daily quota is used up; retrying cannot help until it resets or billing is enabled."""


def log(msg):
    print(msg, flush=True)


# --------------------------------------------------------------------------- JSON schemas

def _loc_schema():
    return {
        "type": "object",
        "properties": {
            "question": {"type": "string"},
            "options": {"type": "array", "items": {"type": "string"}},
            "explanation": {"type": "string"},
        },
        "required": ["question", "options", "explanation"],
        "additionalProperties": False,
    }


def _question_schema():
    return {
        "type": "object",
        "properties": {
            "topic": {"type": "string"},
            "correct_index": {"type": "integer"},
            "en": _loc_schema(),
            "tr": _loc_schema(),
        },
        "required": ["topic", "correct_index", "en", "tr"],
        "additionalProperties": False,
    }


def _bilingual():
    return {
        "type": "object",
        "properties": {"en": {"type": "string"}, "tr": {"type": "string"}},
        "required": ["en", "tr"],
        "additionalProperties": False,
    }


QUIZ_SCHEMA = {
    "type": "object",
    "properties": {"questions": {"type": "array", "items": _question_schema()}},
    "required": ["questions"],
    "additionalProperties": False,
}

STUDY_SCHEMA = {
    "type": "object",
    "properties": {
        "title": _bilingual(),
        "category": _bilingual(),
        "summary": _bilingual(),
        "lesson": _bilingual(),
        "minutes": {"type": "integer"},
        "questions": {"type": "array", "items": _question_schema()},
    },
    "required": ["title", "category", "summary", "lesson", "minutes", "questions"],
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
                    "ok": {"type": "boolean"},
                    "problem": {"type": "string"},
                },
                "required": ["index", "ok", "problem"],
                "additionalProperties": False,
            },
        }
    },
    "required": ["results"],
    "additionalProperties": False,
}

# --------------------------------------------------------------------------- prompts

GENERATION_RULES = """You write accurate, unambiguous multiple-choice law questions for a legal-education app.

Rules:
- Accuracy comes first. Only ask about provisions, doctrines, and decisions you are certain about.
  If you are not certain of a detail (an article number, a year, a holding), ask about something else.
- Every question has exactly 4 options and exactly ONE clearly correct answer. The wrong options must be
  plausible but definitely wrong. Never use "all of the above" or "none of the above".
- Options are plain text: no letters, numbers or bullets. Do not put the correct answer in a predictable position.
- Test understanding (what a provision protects, what a doctrine requires, which body decides), not trivia
  that changes often (current office holders, polling, statistics).
- The explanation is 1-3 sentences, states why the answer is correct, and cites the provision, article,
  amendment or case by name.
- Provide every text in English (US spelling) under "en" and in natural, correct Turkish legal
  terminology under "tr". The two versions must be the same question with the options in the SAME order,
  so correct_index is valid for both. Keep original-language legal terms in parentheses where helpful.
- This is educational content, not legal advice."""


def _topic_lines(topics):
    return "\n".join(f"{i + 1}. {t[1]} - anchor: {t[2]}" for i, t in enumerate(topics))


def quiz_prompt(country, topics, feedback):
    meta = COUNTRIES[country]
    text = (
        f"Jurisdiction: {meta['name']} ({meta['system']}).\n"
        f"Draw on {meta['sources']}.\n\n"
        f"Write {len(topics)} questions, ONE per topic below, in this order. Mix easy, medium and one harder "
        f"question. Set each question's 'topic' to the topic title.\n\n{_topic_lines(topics)}"
    )
    if feedback:
        text += (
            "\n\nA fact-check of your previous attempt found problems. Write NEW questions that avoid them:\n"
            + "\n".join(f"- {f}" for f in feedback)
        )
    return text


def study_prompt(country, topic, feedback):
    meta = COUNTRIES[country]
    text = (
        f"Jurisdiction: {meta['name']} ({meta['system']}).\n"
        f"Draw on {meta['sources']}.\n\n"
        f"Write a study set on: {topic[1]}\nAnchor: {topic[2]}\n\n"
        "Provide:\n"
        "- title (bilingual) and category (bilingual, e.g. 'Constitutional Law').\n"
        "- summary (bilingual): 1-2 sentences for a list card.\n"
        "- lesson (bilingual): a clear 350-550 word lesson as simple HTML using only <h3>, <h4>, <p>, <ul>, <li>, "
        "<b>, <i>. Explain the rule, why it exists, its limits, and one or two landmark applications. Cite the "
        "provisions and cases you rely on.\n"
        "- minutes: estimated reading time in whole minutes.\n"
        f"- questions: exactly {QUESTIONS_PER_STUDY_SET} questions that test the lesson content, easy to hard. "
        f"Set each question's 'topic' to \"{topic[1]}\"."
    )
    if feedback:
        text += (
            "\n\nA fact-check of your previous attempt found problems. Fix them in this version:\n"
            + "\n".join(f"- {f}" for f in feedback)
        )
    return text


def verify_prompt(country, questions):
    meta = COUNTRIES[country]
    payload = [
        {
            "index": i,
            "topic": q["topic"],
            "question": q["en"]["question"],
            "options": q["en"]["options"],
            "claimed_correct_index": q["correct_index"],
            "explanation": q["en"]["explanation"],
            "turkish_question": q["tr"]["question"],
            "turkish_options": q["tr"]["options"],
            "turkish_explanation": q["tr"]["explanation"],
        }
        for i, q in enumerate(questions)
    ]
    text = (
        f"You are a meticulous legal fact-checker for {meta['name']} law.\n"
        "For each question decide whether it is safe to publish: the claimed correct option must be the one and "
        "only correct answer, every other option must be definitely wrong, all cited provisions, article numbers, "
        "years and case names must be accurate, and the question must not be ambiguous or depend on facts that are "
        "likely to have changed. The Turkish fields must be a faithful translation of the English ones with the "
        "options in the same order (so the claimed index is correct for both). If you are not sure, mark it NOT ok. For a problem, describe it in one sentence; "
        "for an ok question leave 'problem' empty.\n\n"
        f"Questions:\n{json.dumps(payload, ensure_ascii=False, indent=1)}"
    )
    return text


def lesson_verify_prompt(country, title, lesson_html):
    meta = COUNTRIES[country]
    return (
        f"You are a meticulous legal fact-checker for {meta['name']} law. Review this lesson titled '{title}'. "
        "Return a single result with index 0: ok=true only if every legal statement, article number, year and "
        "case name is accurate and nothing material is misleading; otherwise ok=false with a one-sentence problem "
        f"naming the error.\n\n{lesson_html[:12000]}"
    )


# --------------------------------------------------------------------------- model calls

def call_json(system, user, schema, thinking="medium", max_tokens=16000, model=None):
    """One structured-output call. Returns the parsed JSON object.

    Transient failures (rate limits, overload, truncated or malformed JSON) are retried with
    backoff; a call that keeps failing raises so the job is reported as failed instead of
    publishing something half-formed.
    """
    last_error = None
    for attempt in range(CALL_ATTEMPTS):
        try:
            interaction = client.interactions.create(
                model=model or MODEL,
                system_instruction=system,
                input=user,
                generation_config={
                    "temperature": 0.7,
                    "max_output_tokens": max_tokens,
                    "thinking_level": thinking,
                },
                response_format={"type": "text", "mime_type": "application/json", "schema": schema},
                store=False,  # stateless: nothing here needs to be kept on Google's side
            )
            text = interaction.output_text
            if not text:
                raise ValueError("empty response (blocked or no output)")
            return json.loads(text)  # truncated output fails here and is retried
        except Exception as e:  # SDK error classes vary by version; treat all as retryable
            last_error = e
            message = str(e)
            if re.search(r"per day|PerDay", message, re.IGNORECASE):
                raise QuotaExhausted(message[:300]) from e
            delay = min(2 ** attempt * 3 + random.uniform(0, 2), 90)
            hint = re.search(r"retry in (\d+(?:\.\d+)?)s", message)  # the API says how long to wait
            if hint:
                delay = min(float(hint.group(1)) + 2, 120)
            log(f"  model call failed ({type(e).__name__}: {message[:200]}); retry {attempt + 1}/{CALL_ATTEMPTS} in {delay:.0f}s")
            time.sleep(delay)
    raise RuntimeError(f"model call failed after {CALL_ATTEMPTS} attempts: {last_error}")


def fact_check(country, questions):
    """Returns {index: problem} for every question that failed the independent check."""
    data = call_json(
        "You verify legal exam questions. Be strict.",
        verify_prompt(country, questions),
        VERIFY_SCHEMA,
        thinking="high",
        max_tokens=8000,
        model=VERIFY_MODEL,
    )
    failed = {}
    seen = set()
    for r in data["results"]:
        seen.add(r["index"])
        if not r["ok"]:
            failed[r["index"]] = r["problem"] or "unspecified problem"
    for i in range(len(questions)):
        if i not in seen:  # a question the checker never confirmed is not published
            failed[i] = "not confirmed by fact-check"
    return failed


def structurally_valid(q):
    try:
        if not isinstance(q["correct_index"], int) or not 0 <= q["correct_index"] <= 3:
            return False
        for lang in ("en", "tr"):
            loc = q[lang]
            if len(loc["options"]) != 4 or len(set(o.strip().lower() for o in loc["options"])) != 4:
                return False
            if not loc["question"].strip() or not loc["explanation"].strip():
                return False
            if any(not o.strip() for o in loc["options"]):
                return False
        return True
    except (KeyError, TypeError, AttributeError):
        return False


def gather_questions(country, produce, wanted):
    """Generate + fact-check until enough questions survive. `produce(feedback)` returns a list."""
    kept = []
    feedback = []
    for attempt in range(1, MAX_ATTEMPTS + 1):
        candidates = [q for q in produce(feedback) if structurally_valid(q)]
        if not candidates:
            feedback = ["The previous output had malformed questions; follow the format exactly."]
            continue
        failed = fact_check(country, candidates)
        kept += [q for i, q in enumerate(candidates) if i not in failed]
        feedback = list(failed.values())
        if len(kept) >= wanted or not feedback:
            break
        log(f"  [{country}] attempt {attempt}: {len(failed)} question(s) failed fact-check")
    return kept[:wanted]


# --------------------------------------------------------------------------- content builders

def day_number(date):
    return (date - dt.date(2026, 1, 1)).days


def topics_for_day(country, date, count):
    topics = CURRICULUM[country]
    base = day_number(date) * count
    return [topics[(base + k) % len(topics)] for k in range(count)]


def study_topic_for_day(country, date):
    topics = CURRICULUM[country]
    # Offset so the study topic of the day is not one of the day's quiz topics.
    return topics[(day_number(date) * 3 + 1) % len(topics)]


def assign_ids(prefix, questions):
    for i, q in enumerate(questions):
        q["id"] = f"{prefix}-{i + 1}"
    return questions


def build_daily_quiz(country, date):
    topics = topics_for_day(country, date, QUESTIONS_PER_QUIZ)

    def produce(feedback):
        return call_json(GENERATION_RULES, quiz_prompt(country, topics, feedback), QUIZ_SCHEMA)["questions"]

    questions = gather_questions(country, produce, QUESTIONS_PER_QUIZ)
    if len(questions) < MIN_QUESTIONS:
        raise RuntimeError(f"only {len(questions)} verified question(s) for {country} {date}")
    return {
        "country": country,
        "date": date.isoformat(),
        "questions": assign_ids(f"{country}-{date.isoformat()}", questions),
        "model": MODEL,
        "verifiedBy": VERIFY_MODEL,
    }


def build_study_set(country, date):
    topic = study_topic_for_day(country, date)
    result = {}

    def produce(feedback):
        data = call_json(GENERATION_RULES, study_prompt(country, topic, feedback), STUDY_SCHEMA, max_tokens=20000)
        result.update({k: v for k, v in data.items() if k != "questions"})
        return data["questions"]

    questions = gather_questions(country, produce, QUESTIONS_PER_STUDY_SET)
    if len(questions) < MIN_QUESTIONS:
        raise RuntimeError(f"only {len(questions)} verified question(s) for study set {topic[0]}")

    # The lesson itself is fact-checked too; a bad lesson is regenerated once, then the run fails
    # loudly rather than publishing something wrong.
    for attempt in (1, 2):
        check = call_json(
            "You verify legal teaching material. Be strict.",
            lesson_verify_prompt(country, result["title"]["en"], result["lesson"]["en"]),
            VERIFY_SCHEMA,
            thinking="high",
            max_tokens=4000,
            model=VERIFY_MODEL,
        )["results"]
        if check and all(r["ok"] for r in check):
            break
        problems = [r["problem"] for r in check if not r["ok"]]
        if attempt == 2:
            raise RuntimeError(f"lesson for {topic[0]} failed fact-check: {problems}")
        log(f"  [{country}] lesson for {topic[0]} failed fact-check, regenerating: {problems}")
        data = call_json(GENERATION_RULES, study_prompt(country, topic, problems), STUDY_SCHEMA, max_tokens=20000)
        result.update({k: v for k, v in data.items() if k != "questions"})

    published_at = int(dt.datetime(date.year, date.month, date.day, tzinfo=dt.timezone.utc).timestamp() * 1000)
    return {
        "country": country,
        "slug": topic[0],
        "date": date.isoformat(),
        "publishedAt": published_at,
        "reference": topic[2],
        "title": result["title"],
        "category": result["category"],
        "summary": result["summary"],
        "lesson": result["lesson"],
        "minutes": max(1, int(result["minutes"])),
        "questions": assign_ids(topic[0], questions),
        "model": MODEL,
    }


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

    def study_ref(self, country, slug):
        return self.db.collection("studySets").document(country).collection("sets").document(slug)

    def quiz_exists(self, country, date):
        return not self.dry_run and self.quiz_ref(country, date).get().exists

    def study_is_current(self, country, date):
        topic = study_topic_for_day(country, date)
        if self.dry_run:
            return False
        snap = self.study_ref(country, topic[0]).get()
        return snap.exists and snap.get("date") == date.isoformat()

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
        self.study_ref(doc["country"], doc["slug"]).set(doc)


# --------------------------------------------------------------------------- driver

def run_job(kind, country, date, store, force):
    label = f"{kind} {country} {date}"
    try:
        if kind == "quiz":
            if not force and store.quiz_exists(country, date):
                return label, "skipped (exists)"
            store.save_quiz(build_daily_quiz(country, date))
        else:
            if not force and store.study_is_current(country, date):
                return label, "skipped (exists)"
            store.save_study(build_study_set(country, date))
        return label, "ok"
    except QuotaExhausted as e:
        return label, f"QUOTA EXHAUSTED: {e}"
    except Exception as e:  # keep going: one bad country/day must not block the rest
        return label, f"FAILED: {type(e).__name__}: {e}"


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
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
    if not (os.environ.get("GEMINI_API_KEY") or os.environ.get("GOOGLE_API_KEY")):
        sys.exit("GEMINI_API_KEY is not set")

    today = dt.datetime.now(dt.timezone.utc).date()
    store = Store(args.dry_run)

    jobs = []
    for country in countries:
        for offset in range(-args.days_back, args.days_ahead + 1):
            jobs.append(("quiz", country, today + dt.timedelta(days=offset)))
        for offset in range(-args.days_back, 1):  # study sets are only published for today and backfill
            jobs.append(("study", country, today + dt.timedelta(days=offset)))

    log(f"model={MODEL} verify_model={VERIFY_MODEL} jobs={len(jobs)} today(UTC)={today}")
    started = time.time()
    failures = 0
    quota_hit = False
    with concurrent.futures.ThreadPoolExecutor(max_workers=args.workers) as pool:
        futures = [pool.submit(run_job, k, c, d, store, args.force) for k, c, d in jobs]
        for f in concurrent.futures.as_completed(futures):
            label, status = f.result()
            log(f"{label}: {status}")
            failures += status.startswith(("FAILED", "QUOTA"))
            quota_hit = quota_hit or status.startswith("QUOTA")

    log(f"done in {time.time() - started:.0f}s, {failures} failure(s)")
    if quota_hit:
        log("The Gemini API quota is exhausted. The free tier allows only ~20 requests/day, far below what "
            "one run needs: enable billing on the Google Cloud project behind GEMINI_API_KEY.")
    # A non-zero exit marks the workflow run red so failures are noticed; successfully generated
    # content has already been written, and the next run retries whatever is still missing.
    sys.exit(1 if failures else 0)


if __name__ == "__main__":
    main()
