"""Free, key-less reference sources for quizgen research notes (no LLM, no billing).

For each curriculum topic we search English Wikipedia, then follow the article's language link to the
country's own Wikipedia (German, French, Spanish, Italian, Turkish), where national constitutional law is
covered in far more detail than in English. The writer model then works from those excerpts instead of from
memory. Wikipedia is a secondary source, so generate.py still runs an independent fact-check, and the prompt
tells the model to prefer settled law over an excerpt that looks wrong.
"""
import json
import time
import urllib.error
import urllib.parse
import urllib.request

USER_AGENT = "LexioQuizgen/1.0 (https://github.com/cynorra/lexio-android; educational content pipeline)"

# Wikipedia language edition per country, in addition to English.
NATIVE_LANGUAGE = {"DE": "de", "FR": "fr", "ES": "es", "IT": "it", "TR": "tr"}

RESULTS_PER_QUERY = 2
EXTRACT_CHARS = 6000  # the API returns the whole article; we keep the lead and first sections
MIN_EXTRACT_CHARS = 600  # shorter pages are stubs / disambiguation pages, not useful notes


def _get(lang, params, retries=3):
    url = f"https://{lang}.wikipedia.org/w/api.php?" + urllib.parse.urlencode({**params, "format": "json"})
    for attempt in range(retries):
        req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
        try:
            with urllib.request.urlopen(req, timeout=30) as resp:
                return json.loads(resp.read().decode("utf-8"))
        except (urllib.error.URLError, TimeoutError, json.JSONDecodeError):
            if attempt == retries - 1:
                return None
            time.sleep(2 ** attempt)
    return None


def _search(lang, query):
    data = _get(lang, {"action": "query", "list": "search", "srsearch": query, "srlimit": RESULTS_PER_QUERY,
                       "srnamespace": 0})
    if not data:
        return []
    return [hit["title"] for hit in data.get("query", {}).get("search", [])]


def _extract(lang, title):
    """Plain text of the article (lead + sections), or None for stubs."""
    data = _get(lang, {"action": "query", "prop": "extracts", "explaintext": 1, "exsectionformat": "plain",
                       "redirects": 1, "titles": title})
    if not data:
        return None
    for page in data.get("query", {}).get("pages", {}).values():
        text = (page.get("extract") or "").strip()
        if len(text) >= MIN_EXTRACT_CHARS:
            return text[:EXTRACT_CHARS]
    return None


def _langlink(title, target_lang):
    """Title of the same article on another language edition, via the English article's language links."""
    data = _get("en", {"action": "query", "prop": "langlinks", "lllang": target_lang, "redirects": 1,
                       "titles": title})
    if not data:
        return None
    for page in data.get("query", {}).get("pages", {}).values():
        for link in page.get("langlinks", []):
            if link.get("lang") == target_lang:
                return link.get("*")
    return None


def _candidate(lang, title):
    text = _extract(lang, title)
    if not text:
        return None
    url = f"https://{lang}.wikipedia.org/wiki/{urllib.parse.quote(title.replace(' ', '_'))}"
    return {"lang": lang, "title": title, "url": url, "text": text}


def fetch_candidates(country_name, native_language, topic):
    """Candidate excerpts for a topic: list of {lang, title, url, text}. Never raises; [] on failure."""
    queries = [f"{country_name} {topic[1]}", f"{country_name} {topic[2]}"]
    english_titles = []
    for query in queries:
        try:
            titles = _search("en", query)
        except Exception:
            titles = []
        for title in titles:
            if title not in english_titles:
                english_titles.append(title)

    out = []
    for title in english_titles[:4]:
        try:
            cand = _candidate("en", title)
            if cand:
                out.append(cand)
            if native_language:
                native_title = _langlink(title, native_language)
                native = _candidate(native_language, native_title) if native_title else None
                if native:
                    out.append(native)
        except Exception:
            continue  # best-effort: skip an article that fails, keep the rest
    return out
