# quizgen - automatic daily law quizzes and constitution study sets

`generate.py` writes fresh content to Firestore every day. The Android app only *reads* it.

## What it produces

| Path | Content |
|---|---|
| `dailyQuizzes/{CC}/days/{YYYY-MM-DD}` | 5 questions (one per curriculum topic), English + Turkish |
| `studySets/{CC}/sets/{slug}` | one constitution / basic-law lesson + 5 questions per day, English + Turkish |

Countries (product priority order): `US, UK, DE, FR, ES, IT, TR`. Topics live in `curriculum.py`
and are walked round-robin, so every topic comes back (freshly rewritten) each cycle.
Add topics or countries there; the app picks them up with no release.

Every question is generated, then independently fact-checked by a second model call
(correct answer unique, citations accurate, Turkish faithful to English). Failing questions are
regenerated and dropped if they still fail; a quiz with fewer than 4 verified questions is not published.

## Setup (once)

1. **Gemini key** - create one in Google AI Studio and add it as repository secret `GEMINI_API_KEY`.
2. **Firebase service account** - Firebase console -> Project settings -> Service accounts ->
   *Generate new private key*. Paste the whole JSON as repository secret `FIREBASE_SERVICE_ACCOUNT`.
3. **Firestore rules** - clients must be able to read the content but never write it (the Admin SDK
   bypasses rules). Add to your existing rules:

   ```
   match /dailyQuizzes/{cc}/days/{day} { allow read: if request.auth != null; allow write: if false; }
   match /studySets/{cc}/sets/{slug}   { allow read: if request.auth != null; allow write: if false; }
   ```
4. Run the **Daily quiz generation** workflow once by hand (Actions tab -> Run workflow) to backfill;
   after that it runs daily at 02:15 UTC.

## Run locally

```bash
pip install -r requirements.txt
export GEMINI_API_KEY=...
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

## Limits

- Content is written from the model's knowledge (no search grounding), not live sources, so it is best for stable
  constitutional law; it deliberately avoids office holders and statistics. The fact-check pass
  reduces but does not eliminate errors - spot-check new topics in Firestore before promoting the app.
- Days are UTC; the app asks for the user's local date and falls back to the latest available
  quiz if that day is missing, so a delayed run never shows an empty quiz.
