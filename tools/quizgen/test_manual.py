import datetime as dt
import json
import os
import sys
import unittest

sys.path.insert(0, os.path.dirname(__file__))
os.environ.setdefault("GEMINI_API_KEY", "test")

import manual as m  # noqa: E402

DAY = dt.date(2026, 9, 28)


def loc(tag):
    return {"question": f"Q {tag}?", "options": [f"a{tag}", f"b{tag}", f"c{tag}", f"d{tag}"], "explanation": f"E {tag}"}


def question(i=0, langs=m.ALL_LANGS):
    q = {"topic": f"t{i}", "correct_index": i % 4}
    for lang in langs:
        q[lang] = loc(f"{i}{lang}")
    return q


def quiz_answer(**kw):
    return {"questions": [question(i, **kw) for i in range(5)]}


def study_answer():
    texts = {lang: f"<h3>x</h3><p>{lang}</p>" for lang in m.ALL_LANGS}
    plain = {lang: lang for lang in m.ALL_LANGS}
    return {"title": plain, "category": plain, "summary": plain, "lesson": texts, "minutes": 4,
            "questions": [question(i) for i in range(5)]}


class PromptTests(unittest.TestCase):
    def test_quiz_prompt_names_the_days_topics_and_languages(self):
        text = m.quiz_prompt("US", DAY)
        for topic in m.g.topics_for_day("US", DAY, 5):
            self.assertIn(topic[1], text)
        for lang in m.ALL_LANGS:
            self.assertIn(f'"{lang}"', text)

    def test_exam_and_study_prompts(self):
        key = m.exam_key("tr-hmgs")
        self.assertIn("HMGS", m.quiz_prompt(key, DAY))
        self.assertIn("<h3>The rule</h3>", m.study_prompt("TR", m.CURRICULUM["TR"][0]))


class ImportTests(unittest.TestCase):
    def test_valid_quiz_gets_the_pipeline_shape(self):
        doc = m.build_quiz_doc(quiz_answer(), "TR", DAY)
        self.assertEqual("TR", doc["country"])
        self.assertEqual("2026-09-28", doc["date"])
        self.assertEqual("TR-2026-09-28-1", doc["questions"][0]["id"])
        self.assertEqual("manual", doc["model"])
        self.assertNotIn("exam", doc)

    def test_exam_quiz_carries_its_exam_id(self):
        doc = m.build_quiz_doc(quiz_answer(), m.exam_key("us-ube"), DAY)
        self.assertEqual("us-ube", doc["exam"])
        self.assertEqual("US", doc["country"])

    def test_optional_language_that_is_malformed_is_skipped_required_one_is_not(self):
        data = quiz_answer()
        data["questions"][2]["de"]["options"] = ["only", "two"]
        warnings = []
        doc = m.build_quiz_doc(data, "TR", DAY, warnings.append)
        self.assertNotIn("de", doc["questions"][2])
        self.assertEqual(1, len(warnings))
        data["questions"][0]["tr"]["options"] = ["x", "x", "y", "z"]  # duplicate option
        with self.assertRaises(m.Invalid):
            m.build_quiz_doc(data, "TR", DAY)

    def test_wrong_count_or_index_is_rejected(self):
        data = quiz_answer()
        data["questions"].pop()
        with self.assertRaises(m.Invalid):
            m.build_quiz_doc(data, "TR", DAY)
        data = quiz_answer()
        data["questions"][0]["correct_index"] = 4
        with self.assertRaises(m.Invalid):
            m.build_quiz_doc(data, "TR", DAY)

    def test_study_set_and_html_check(self):
        topic = m.CURRICULUM["TR"][0]
        doc = m.build_study_doc(study_answer(), "TR", topic, DAY)
        self.assertEqual(topic[0], doc["slug"])
        self.assertEqual(5, len(doc["questions"]))
        self.assertEqual("TR", doc["country"])
        bad = study_answer()
        bad["lesson"]["en"] = "<script>alert(1)</script>"
        with self.assertRaises(m.Invalid):
            m.build_study_doc(bad, "TR", topic, DAY)
        with self.assertRaises(m.Invalid):
            m.build_study_doc(study_answer(), m.exam_key("us-ube"), topic, DAY)

    def test_load_json_accepts_fenced_answers_with_chatter(self):
        raw = "Here you go:\n```json\n" + json.dumps(quiz_answer()) + "\n```\nHope it helps!"
        self.assertEqual(5, len(m.load_json(raw)["questions"]))
        with self.assertRaises(m.Invalid):
            m.load_json("no json here")


class BundleTests(unittest.TestCase):
    def test_prompt_lists_every_country_and_exam_once(self):
        text = m.bundle_prompt(DAY, 1, list(m.ALL_LANGS), with_study=False)
        for key in m.all_targets():
            self.assertIn(f'target "{m.target_id(key)}" | date 2026-09-28', text)
        self.assertIn("18 quizzes", text)
        self.assertIn("PART 1 DONE", text)

    def test_prompt_with_days_langs_and_study(self):
        text = m.bundle_prompt(DAY, 2, ["en", "tr"], with_study=True)
        self.assertIn("36 quizzes", text)
        self.assertNotIn('"de":', text)
        self.assertIn("studySets", text)

    def test_answer_split_over_parts_is_merged_and_bad_items_do_not_block_good_ones(self):
        import tempfile
        part1 = {"quizzes": [{"target": "US", "date": "2026-09-28", "questions": [question(i) for i in range(5)]},
                             {"target": "exam:us-ube", "date": "2026-09-28", "questions": [question(i) for i in range(5)]}]}
        broken = {"target": "TR", "date": "2026-09-28", "questions": [question(i) for i in range(4)]}
        part2 = {"quizzes": [broken, {"target": "exam:nope", "date": "2026-09-28", "questions": []}]}
        study = dict(study_answer(), target="TR", topic=m.CURRICULUM["TR"][0][0])
        fence = "`" * 3
        text = chr(10).join(["Here is part 1", fence + "json", json.dumps(part1), fence, "PART 1 DONE",
                          fence + "json", json.dumps(part2), fence,
                          fence + "json", json.dumps({"studySets": [study]}), fence])
        with tempfile.TemporaryDirectory() as d:
            path = os.path.join(d, "answer.txt")
            open(path, "w", encoding="utf-8").write(text)
            quizzes, studies, problems = m.load_bundle_files([path])
        docs, rejected = m.bundle_docs(quizzes, studies, lambda msg: None)
        self.assertEqual([], problems)
        self.assertEqual([("quiz", "US"), ("quiz", "exam-us-ube"), ("study", "TR")], [(k, key) for k, key, _ in docs])
        self.assertEqual(2, len(rejected))
        exam_doc = docs[1][2]
        self.assertEqual("us-ube", exam_doc["exam"])
        self.assertEqual("US", exam_doc["country"])

    def test_duplicate_target_and_reduced_languages(self):
        item = {"target": "US", "date": "2026-09-28", "questions": [question(i, langs=("en", "tr")) for i in range(5)]}
        docs, rejected = m.bundle_docs([item, item], [], lambda msg: None, langs=["en", "tr"])
        self.assertEqual(1, len(docs))
        self.assertEqual(1, len(rejected))


if __name__ == "__main__":
    unittest.main()


class GeneralPromptTests(unittest.TestCase):
    def test_general_prompt_has_no_fixed_date_and_lists_every_topic_list(self):
        text = m.general_prompt(list(m.ALL_LANGS))
        self.assertNotIn("| date 20", text)
        self.assertIn("Date: 2026-10-05", text)
        for key in m.all_targets():
            self.assertIn(f'TARGET "{m.target_id(key)}"', text)
        self.assertIn(m.CURRICULUM["US"][0][1], text)
        self.assertIn("Reply \"Ready\"", text)
