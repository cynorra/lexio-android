# quizgen - automatic daily law quizzes and constitution study sets

`generate.py` writes fresh content to Firestore every day. The Android app only *reads* it.

## What it produces

| Path | Content |
|---|---|
| `dailyQuizzes/{CC}/days/{YYYY-MM-DD}` | 5 questions (one per curriculum topic), English + Turkish, German, French, Spanish, Italian |
| `caseLibrary/{CC}/cases/{slug}` | one trial-simulation case (facts, evidence, 4 steps x 3 arguments per side, two verdicts), **English only for now**; themes in `cases.py`, one new case per country per run (`--cases N`, 0 disables) |
| `studySets/{CC}/sets/{slug}` | one constitution / basic-law lesson + 5 questions per day, English + Turkish, German, French, Spanish, Italian |

Countries (product priority order): `US, UK, DE, FR, ES, IT, TR`. Topics live in `curriculum.py`
and are walked round-robin, so every topic comes back (freshly rewritten) each cycle.
Add topics or countries there; the app picks them up with no release.

**Current focus (scheduled runs):** United States, United Kingdom and Turkey (plus their exams), translated to Turkish only.
Widen later without code changes: set the repository variable `QUIZGEN_LANGS` to `tr,de,fr,es,it`, and run the workflow with
`countries` = `US,UK,DE,FR,ES,IT,TR`. Existing DE/FR/ES/IT content stays in Firestore.

## Pipeline (per topic)

1. **Research** - free, no billing: Wikipedia excerpts (the English article plus its German / French /
   Spanish / Italian / Turkish counterpart via language links, see `sources.py`), filtered for relevance by
   one cheap model call. Notes are cached in Firestore (`researchNotes/`, 30 days), so a topic is researched
   about monthly. Set `QUIZGEN_RESEARCH=off` to skip this step.
2. **Write** - questions and lessons are written in English, only from those notes.
3. **Check** - an independent pass against the notes. Only *material* errors block (wrong answer key,
   false statement, ambiguous answer); style and nuance don't.
4. **Repair** - failing questions are rewritten with the checker's feedback; a lesson gets two targeted
   rewrites and then a conservative version. A topic that still can't be fixed is swapped for another,
   so every day gets content and one hard topic never blocks a run.
5. **Translate** - the verified English is translated to Turkish, German, French, Spanish and Italian by a translation engine, not the LLM. A language other than Turkish that fails is skipped (the app shows English) and a later run adds it: every run also completes missing languages on the quizzes/lessons it would otherwise skip. Set `QUIZGEN_LANGS=tr,de` to translate into fewer languages
   (`translate.py`). Options are translated as separate strings, so `correct_index` stays valid.

The workflow only turns red if today's or tomorrow's quiz is missing for some country, so a red run
always means "look at this".

## Setup (once)

1. **Gemini key** - create one in Google AI Studio and add it as repository secret `GEMINI_API_KEY`.
2. **Firebase service account** - Firebase console -> Project settings -> Service accounts ->
   *Generate new private key*. Paste the whole JSON as repository secret `FIREBASE_SERVICE_ACCOUNT`.
3. **Firestore rules** - clients must be able to read the content but never write it (the Admin SDK
   bypasses rules). Add to your existing rules:

   ```
   match /dailyQuizzes/{cc}/days/{day} { allow read: if request.auth != null; allow write: if false; }
   match /studySets/{cc}/sets/{slug}   { allow read: if request.auth != null; allow write: if false; }
   match /caseLibrary/{cc}/cases/{slug} { allow read: if request.auth != null; allow write: if false; }
   ```
4. Run the **Daily quiz generation** workflow once by hand (Actions tab -> Run workflow) to backfill;
   after that it runs daily at 02:15 UTC.

### Translation engine (optional but recommended)

| Engine | How | Notes |
|---|---|---|
| **DeepL** | add secret `DEEPL_API_KEY` (free keys end in `:fx`) | best quality; free tier is 500k chars/month, but a full daily run is roughly 40k chars PER LANGUAGE (about 200k for all five), so the free tier lasts a few days; the run then falls back to Gemini automatically |
| **Google Cloud Translation** | set env `QUIZGEN_TRANSLATOR=google` | uses the Firebase service account; enable "Cloud Translation API" and billing on the project |
| **Gemini** (default) | nothing to set | works out of the box but uses LLM quota |

If the chosen engine fails or runs out of quota the run falls back to Gemini, so content is never blocked.

## Run locally

```bash
pip install -r requirements.txt
export GEMINI_API_KEY=...
python -m unittest test_generate   # offline tests, no key needed
python generate.py --dry-run --countries US --days-ahead 0 --days-back 0   # prints documents, writes nothing
export GOOGLE_APPLICATION_CREDENTIALS=path/to/service-account.json
python generate.py                                                          # writes to Firestore
```

Useful flags: `--countries US,TR`, `--days-ahead N` (default 2), `--days-back N` (default 1),
`--force` (regenerate existing), `--workers N`.

## Model and cost

Content is written by `gemini-3.5-flash-lite` (override with `QUIZGEN_MODEL`) and fact-checked by the same
model at high thinking (Flash-Lite fits the free tier; `gemini-3.8-flash` is stronger but has a 20 requests/day free limit). For better independence set `QUIZGEN_VERIFY_MODEL=gemini-3.1-pro-preview`
(a preview model - Google may change or retire it). Steady state is ~7 quizzes + 7 study sets per day,
each with a generation call and one or more fact-check calls; on Flash-class pricing that is
well under a dollar a day. The first run backfills ~4 days per country. A paid API key is
recommended: free-tier rate limits are low (the script retries with backoff, but a full run may crawl).

## Law-exam prep quizzes

`exams.py` lists the exams people study for (UBE, MBE, MPRE, California Bar, SQE1, Staatsexamen, CRFPA, Spanish/Italian bar exams, HMGS, ...). Each exam gets its own daily quiz in `examQuizzes/{exam-id}/days/{date}` (5 questions, same research/write/check/translate pipeline, quizzes only, no study sets). Today and tomorrow are generated per run.

```
python generate.py                         # all countries + all exams
python generate.py --countries US          # US only, no exams (smoke test)
python generate.py --countries US --exams us-ube,tr-hmgs
python generate.py --countries US --exams none
```

Workflow input `exams` does the same (`us-ube,tr-hmgs`; empty = all exams when `countries` is empty, else none). Reference notes are shared with the jurisdiction (`US__<slug>`). An exam run costs about 3-4 Gemini requests per exam per day in steady state.

To add an exam: an entry in `EXAMS` (exams.py) and a row in the app's `model/Exams.java`.

## Manual content (Gemini chat instead of the API)

The chat app has a bigger allowance than the free API. `manual.py` writes the prompt, you paste it into the Gemini chat, save the JSON answer, and `manual.py import` validates and publishes it (no Gemini API, only Firestore):

```
python manual.py prompt quiz  --country TR --date 2026-09-28      # or --exam tr-hmgs
python manual.py prompt study --country TR --topic tr-sovereignty
python manual.py import quiz  answer.json --country TR --date 2026-09-28 [--dry-run] [--overwrite]
python manual.py import study answer.json --country TR --topic tr-sovereignty
```

The answer holds all languages (en, tr, de, fr, es, it) so nothing needs translating. English and Turkish are required; a malformed optional language is skipped with a warning. Documents are marked `model: manual`.

### One prompt for everything (bundle)

```
python manual.py bundle-prompt --date 2026-09-29 [--days 2] [--langs en,tr] [--study]   # writes prompt-bundle-<date>.txt
python manual.py bundle-import answer.json [answer2.json ...] [--dry-run] [--overwrite]
```

One prompt covers all 7 countries and all 11 exams (18 daily quizzes). If the chat cuts the answer off, write "continue"; every JSON code block from every part can go into the same file (or several files). `bundle-import` publishes each valid quiz/lesson independently, reports rejected ones by name, and skips documents that already exist unless `--overwrite`. `--langs en,tr` shrinks the answer (the app falls back to English for missing languages).

## Limits

- Content is written from the model's knowledge (no search grounding), not live sources, so it is best for stable
  constitutional law; it deliberately avoids office holders and statistics. The fact-check pass
  reduces but does not eliminate errors - spot-check new topics in Firestore before promoting the app.
- Days are UTC; the app asks for the user's local date and falls back to the latest available
  quiz if that day is missing, so a delayed run never shows an empty quiz.

## Topic pool, originality and daily discovery

- **Curriculum** = `curriculum.py` + `exams.py` + `topics_extra.py` (hand-written, merged at import) + topics the generator
  added itself (`topicPool/{key}/topics` in Firestore, slugs start with `dyn-`). US/UK/TR have 70-95 topics each.
- **Daily discovery:** every run asks the model for up to `--new-topics` (default 2, env `QUIZGEN_NEW_TOPICS`, 0 = off) NEW topics per
  country/exam. A proposal is kept only if it is not a rephrasing of an existing title and reference notes can be found for it
  (otherwise questions would be unverifiable). The pool is capped at 150 model-proposed topics per key.
- **Topic planning:** each day's quiz takes the least-asked topics (never-asked new ones first); days in one run are planned together.
- **Originality:** the writer sees the questions already asked on a topic and a rotating angle; repeats are rejected before
  the fact-check (see `find_repeat`, `DUP_RATIO`).
