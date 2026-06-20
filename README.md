# Lexio — Deploy Rehberi

## Yerel Geliştirme

```bash
npm install
cp .env.local.example .env.local
# .env.local içine Gemini API key'ini yaz
npm run dev
# http://localhost:3000
```

## Gemini API Key Alma (Ücretsiz)

1. https://aistudio.google.com/apikey adresine git
2. "Create API key" tıkla
3. Key'i kopyala, .env.local'e yapıştır

**Ücretsiz limit:** Günde 1500 istek (300+ simülasyon) — kart bilgisi gerekmez.

## Vercel Deploy (5 dakika)

1. GitHub'a push et:
```bash
git init
git add .
git commit -m "Lexio MVP"
git remote add origin https://github.com/KULLANICI/lexio.git
git push -u origin main
```

2. https://vercel.com adresine git
3. "New Project" → GitHub repo'nu seç → Import
4. **Environment Variables** bölümüne ekle:
   - Key: `GEMINI_API_KEY`
   - Value: `AIza...` (senin key'in)
5. "Deploy" tıkla
6. `lexio.vercel.app` URL'in hazır!

## Güvenlik

- API key hiçbir zaman frontend'e gönderilmez
- Sadece Vercel sunucusu key'i görür
- Kullanıcı tarayıcısından key erişilemez

## Sayfalar

- `/` — Landing
- `/vakalar` — Vaka listesi
- `/vaka/[id]` — Vaka detay + rol seçimi
- `/simulasyon/[id]?rol=savunma` — Simülasyon
- `/api/chat` — Gemini proxy (backend, güvenli)
