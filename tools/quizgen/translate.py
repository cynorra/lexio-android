"""English -> Turkish translation for quizgen, done by a dedicated translation engine instead of the LLM.

Why: the LLM only has to write and fact-check ONE language, and translation of already-verified
text is a solved, cheap, deterministic problem. Options are translated as separate strings, so
their order (and therefore correct_index) is preserved by construction.

Provider is chosen from the environment:
  DEEPL_API_KEY                  -> DeepL (free keys end in ":fx"; 500k chars/month free). Best Turkish quality.
  QUIZGEN_TRANSLATOR=google      -> Google Cloud Translation (Basic, v2) using the Firebase service account;
                                    needs the "Cloud Translation API" enabled on the project (billing required).
  neither                        -> the Gemini model itself (works out of the box, uses a little quota).
If the chosen engine fails or runs out of quota, the run falls back to Gemini so a quota never blocks content.
"""
import html
import json
import os
import time
import urllib.error
import urllib.request

SOURCE, TARGET = "en", "tr"
BATCH = 40  # DeepL accepts up to 50 texts per request


def _post(url, payload, headers, retries=4):
    body = json.dumps(payload).encode("utf-8")
    for attempt in range(retries):
        req = urllib.request.Request(url, data=body, headers={"Content-Type": "application/json", **headers})
        try:
            with urllib.request.urlopen(req, timeout=60) as resp:
                return json.loads(resp.read().decode("utf-8"))
        except urllib.error.HTTPError as e:
            detail = e.read().decode("utf-8", "replace")[:300]
            if e.code in (429, 500, 502, 503, 504) and attempt < retries - 1:
                time.sleep(2 ** attempt * 2)
                continue
            raise TranslationError(e.code, detail) from e
        except urllib.error.URLError as e:
            if attempt < retries - 1:
                time.sleep(2 ** attempt * 2)
                continue
            raise TranslationError(0, str(e)) from e


class TranslationError(Exception):
    def __init__(self, status, detail):
        super().__init__(f"HTTP {status}: {detail}")
        self.status = status


class DeepL:
    name = "deepl"

    def __init__(self, key):
        self.key = key
        host = "api-free.deepl.com" if key.endswith(":fx") else "api.deepl.com"
        self.url = f"https://{host}/v2/translate"

    def translate(self, texts, is_html=False):
        out = []
        for i in range(0, len(texts), BATCH):
            chunk = texts[i:i + BATCH]
            payload = {"text": chunk, "source_lang": SOURCE.upper(), "target_lang": TARGET.upper(),
                       "formality": "prefer_more"}
            if is_html:
                payload["tag_handling"] = "html"
            data = _post(self.url, payload, {"Authorization": f"DeepL-Auth-Key {self.key}"})
            out += [t["text"] for t in data["translations"]]
        return out


class GoogleCloud:
    name = "google"
    URL = "https://translation.googleapis.com/language/translate/v2"

    def __init__(self):
        import google.auth
        import google.auth.transport.requests

        self._request = google.auth.transport.requests.Request()
        self._creds, _ = google.auth.default(scopes=["https://www.googleapis.com/auth/cloud-platform"])

    def _token(self):
        if not self._creds.valid:
            self._creds.refresh(self._request)
        return self._creds.token

    def translate(self, texts, is_html=False):
        out = []
        for i in range(0, len(texts), BATCH):
            chunk = texts[i:i + BATCH]
            data = _post(self.URL, {"q": chunk, "source": SOURCE, "target": TARGET,
                                    "format": "html" if is_html else "text"},
                         {"Authorization": f"Bearer {self._token()}"})
            # The API returns HTML entities (&#39; ...) even for plain text.
            out += [html.unescape(t["translatedText"]) for t in data["data"]["translations"]]
        return out


class Translator:
    """Primary engine with a Gemini fallback. `gemini_translate(texts, is_html)` is injected."""

    def __init__(self, gemini_translate, log=print):
        self._gemini = gemini_translate
        self._log = log
        self.primary = None
        deepl_key = os.environ.get("DEEPL_API_KEY")
        if deepl_key:
            self.primary = DeepL(deepl_key)
        elif os.environ.get("QUIZGEN_TRANSLATOR", "").lower() == "google":
            try:
                self.primary = GoogleCloud()
            except Exception as e:  # missing credentials etc.
                log(f"Google Cloud Translation unavailable ({e}); using Gemini for translation")
        self.name = self.primary.name if self.primary else "gemini"
        self._primary_ok = self.primary is not None

    def translate(self, texts, is_html=False):
        if not texts:
            return []
        if self._primary_ok:
            try:
                result = self.primary.translate(texts, is_html)
                if len(result) == len(texts) and all(isinstance(t, str) and t.strip() for t in result):
                    return result
                raise TranslationError(0, "engine returned a malformed result")
            except TranslationError as e:
                # 456 = DeepL quota exceeded, 403 = key/API not enabled: don't keep hammering the engine.
                if e.status in (403, 456):
                    self._primary_ok = False
                self._log(f"  translation engine '{self.primary.name}' failed ({e}); using Gemini for this text"
                          + ("" if self._primary_ok else " and for the rest of the run"))
        return self._gemini(texts, is_html)
