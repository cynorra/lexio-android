"""Offline tests for the quizgen pipeline (no network, no Firestore, no API key needed).

Run:  python -m unittest test_generate -v
The model and the translation engine are replaced by scripted fakes, so these tests pin down the
control flow: repair instead of reject, topic swapping, fallbacks, and what gets stored.
"""
import datetime as dt
import os
import sys
import types
import unittest

# `generate` builds a Gemini client at import time; give it a stand-in when the SDK is not installed.
os.environ.setdefault("GEMINI_API_KEY", "test")
try:
    import google.genai  # noqa: F401
except ImportError:
    google_pkg = types.ModuleType("google")
    genai_pkg = types.ModuleType("google.genai")
    genai_pkg.Client = lambda **kw: None
    google_pkg.genai = genai_pkg
    sys.modules["google"] = google_pkg
    sys.modules["google.genai"] = genai_pkg

import generate as g  # noqa: E402
import translate  # noqa: E402
from curriculum import CURRICULUM  # noqa: E402

DAY = dt.date(2026, 9, 25)


def make_question(topic, tag="a"):
    return {"topic": topic, "correct_index": 2, "question": f"Q about {topic} ({tag})?",
            "options": [f"{tag}1", f"{tag}2", f"{tag}3", f"{tag}4"], "explanation": f"Because {topic}."}


class FakeTranslator:
    name = "fake"

    def translate(self, texts, is_html=False):
        return [f"TR:{t}" for t in texts]


class FakeStore:
    dry_run = True

    def get_notes(self, key):
        return None

    def save_notes(self, key, notes):
        pass

    def save_quiz(self, doc):
        self.saved = doc

    def save_study(self, doc):
        self.saved = doc


class FakeModel:
    """Scripted stand-in for call_json / _call. `bad_topics` are rejected by the checker until
    `heal_after` writer calls have been made for them (None = never heal)."""

    def __init__(self, bad_topics=(), heal_after=None, lesson_always_bad=(), research_fails=False):
        self.bad_topics = set(bad_topics)
        self.heal_after = heal_after
        self.lesson_always_bad = set(lesson_always_bad)
        self.research_fails = research_fails
        self.writes = {}
        self.calls = []

    def call_json(self, system, user, schema, **kw):
        self.calls.append(schema)
        if schema is g.QUESTIONS_SCHEMA:
            # slots are numbered "N. label\n   Topic: <title> (anchor: ...)"
            titles = [line.split("Topic: ")[1].split(" (anchor")[0] for line in user.splitlines() if "Topic: " in line]
            for t in titles:
                self.writes[t] = self.writes.get(t, 0) + 1
            return {"questions": [make_question(t, str(self.writes[t])) for t in titles]}
        if schema is g.VERIFY_SCHEMA:
            if "LESSON:" in user:  # lesson check
                title = user.split("titled '")[1].split("'")[0]
                bad = title in self.lesson_always_bad
                return {"results": [{"index": 0, "verdict": "error" if bad else "ok",
                                     "problem": "wrong article number" if bad else ""}]}
            import json
            payload = json.loads(user.split("Questions:\n", 1)[1])
            results = []
            for item in payload:
                topic = item["topic"]
                bad = topic in self.bad_topics and (self.heal_after is None or self.writes.get(topic, 0) < self.heal_after)
                results.append({"index": item["index"], "verdict": "error" if bad else "ok",
                                "problem": "wrong year" if bad else ""})
            return {"results": results}
        if schema is g.RELEVANCE_SCHEMA:
            return {"keep": [0]}  # the first candidate is the on-topic one
        if schema is g.LESSON_SCHEMA:
            title = user.split("Write a study lesson on: ")[1].split("\n")[0]
            return {"title": title, "category": "Constitutional Law", "summary": "s", "lesson": "<p>text</p>", "minutes": 4}
        raise AssertionError("unexpected schema")

    def candidates(self, country_name, native_language, topic):  # stands in for sources.fetch_candidates
        if self.research_fails:
            raise RuntimeError("wikipedia unreachable")
        return [{"lang": "en", "title": topic[1], "url": "http://example.org/x", "text": "notes " * 200},
                {"lang": "en", "title": "Unrelated", "url": "http://example.org/y", "text": "other " * 200}]


class PipelineTests(unittest.TestCase):
    def setUp(self):
        self._orig = (g.call_json, g.sources.fetch_candidates, g.log)
        g.log = lambda msg: None

    def tearDown(self):
        g.call_json, g.sources.fetch_candidates, g.log = self._orig

    def install(self, fake):
        g.call_json = fake.call_json
        g.sources.fetch_candidates = fake.candidates
        return g.Research(FakeStore())

    def test_failing_question_is_repaired_not_dropped(self):
        topics = g.topics_for_day("US", DAY, 5)
        fake = FakeModel(bad_topics=[topics[1][1]], heal_after=2)  # second write of that topic passes
        research = self.install(fake)
        doc = g.build_daily_quiz("US", DAY, research, DAY.isoformat(), FakeTranslator())
        self.assertEqual(5, len(doc["questions"]))
        self.assertEqual(2, fake.writes[topics[1][1]])

    def test_stubborn_topic_is_swapped_for_another(self):
        topics = g.topics_for_day("US", DAY, 5)
        fake = FakeModel(bad_topics=[topics[2][1]])  # never heals
        research = self.install(fake)
        doc = g.build_daily_quiz("US", DAY, research, DAY.isoformat(), FakeTranslator())
        self.assertEqual(5, len(doc["questions"]))
        used = {q["topic"] for q in doc["questions"]}
        self.assertNotIn(topics[2][1], used)

    def test_quiz_is_rejected_when_almost_everything_fails(self):
        topics = g.topics_for_day("US", DAY, 5)
        fake = FakeModel(bad_topics=[t[1] for t in CURRICULUM["US"]])
        research = self.install(fake)
        with self.assertRaises(g.Rejected):
            g.build_daily_quiz("US", DAY, research, DAY.isoformat(), FakeTranslator())

    def test_stored_shape_has_both_languages_and_aligned_options(self):
        research = self.install(FakeModel())
        doc = g.build_daily_quiz("TR", DAY, research, DAY.isoformat(), FakeTranslator())
        q = doc["questions"][0]
        self.assertEqual({"id", "topic", "correct_index", "en", "tr"}, set(q))
        self.assertEqual([f"TR:{o}" for o in q["en"]["options"]], q["tr"]["options"])
        self.assertEqual("TR:" + q["en"]["question"], q["tr"]["question"])
        self.assertEqual("fake", doc["translatedBy"])
        self.assertTrue(q["id"].startswith("TR-2026-09-25-"))

    def test_study_set_falls_back_to_next_topic_when_lesson_cannot_be_fixed(self):
        first, second = g.study_topic_candidates("DE", DAY)[:2]
        fake = FakeModel(lesson_always_bad=[first[1]])
        research = self.install(fake)
        doc = g.build_study_set("DE", DAY, research, DAY.isoformat(), FakeTranslator())
        self.assertEqual(second[0], doc["slug"])
        self.assertEqual({"en", "tr"}, set(doc["lesson"]))
        self.assertEqual("TR:<p>text</p>", doc["lesson"]["tr"])
        self.assertEqual(5, len(doc["questions"]))

    def test_study_set_fails_only_if_every_candidate_topic_fails(self):
        cands = g.study_topic_candidates("DE", DAY)
        fake = FakeModel(lesson_always_bad=[t[1] for t in cands])
        research = self.install(fake)
        with self.assertRaises(g.Rejected):
            g.build_study_set("DE", DAY, research, DAY.isoformat(), FakeTranslator())

    def test_research_failure_degrades_gracefully(self):
        fake = FakeModel(research_fails=True)
        research = self.install(fake)
        doc = g.build_daily_quiz("FR", DAY, research, DAY.isoformat(), FakeTranslator())
        self.assertEqual(5, len(doc["questions"]))
        self.assertFalse(doc["grounded"])

    def test_research_notes_use_only_relevant_articles_and_cite_sources(self):
        research = self.install(FakeModel())
        topic = CURRICULUM["US"][0]
        notes = research.notes("US", topic, DAY.isoformat())
        self.assertIn("[Source: Wikipedia (en): " + topic[1], notes)
        self.assertNotIn("Unrelated", notes)  # filtered out by the relevance step
        doc = g.build_daily_quiz("US", DAY, research, DAY.isoformat(), FakeTranslator())
        self.assertTrue(doc["grounded"])

    def test_daily_quota_is_reported_not_swallowed(self):
        def boom(*a, **k):
            raise g.QuotaExhausted("20 requests per day")

        g.call_json = boom
        g._call = boom
        g.sources.fetch_candidates = lambda *a, **k: [{"lang": "en", "title": "T", "url": "u", "text": "x" * 700}]
        store = FakeStore()
        store.quiz_exists = lambda c, d: False
        label, status = g.run_job("quiz", "US", DAY, store, g.Research(store), FakeTranslator(), DAY.isoformat(), False)
        self.assertTrue(status.startswith("QUOTA EXHAUSTED"))


class TranslatorTests(unittest.TestCase):
    class Broken:
        name = "deepl"

        def __init__(self, status):
            self.status = status

        def translate(self, texts, is_html=False):
            raise translate.TranslationError(self.status, "quota")

    def make(self, primary):
        t = translate.Translator(lambda texts, is_html: [f"G:{x}" for x in texts], lambda m: None)
        t.primary, t._primary_ok, t.name = primary, primary is not None, primary.name if primary else "gemini"
        return t

    def test_quota_error_switches_to_fallback_for_the_rest_of_the_run(self):
        t = self.make(self.Broken(456))
        self.assertEqual(["G:a"], t.translate(["a"]))
        self.assertFalse(t._primary_ok)

    def test_transient_error_uses_fallback_but_keeps_primary(self):
        t = self.make(self.Broken(503))
        self.assertEqual(["G:a"], t.translate(["a"]))
        self.assertTrue(t._primary_ok)

    def test_without_credentials_gemini_is_the_engine(self):
        for var in ("DEEPL_API_KEY", "QUIZGEN_TRANSLATOR"):
            os.environ.pop(var, None)
        t = translate.Translator(lambda texts, is_html: ["x"] * len(texts), lambda m: None)
        self.assertEqual("gemini", t.name)


class RotationTests(unittest.TestCase):
    def test_every_topic_is_covered_within_a_cycle_and_days_differ(self):
        for country, topics in CURRICULUM.items():
            seen = set()
            for i in range(len(topics)):
                seen |= {t[0] for t in g.topics_for_day(country, DAY + dt.timedelta(days=i), 5)}
            self.assertEqual({t[0] for t in topics}, seen, country)
            self.assertNotEqual(g.topics_for_day(country, DAY, 5), g.topics_for_day(country, DAY + dt.timedelta(days=1), 5))

    def test_question_validation(self):
        good = make_question("t")
        self.assertTrue(g.structurally_valid(good))
        dup = dict(good, options=["a", "a", "b", "c"])
        self.assertFalse(g.structurally_valid(dup))
        self.assertFalse(g.structurally_valid(dict(good, correct_index=4)))


if __name__ == "__main__":
    unittest.main()
