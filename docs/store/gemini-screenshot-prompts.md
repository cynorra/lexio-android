# Lexio – Play Store görselleri: premium Gemini istemleri (v2)

Hazırlanma tarihi: 2026-09-26. Hedef: Apple / Linear / Arc / Notion seviyesinde "ürün lansmanı" görselleri. Düz
"telefon + başlık" değil; **derinlik, ışık, katmanlı kartlar, sinematik kompozisyon**.
İstemler uygulamanın gerçek ekranlarına göre yazıldı (`layout_*.xml`, `strings.xml`); gerçek ekran görüntüsü her istemde **referans** olarak eklenir.

---

## 0. Neden ilk sonuçlar "ilkel" çıktı ve ne değişiyor

| Sorun | Çözüm (bu sürümde) |
|---|---|
| Düz renk arka plan, telefon ortada, başlık üstte: şablon görünümü | Her ekran için **ayrı sahne**: ışık, derinlik, ekrandan taşan kartlar |
| Aynı kompozisyon 8 kez tekrarlandı | Her görsel farklı bir "ürün hikâyesi" anlatıyor, ama aynı renk/ışık dili |
| Yazı kalitesi, aksanlı harfler bozuk | **İki aşamalı yöntem**: sahneyi Gemini yapar, başlığı sen bir kez düzgün eklersin (§2B) veya Gemini'ye kısa başlık yazdırıp kontrol edersin |
| Uydurma arayüz | Gerçek ekran görüntüsü referansı + "pixel-accurate, do not redraw" kuralı |
| Düşük çözünürlük | 2K/4K iste, sonra 1080×1920'ye küçült (küçültme keskinlik verir) |

## 1. Ana sanat yönetimi (her sohbetin başında BİR KEZ gönder)

```
You are the art director and image generator for the Google Play Store launch visuals of "Lexio", a premium law-study app
(law exam prep, daily law quizzes, constitutional-law lessons, courtroom practice). The result must look like the launch
imagery of a top-tier 2026 product (think Apple, Linear, Arc, Notion): confident, calm, expensive, editorial.

BRAND
- Signature color: warm ember orange #F97316 (used sparingly as light, glow and one accent word - never as a flat fill).
- Deep ink #09090B / #111114 and graphite #18181B as the dark world; warm ivory #FFF8F0 and pure white #FFFFFF as the light world.
- Secondary accent for success: emerald #10B981; for rank/prestige: soft gold #E8C27A.
- Typography: a modern grotesque / geometric sans (Inter Display, SF Pro Display, Geist style), headline in semibold with tight
  tracking (-2%), large and airy; supporting text medium weight, 60% opacity. One key word of the headline in an orange-to-amber gradient.

VISUAL LANGUAGE (apply to every image)
- Premium product photography feel rendered in 3D: a modern Android flagship phone with thin titanium-like bezels, subtle
  reflections, correct perspective and a soft contact shadow. The phone is the hero.
- Depth: 2-4 UI cards or chips (taken from the real app screen) float slightly in front of and behind the phone with
  parallax, soft shadows and a very slight blur on the far ones. They are simplified copies of real UI, always legible.
- Light: one large soft key light from the top-left, a warm orange rim light on the phone edge, gentle volumetric haze,
  a faint bloom around bright UI elements. No harsh flat gradients; use smooth mesh gradients with fine film grain (2%).
- Materials: frosted glass panels (background blur, 1px white 12% border), matte dark surfaces, brushed metal.
- Composition: generous negative space, strong diagonal or centered-tilt composition (phone rotated 6-12 degrees or straight, per image),
  headline in the top third or left third, never touching the edges (>= 6% margin).
- Restraint: at most 3 colors on screen besides the app UI. No clutter, no stock clip-art (no gavel/scales collages), no faces,
  no watermarks, no logos of exam bodies or organizations, no rating stars, no "#1", no prices, no lorem ipsum.

TEXT RULES
- Only write the exact text I give you in quotes. Spell every character exactly, including accents (ö ü ç ı ş ğ é è à ñ ä). Do not invent extra words.
- Never modify the text inside the attached app screenshot: keep it pixel-accurate, sharp and readable. Do not redraw, restyle or translate it.

OUTPUT
- One PNG per request, 9:16 portrait, highest resolution available (aim for 2160x3840). Confirm "ready" and wait for my first request.
```

## 2. Yöntem

### A) Tek adım (hızlı)
Ekran görüntüsünü ekle → §4'teki ekran istemini gönder. Sonuç yazıyı doğru çıkarırsa bitti.

### B) İki adım (en premium ve hatasız; önerilen)
1. **Sahne:** İstemin sonundaki `SCENE ONLY` satırını ekle: Gemini başlıksız, sadece telefon + ekran görüntüsü + kartlar + ışık üretir.
2. **Başlık:** Görseli Canva/Figma/Photoshop'a koy, başlığı **Inter Display Semibold** ile ekle (turuncu vurgulu kelime için #F97316→#FDBA74 gradyan). Aksan hatası riski sıfırlanır ve 7 dil için aynı sahneyi yeniden kullanırsın.

### İyileştirme turları (ilk sonuç iyi ama "tam premium" değilse sırayla dene)
- *"Increase the depth: push the back card further away and blur it 30% more; add a subtle orange rim light on the phone's left edge."*
- *"Make it feel more expensive: reduce saturation of the background by 20%, add fine film grain, deepen the shadows, keep the app screen untouched."*
- *"The phone looks flat. Add realistic screen glass reflection (10% opacity diagonal), correct titanium bezel highlight and a soft ground shadow."*
- *"Headline is too big / too small: set it to 14% of image height, left-aligned, 2 lines max."*
- *"The floating cards are too many: keep only two, larger, and place one overlapping the phone's top-right corner."*

## 3. Boyut ve teslim

| Görsel | Üret | Yükle |
|---|---|---|
| Telefon | 9:16, 2160×3840 | 1080×1920 PNG (Play: 320-3840 px, uzun kenar ≤ 2× kısa kenar) |
| 7" tablet | 9:16 veya 16:9 | 1200×1920 / 1920×1080 |
| 10" tablet | 16:9 yatay | 2560×1440 |
| Öne çıkan görsel | 1024×500 | 1024×500 PNG/JPG, saydamsız |

Play bu ölçüleri değiştirebilir; yüklerken Console'un uyarılarına bak. Hepsi **7 yerel ayar** için (dil başına 8 telefon).

## 4. Ekran istemleri (telefon, mağazadaki sıra)

Kural: her istemin başına gerçek ekran görüntüsünü ekle. `[HEADLINE]` yerine §5 tablosundaki dile uygun başlığı yaz.
Vurgulanacak kelime **[ACCENT]** ile işaretlidir (turuncu gradyan).

### 1 · Sınav Hazırlık merkezi (İLK GÖRSEL, en önemlisi)
Gerçek ekran: "EXAM PREP" üst yazısı, "Which exam are you preparing for?", kartta **🇺🇸 UBE – Uniform Bar Exam**, turuncu "Start today's exam quiz" düğmesi.
```
Attached: the real Lexio "Exam Prep" screen. Create image 1 of the store set, the hero image.
Scene: the phone floats straight and slightly tilted (8 degrees) in a deep ink environment with a large warm orange glow rising behind it
like a sunrise, fine haze and film grain. Around the phone, four frosted-glass exam chips float with parallax and depth-of-field:
"UBE", "MBE", "SQE1", "HMGS" (white text, 1px glass border, the far two slightly blurred). Thin orange light lines trace the phone edge.
Headline (top third, left-aligned, large): "[HEADLINE]" with [ACCENT] in an orange-to-amber gradient. Small supporting line under it in 60% white: "[SUBLINE]".
Keep the app screen pixel-accurate.
```

### 2 · Sınav seçici – "11 sınav, tek uygulama"
Gerçek ekran: "Change exam" listesi (bayraklı 11 sınav).
```
Attached: the real exam-picker list screen. Image 2.
Scene: bright ivory-to-white studio world (light theme), the phone tilted 10 degrees to the right, casting a long soft shadow.
Behind it a fan of five translucent glass cards (stacked like a deck, each rotated 4 degrees more) suggests "many exams".
Two small floating flag chips (rounded, glass) near the list. Warm orange rim light, very subtle. Headline top, centered: "[HEADLINE]", [ACCENT] as gradient.
Keep the app screen pixel-accurate.
```

### 3 · Sınav sorusu – "Her gün 5 yeni soru"
Gerçek ekran: turuncu ilerleme çubuğu, "Question 2 / 5", soru kartı, 4 seçenek.
```
Attached: the real quiz-question screen. Image 3.
Scene: dark graphite world, the phone straight, large and centered, lower part slightly cropped by the bottom edge for scale.
Above the phone a large floating frosted-glass "5" numeral card with a thin orange progress ring (four segments filled) and the text "[CHIP]".
Soft top-left key light, orange rim light on the right edge, volumetric haze. Headline above: "[HEADLINE]", [ACCENT] as gradient.
Keep the app screen pixel-accurate.
```

### 4 · Cevap + açıklama – "Her cevabı anla"
Gerçek ekran: yeşil doğru şık, kırmızı yanlış şık, yeşil çerçeveli "Legal Explanation" kartı.
```
Attached: the real answered-question screen with green/red options and the explanation card. Image 4.
Scene: split lighting - left half deep ink, right half warm ivory glow, meeting behind the phone as a soft diagonal.
The explanation card is lifted out of the screen and floats in front of the phone, 15% larger, with a soft emerald glow beneath it (keep its text readable and unchanged).
A small emerald glass chip "✓ [CHIP]" floats near the correct option. Headline: "[HEADLINE]", [ACCENT] as gradient.
Keep the app screen pixel-accurate.
```

### 5 · Hukuk kütüphanesi – "Anayasayı öğren"
Gerçek ekran: Law Library listesi, "⏱ 5 min read", "Read →".
```
Attached: the real Law Library screen. Image 5.
Scene: calm, editorial. Deep navy-graphite world with a very faint architectural line drawing of classical columns receding into haze (4% opacity, never dominant).
The phone tilted 6 degrees left. Two lesson cards from the screen float out to the right with soft shadows, one blurred behind.
A thin gold (#E8C27A) light line under the headline. Headline: "[HEADLINE]", [ACCENT] as gradient.
Keep the app screen pixel-accurate.
```

### 6 · Mahkeme pratiği – "Örnek davalarda tez savun"
Gerçek ekran: simülasyon, "⚖️ Defense Counsel" yeşil rozet, aşama göstergesi, seçenek düğmeleri.
```
Attached: the real courtroom-practice screen (role badge, stage indicator, choice buttons). Image 6.
Scene: cinematic dark courtroom-inspired atmosphere WITHOUT any literal objects: warm amber light shafts from the upper right cutting through haze, deep wood-toned gradient (#1A1208 to #09090B).
The phone straight, glowing edge. Three glass "stage" chips connected by a thin orange line float above it (first one lit orange, the others dim) - no text on them except "1", "2", "3".
Absolutely no text or claims about artificial intelligence. Headline: "[HEADLINE]", [ACCENT] as gradient.
Keep the app screen pixel-accurate.
```

### 7 · Sıralama – "Sıralamada yüksel"
Gerçek ekran: Daily/Weekly/Monthly sekmeleri, 🥇🥈🥉, "You" satırı.
```
Attached: the real leaderboard screen. Image 7.
Scene: bright, energetic light world (ivory to warm white) with a soft orange-to-amber mesh gradient in the lower third. The phone tilted 9 degrees.
A frosted-glass podium of three ascending rounded blocks (1, 2, 3, subtle gold/silver/bronze tint) floats below-left of the phone, as an abstract graphic, no text except the numerals.
The user's highlighted row is lifted out slightly with an orange glow. Headline: "[HEADLINE]", [ACCENT] as gradient.
Keep the app screen pixel-accurate.
```

### 8 · Profil, seri, rozetler – "Serini koru"
Gerçek ekran: profil, kariyer rütbesi, puan, rozetler.
```
Attached: the real profile screen (rank, points, badges). Image 8.
Scene: dark world with a warm ember glow rising from the bottom. A stylized flame made of soft orange light (abstract, not clip-art) floats behind the phone.
Two badge tiles from the screen float in front with a subtle emerald and gold glow. A small glass chip "[CHIP]" near the streak.
Headline: "[HEADLINE]", [ACCENT] as gradient.
Keep the app screen pixel-accurate.
```

## 5. Metinler (7 yerel ayar)

`[HEADLINE]` (vurgulu kelime kalın harfle), `[SUBLINE]` yalnızca 1. görselde, `[CHIP]` ilgili görselde.

| # | en | tr | de | fr | es | it |
|---|---|---|---|---|---|---|
| 1 | Practice the **exam** you're taking | Girdiğin **sınava** çalış | Übe für deine **Prüfung** | Préparez votre **examen** | Practica tu **examen** | Allenati per il tuo **esame** |
| 1 sub | UBE · MBE · SQE1 · HMGS and more | UBE · MBE · SQE1 · HMGS ve daha fazlası | UBE · MBE · SQE1 · HMGS und mehr | UBE · MBE · SQE1 · HMGS et plus | UBE · MBE · SQE1 · HMGS y más | UBE · MBE · SQE1 · HMGS e altro |
| 2 | **11** law exams in one app | Tek uygulamada **11** hukuk sınavı | **11** Jura-Prüfungen in einer App | **11** examens de droit, une app | **11** exámenes de Derecho en una app | **11** esami di diritto in un'app |
| 3 | **5** new questions every day | Her gün **5** yeni soru | Jeden Tag **5** neue Fragen | **5** nouvelles questions par jour | **5** preguntas nuevas al día | **5** nuove domande al giorno |
| 3 chip | New every day | Her gün yeni | Täglich neu | Nouveau chaque jour | Nuevo cada día | Nuovo ogni giorno |
| 4 | Understand every **answer** | Her **cevabı** anla | Verstehe jede **Antwort** | Comprenez chaque **réponse** | Entiende cada **respuesta** | Capisci ogni **risposta** |
| 4 chip | Correct | Doğru | Richtig | Correct | Correcto | Corretto |
| 5 | Learn the **constitution** | **Anayasayı** öğren | **Verfassung** verstehen | Comprenez la **constitution** | Aprende la **constitución** | Impara la **costituzione** |
| 6 | Argue practice **cases** | Örnek **davalarda** tez savun | Plädiere in **Fallsimulationen** | Plaidez des **affaires** simulées | Defiende **casos** simulados | Sostieni **casi** simulati |
| 7 | Climb the **leaderboard** | **Sıralamada** yüksel | Steige in der **Rangliste** auf | Grimpez au **classement** | Sube en la **clasificación** | Scala la **classifica** |
| 8 | Keep your **streak** | **Serini** koru | Halte deine **Serie** | Gardez votre **série** | Mantén tu **racha** | Mantieni la **serie** |
| 8 chip | 4-day streak | 4 günlük seri | 4 Tage in Folge | Série de 4 jours | Racha de 4 días | Serie di 4 giorni |

Yerel ayara göre 1. görseldeki gerçek ekran görüntüsünde o dilin sınavı seçili olsun: en-US → UBE, en-GB → SQE1, de → Staatsexamen, fr → CRFPA, es → Acceso a la abogacía, it → Esame di abilitazione forense, tr → HMGS.

## 6. Tablet ve öne çıkan görsel

### Tablet (10", 16:9 yatay) – her ekran için
```
Same art direction. Create a 16:9 landscape image (2560x1440) for the tablet listing of image [N].
Layout: headline on the left third (large, left-aligned, "[HEADLINE]" with [ACCENT] gradient), the phone with the attached real screen on the right two-thirds,
tilted 8 degrees, with the same floating cards from image [N] arranged wider across the space and two additional blurred phone silhouettes (no readable UI) far behind for depth.
Keep the app screen pixel-accurate. Generous negative space, same lighting and colors as image [N].
```
Uygulamanın ayrı bir tablet düzeni yok; bu yüzden tablet görselleri telefon çerçevesiyle yapılır. Gerçek olmayan bir tablet arayüzü çizdirme.

### Öne çıkan görsel (1024×500)
```
Create a 1024x500 Google Play feature graphic for "Lexio". Premium launch-visual style, same art direction.
Left: the wordmark "Lexio" in semibold near-white (or near-black on the light variant) with an ember-orange period, and the tagline "[TAGLINE]" in 60% opacity, max 2 lines, left-aligned with a 60px margin.
Right: the phone with the attached real Exam Prep screen tilted 8 degrees, two floating glass exam chips ("UBE", "SQE1"), orange rim light and a large warm glow behind it on a deep ink background.
Keep every text at least 60px from the edges. No logos of exam bodies, no stars, no prices, no faces.
```
`[TAGLINE]`: en "Daily practice for your law exam" · tr "Hukuk sınavın için günlük pratik" · de "Täglich üben für dein Jura-Examen" · fr "Un entraînement quotidien pour votre examen de droit" · es "Práctica diaria para tu examen de Derecho" · it "Allenamento quotidiano per il tuo esame di diritto".

## 7. Gerçek ekran görüntüsü nasıl alınır (referansın kalitesi sonucu belirler)

- Gerçek telefonda, en yüksek çözünürlükte, **tek tema** (öneri: koyu tema). Durum çubuğu temiz: pil dolu, saat 9:41, bildirim yok.
- Profil ekranında e-posta görünür: test hesabı kullan veya bulanıklaştır.
- Sınav Hazırlık ve testin çalışması için Firestore'da o günün verisi olmalı (yüklenmiş durumda).
- Her dil için o dilde tekrar çek (Profil → Uygulama Dili).

## 8. Yayın öncesi kalite kontrolü

- [ ] Başlıklardaki aksanlı harfler doğru (ö ü ç ı ş ğ é è à ñ ä): görseli %200 yakınlaştırıp oku.
- [ ] Ekran görüntüsündeki metin değişmemiş ve okunuyor.
- [ ] 8 görsel aynı ışık ve renk dilinde, ama hepsi farklı kompozisyon.
- [ ] İlk iki görselde sınav adları (UBE, MBE, SQE1, HMGS) net okunuyor: aramada ilk görünen bunlar.
- [ ] Hiçbir görselde resmî kurum logosu, yıldız puanı, "#1", fiyat, "yapay zekâ" iddiası yok.
- [ ] Görseller kayıpsız PNG, doğru boyutta.
