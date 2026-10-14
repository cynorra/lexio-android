"""Offline tests for the trial-simulation case generator (no network, no Firestore)."""
import os as _os
_os.environ.setdefault("QUIZGEN_LANGS", "tr,de,fr,es,it")  # tests exercise the multi-language paths
import os
import sys
import types
import unittest

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
from cases import CASE_THEMES  # noqa: E402
from curriculum import COUNTRY_ORDER  # noqa: E402


def choice(tag):
    return {"text": f"{tag} argument text", "response": f"Opposing side answers {tag}"}


def good_case():
    step = lambda n: {"step_name": f"Step {n}", "prompt": f"Prompt number {n}",
                      "defense": [choice("d1"), choice("d2"), choice("d3")],
                      "prosecution": [choice("p1"), choice("p2"), choice("p3")]}
    return {
        "title": "A Fictional Dispute", "preview": "Was the search lawful?", "difficulty": "Medium",
        "duration_minutes": 6, "ref": "Fourth Amendment", "facts": "Facts of the case " * 8,
        "evidence": ["Exhibit A", "Witness statement", "Report"],
        "steps": [step(i) for i in range(g.CASE_STEPS)],
        "verdict_defense_win": "COURT RULING: the evidence is excluded.",
        "verdict_defense_lose": "COURT RULING: the search was lawful.",
    }


class CaseTests(unittest.TestCase):
    def test_every_country_has_unique_themes(self):
        for country in COUNTRY_ORDER:
            slugs = [t[0] for t in CASE_THEMES[country]]
            self.assertGreaterEqual(len(slugs), 8, country)
            self.assertEqual(len(slugs), len(set(slugs)), country)
            self.assertTrue(all(t[0].startswith("case-" + country.lower()) for t in CASE_THEMES[country]))

    def test_good_case_has_no_structure_problems(self):
        self.assertEqual([], g.case_structure_problems(good_case()))

    def test_structure_problems_are_reported(self):
        c = good_case()
        c["steps"] = c["steps"][:2]
        c["verdict_defense_win"] = "The court agrees."
        c["steps"][0]["defense"] = c["steps"][0]["defense"][:2]
        problems = " | ".join(g.case_structure_problems(c))
        self.assertIn("exactly 4 steps", problems)
        self.assertIn("COURT RULING", problems)
        self.assertIn("3 complete defense", problems)

    def test_doc_scores_are_complete_and_shuffled_deterministically(self):
        theme = CASE_THEMES["US"][0]
        doc = g.to_case_doc("US", theme, good_case(), "2026-10-14")
        for step in doc["trialSteps"]:
            for side in ("defenseChoices", "prosecutionChoices"):
                self.assertEqual([5, 15, 25], sorted(c["scoreEffect"] for c in step[side]))
        again = g.to_case_doc("US", theme, good_case(), "2026-10-14")
        self.assertEqual(doc["trialSteps"], again["trialSteps"])
        self.assertEqual("en", doc["language"])
        self.assertEqual("US", doc["jurisdiction"])
        self.assertEqual("~6 min", doc["duration"])

    def test_case_ids_are_stable_and_above_bundled_ids(self):
        a = g.case_id("US", "case-us-wire-fraud")
        self.assertEqual(a, g.case_id("US", "case-us-wire-fraud"))
        self.assertNotEqual(a, g.case_id("UK", "case-us-wire-fraud"))
        self.assertGreaterEqual(a, 1_000_000)
        self.assertLess(a, 2 ** 31)

    def test_next_themes_skip_published_ones(self):
        first = CASE_THEMES["DE"][0][0]
        self.assertEqual(CASE_THEMES["DE"][1][0], g.next_case_themes("DE", {first}, 1)[0][0])
        self.assertEqual([], g.next_case_themes("DE", {t[0] for t in CASE_THEMES["DE"]}, 1))

    def test_make_case_repairs_after_checker_error(self):
        calls = []
        replies = [good_case(), {"results": [{"index": 0, "verdict": "error", "problem": "wrong year"}]},
                   good_case(), {"results": [{"index": 0, "verdict": "ok", "problem": ""}]}]
        original = g.call_json
        g.call_json = lambda *a, **k: (calls.append(1), replies.pop(0))[1]
        try:
            self.assertEqual("A Fictional Dispute", g.make_case("US", CASE_THEMES["US"][0], "notes")["title"])
        finally:
            g.call_json = original
        self.assertEqual(4, len(calls))

    def test_make_case_gives_up_after_repeated_errors(self):
        bad = {"results": [{"index": 0, "verdict": "error", "problem": "false statement"}]}
        replies = [good_case(), bad] * g.CASE_ATTEMPTS
        original = g.call_json
        g.call_json = lambda *a, **k: replies.pop(0)
        try:
            with self.assertRaises(g.Rejected):
                g.make_case("US", CASE_THEMES["US"][0], "notes")
        finally:
            g.call_json = original


if __name__ == "__main__":
    unittest.main()
