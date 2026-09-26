# Lexio – Play Store görselleri için Gemini istemleri

Hazırlanma tarihi: 2026-09-26. Bu istemler uygulamanın **gerçek ekranlarına** göre yazıldı (`layout_*.xml`, `strings.xml`).
Gemini'nin görsel modeli yazıyı ve arayüzü ancak referans verirseniz doğru çizer; bu yüzden en iyi yöntem aşağıdaki **A yöntemi**.

## 0. Yöntem: gerçek ekran görüntüsü + Gemini ile cilalama (önerilen)

1. Uygulamayı telefonda (veya Android Studio emülatöründe) açıp her ekranın **gerçek ekran görüntüsünü** alın (aşağıdaki 8 ekran).
   Koyu tema ve açık tema için ayrı alın; mağazada **tek tema** kullanın (öneri: açık tema ilk 3 görselde, koyu tema sonrakilerde değil; **hepsi aynı** olsun, tutarlılık dönüşümü artırır).
2. Gemini'de yeni bir sohbet açın, önce **"Ana stil istemi"ni** (§1) gönderin, ardından her ekran için: ekran görüntüsünü ekleyip **ilgili ekran istemini** (§3) yapıştırın.
3. Çıktı bir şey bozarsa (yazı yanlış, arayüz uydurma) şunu ekleyin: *"Do not redraw or alter the attached screenshot's UI text or layout. Only place it into the device frame and add the caption and background."*
4. Her dil için **yalnızca `[CAPTION]` satırını** değiştirip yeniden üretin (§4 tablosu). Ekran görüntüsü de o dilde olmalı: uygulama dilini Profil → Uygulama Dili'nden değiştirip yeniden çekin.
5. Boyut: aşağıdaki §2. Gemini kesin piksel vermeyebilir; son adımda görseli istenen oranda kırpın/yeniden boyutlandırın (Play Console yüklemede sorun çıkarırsa).

> **B yöntemi (referanssız):** Gemini'den ekranı sıfırdan çizmesini istemek, uydurma arayüz ve yanlış hukuk metni üretir; Play "yanıltıcı görsel" politikasına takılabilir. Yalnızca §5'teki öne çıkan görsel ve arka planlar için kullanın.

## 1. Ana stil istemi (her sohbetin başında bir kez)

```
You are designing Google Play Store screenshots for "Lexio", an Android law-study app
(law exam prep, daily law quizzes, constitutional-law lessons, courtroom simulations).

Visual system - follow it exactly for every image in this conversation:
- Brand color: orange #F97316 (accent, buttons, progress bars, headline highlight word).
- Neutrals: white #FFFFFF, near-black #18181B for text, light grey #F4F4F5 cards, border #E4E4E7.
  (Dark variant: background #09090B, cards #18181B, text #FAFAFA.)
- Clean, modern, minimal, generous white space, soft rounded cards (16px radius), thin borders. Fonts: a geometric sans-serif (Inter / Google Sans style).
- Layout of every screenshot: solid or very subtle light gradient background in warm off-white (#FFF7ED to #FFFFFF), a short bold headline at the top (max 6 words, 2 lines max, dark text, ONE key word in orange), and below it the phone showing the provided app screen, slightly larger than half of the image height, centered, with a modern thin-bezel Android phone frame and a soft drop shadow. The phone may bleed off the bottom edge.
- Absolutely NO logos or wordmarks of any exam body or organization (no NCBE, no SRA, no state bars, no flags as decoration except those that already appear inside the app screen), no gavel-and-scales stock clip-art overload, no people's faces, no fake star ratings, no "#1" or "best" claims, no price text.
- Keep the app screen 100% legible and unchanged. Never invent additional UI.
- Output a single PNG per request.
```

## 2. Boyutlar (Play Console'da yüklerken doğrulayın: kurallar değişebilir)

| Görsel | Oran / boyut | Adet |
|---|---|---|
| Telefon ekran görüntüsü | 9:16 dikey, örn. **1080 × 1920** px (her kenar 320–3840 px, uzun kenar ≤ 2× kısa kenar) | 2–8 (8 öneriyoruz) |
| 7" tablet | 9:16 dikey **1200 × 1920** veya 16:9 yatay 1920 × 1080 | 4–8 |
| 10" tablet | 9:16 dikey **1600 × 2560** veya 16:9 yatay 2560 × 1440 | 4–8 |
| Öne çıkan görsel (feature graphic) | **1024 × 500** px, PNG/JPG, saydamsız | 1 |
| Uygulama ikonu | 512 × 512 (mevcut ikon kullanılır) | 1 |

Tablet notu: Play, büyük ekran uyumluluğu için tablette en az 4 ekran görüntüsünü öneriyor. Uygulamanın tablet düzeni ayrı değil (telefon düzeni ortalanmış görünür); bu yüzden tablet görsellerinde **telefon çerçevesi + geniş arka plan** kullanın, gerçek olmayan bir tablet arayüzü çizdirmeyin.

Örnek soru (ekran 2–4'te sabit kullanın; içerik doğru bir hukuk sorusudur, FRE 803(2)):

- **Soru:** *A witness testifies that moments after an attack, the victim said "Dan stabbed me." Which statement is most accurate?*
- **A)** It may be admitted as an excited utterance. ✅
- **B)** It is inadmissible because it is hearsay with no exception.
- **C)** It is admissible only if the victim testifies at trial.
- **D)** It is inadmissible because the statement was not made under oath.
- **Açıklama:** *A statement made under the stress of a startling event and relating to it is an excited utterance (FRE 803(2)). No oath is required, and the declarant does not have to testify.*

Sıralamada kullanılacak örnek kullanıcı adları uydurmadır: `Maya R.`, `Daniel K.`, `Elif Y.`, `Lukas B.`, `You`.

## 3. Ekran istemleri (telefon, sırayla mağazada görünecek)

Her istemde `[CAPTION]` yerine §4 tablosundaki başlığı yazın. Ekran görüntüsünü mutlaka ekleyin.

### Ekran 1 – Sınav Hazırlık merkezi (İLK GÖRSEL: en önemlisi)
Gerçek ekran: `layout_exam_prep` – "EXAM PREP" turuncu üst yazı, "Which exam are you preparing for?" başlığı, "Your exam" kartı içinde **🇺🇸 UBE – Uniform Bar Exam**, turuncu "Start today's exam quiz" düğmesi, çerçeveli "Change exam" düğmesi, altta 6'lı gezinme çubuğu.
```
Create a 1080x1920 Google Play screenshot. Headline at the top: "[CAPTION]" (highlight the word for "exam" in orange).
Place the attached Lexio "Exam Prep" screen inside a modern Android phone frame, centered, as described in the style rules.
Add three small rounded pill chips floating just above the top-right of the phone (they must not cover the app screen): "UBE", "MBE", "SQE1" in light grey pills with dark text.
Do not alter the attached screen.
```

### Ekran 2 – Sınav seçici
Gerçek ekran: Sınav Hazırlık'ta "Change exam" → liste: 🇺🇸 UBE, 🇺🇸 MBE, 🇺🇸 MPRE, 🇺🇸 California Bar Exam, 🇬🇧 SQE1, 🇩🇪 Erstes Juristisches Staatsexamen, 🇫🇷 CRFPA, 🇪🇸 Prueba de acceso a la abogacía, 🇮🇹 Esame di abilitazione forense, 🇹🇷 HMGS, 🇹🇷 Hâkim ve Savcı Adaylığı Sınavı.
```
Create a 1080x1920 Google Play screenshot. Headline: "[CAPTION]" with the number "11" in orange.
Show the attached dialog screen (exam list with flags) inside the phone frame. Background: warm off-white gradient with a very faint, large, low-contrast pattern of scales-of-justice line icons (5% opacity).
Do not alter the attached screen.
```

### Ekran 3 – Sınav sorusu
Gerçek ekran: soru ekranı – üstte turuncu ilerleme çubuğu, "Question 2 / 5", soru kartı, 4 seçenek düğmesi (henüz cevaplanmamış).
```
Create a 1080x1920 Google Play screenshot. Headline: "[CAPTION]" (highlight "5" in orange).
Show the attached quiz-question screen in the phone frame. Behind the phone, on the left and right edges, add two softly blurred orange-tinted circles for depth.
Do not alter the attached screen.
```

### Ekran 4 – Cevap + açıklama
Gerçek ekran: doğru seçenek yeşil, yanlış seçilen kırmızı, "💡 Legal Explanation" kartı (yeşil çerçeveli), turuncu "Next Question" düğmesi.
```
Create a 1080x1920 Google Play screenshot. Headline: "[CAPTION]" (highlight the word for "answer" in orange).
Show the attached answered-question screen (green correct option, explanation card) in the phone frame. Add one small floating green rounded tag near the top-left of the phone reading "Correct" with a check icon (outside the app screen).
Do not alter the attached screen.
```

### Ekran 5 – Hukuk kütüphanesi / anayasa dersi
Gerçek ekran: `layout_study_portal` (Law Library, "Constitution and Core Law", ders kartları "⏱ 5 min read", "Read →") veya ders detayı + "Test Yourself".
```
Create a 1080x1920 Google Play screenshot. Headline: "[CAPTION]" (highlight "constitution" in orange).
Show the attached Law Library screen in the phone frame, background a soft warm gradient with a faint outline of a classical courthouse facade (columns) at 6% opacity at the bottom.
Do not alter the attached screen.
```

### Ekran 6 – Mahkeme simülasyonu
Gerçek ekran: simülasyon – "⚖️ Defense Counsel" yeşil rozet, aşama göstergesi "1. … → 2. …", sohbet baloncukları, altta seçenek düğmeleri.
```
Create a 1080x1920 Google Play screenshot. Headline: "[CAPTION]" (highlight the word for "cases" in orange).
Show the attached courtroom-simulation screen in the phone frame. Add a subtle 3-step progress motif (three small dots connected by a line) under the headline, orange for the active dot.
Do not alter the attached screen. Do not add any text that claims artificial intelligence.
```

### Ekran 7 – Sıralama
Gerçek ekran: Daily / Weekly / Monthly sekmeleri, 🥇🥈🥉 satırları, "You" işaretli satır.
```
Create a 1080x1920 Google Play screenshot. Headline: "[CAPTION]" (highlight "leaderboard" in orange).
Show the attached Leaderboard screen in the phone frame. Add a small trophy-like orange rounded badge with the number "1" near the top-right corner of the phone (outside the app screen).
Do not alter the attached screen.
```

### Ekran 8 – Profil, seri ve rozetler
Gerçek ekran: profil – kariyer rütbesi, puan, rozet ızgarası, dil ve ülke satırları.
```
Create a 1080x1920 Google Play screenshot. Headline: "[CAPTION]" (highlight the word for "streak" in orange).
Show the attached Profile screen in the phone frame. Add a flame icon (🔥 style, orange) next to the headline.
Do not alter the attached screen.
```

## 4. Başlıklar (`[CAPTION]`) – 6 dil

| # | en | tr | de | fr | es | it |
|---|---|---|---|---|---|---|
| 1 | Practice the exam you're taking | Girdiğin sınava çalış | Übe für deine Prüfung | Préparez votre examen | Practica tu examen | Allenati per il tuo esame |
| 2 | 11 law exams in one app | Tek uygulamada 11 hukuk sınavı | 11 Jura-Prüfungen in einer App | 11 examens de droit, une app | 11 exámenes de Derecho en una app | 11 esami di diritto in un'app |
| 3 | 5 new questions every day | Her gün 5 yeni soru | Jeden Tag 5 neue Fragen | 5 nouvelles questions par jour | 5 preguntas nuevas al día | 5 nuove domande al giorno |
| 4 | Understand every answer | Her cevabı anla | Verstehe jede Antwort | Comprenez chaque réponse | Entiende cada respuesta | Capisci ogni risposta |
| 5 | Learn the constitution | Anayasayı öğren | Verfassung verstehen | Comprenez la constitution | Aprende la constitución | Impara la costituzione |
| 6 | Argue practice cases | Örnek davalarda tez savun | Plädiere in Fallsimulationen | Plaidez des affaires simulées | Defiende casos simulados | Sostieni casi simulati |
| 7 | Climb the leaderboard | Sıralamada yüksel | Steige in der Rangliste auf | Grimpez au classement | Sube en la clasificación | Scala la classifica |
| 8 | Keep your streak | Serini koru | Halte deine Serie | Gardez votre série | Mantén tu racha | Mantieni la serie |

Yerel ayara göre öne çıkan **ilk görsel** aynı kalır ama ekran görüntüsündeki sınav seçili olmalı: en-US → UBE, en-GB → SQE1, de → Staatsexamen, fr → CRFPA, es → Acceso a la abogacía, it → Esame di abilitazione forense, tr → HMGS (Sınav Hazırlık'ta "Change exam" ile seçip yeniden ekran görüntüsü alın).

## 5. Tablet, öne çıkan görsel ve arka planlar

### Tablet (7" ve 10") – her ekran için
Aynı 8 istemi kullanın, yalnızca ilk satırı değiştirin:
```
Create a 2560x1600 landscape Google Play tablet screenshot (10-inch). Same style rules. Headline "[CAPTION]" on the left third in large type (max 3 lines).
Place the attached phone screen inside a phone frame on the right two-thirds, vertically centered, with two additional decorative, blurred, low-opacity copies of the same frame behind it (left and right) for depth.
Do not alter the attached screen.
```
7" için: `1920x1080` yatay ya da `1200x1920` dikey. Tabletlerde ekran görüntüsünü aynı tutun, sadece kompozisyonu değiştirin.

### Öne çıkan görsel (1024 × 500)
```
Create a 1024x500 Google Play feature graphic for the Android app "Lexio" (law exam prep and daily law quizzes).
Left half: the wordmark "Lexio" in bold near-black (#18181B) with an orange (#F97316) period after it, and under it the tagline "[TAGLINE]" in medium grey-black, max 2 lines.
Right half: a modern Android phone, slightly tilted (8 degrees), showing the attached Lexio Exam Prep screen, with a soft orange glow behind it.
Background: warm off-white gradient (#FFF7ED to #FFFFFF). Keep all text at least 60px away from the edges. No logos of exam bodies, no stars, no "#1", no prices, no faces.
```
`[TAGLINE]`: en "Daily practice for your law exam" · tr "Hukuk sınavın için günlük pratik" · de "Täglich üben für dein Jura-Examen" · fr "Un entraînement quotidien pour votre examen de droit" · es "Práctica diaria para tu examen de Derecho" · it "Allenamento quotidiano per il tuo esame di diritto".

### Koyu tema sürümü (isteğe bağlı, deney için)
Ana stil istemindeki "Dark variant" renklerini kullanın: arka plan `#09090B → #18181B`, başlık `#FAFAFA`, turuncu aynı. Store listing experiments ile açık tema ile karşılaştırın.

## 6. Yayın öncesi kontrol

- [ ] Görsellerdeki tüm metin, ilgili yerel ayarın dilinde ve yazım hatasız (Gemini ünlü olarak aksanlı harflerde hata yapar: ö, ü, ç, é, ñ, ı; her görseli yakından okuyun).
- [ ] Ekran görüntüsünde gerçek kullanıcı adı/e-posta yok (Profil ekranında e-posta görünür: bulanıklaştırın veya test hesabı kullanın).
- [ ] Hiçbir görselde resmî sınav kurumu logosu, "resmî/onaylı" ifadesi, yıldız puanı veya fiyat yok.
- [ ] Sınav Hazırlık ve Sınav Testi ekranları gerçek uygulamada çalışıyor (Firestore'da o günün `examQuizzes` verisi olmalı: `quizgen` workflow'unu çalıştırın).
- [ ] İlk iki görsel telefon listesinde arama sonucunda görünen görsellerdir: sınav adları (UBE, MBE, SQE1…) bunlarda okunur olmalı.
