# Lexio – Google Play mağaza girişi: ASO + SEO + AEO + GEO

Hazırlanma tarihi: 2026-09-26 · Paket: `com.cynorra.lexio` · Kategori: **Education** · Birincil pazar: **ABD**, sonra Avrupa, sonra Türkiye.

Bu dosya Play Console'a yapıştırılacak metinleri (6 dil, 7 yerel ayar) ve neden öyle yazıldıklarını içerir.
Metinler uygulamanın **gerçek** özelliklerine göre yazıldı (aşağıdaki "Doğrulanmış özellikler" listesi). Karakter sayıları
betikle doğrulandı; sınırlar: başlık 30, kısa açıklama 80, uzun açıklama 4000, "yenilikler" 500.

---

## 1. Doğrulanmış özellikler (mağazada yalnızca bunlar vaat edilir)

| Özellik | Durum |
|---|---|
| Hukuk sınavı hazırlığı: 11 sınav (UBE, MBE, MPRE, California Bar, SQE1, Erstes Staatsexamen, CRFPA, İspanya acceso a la abogacía, İtalya esame avvocato, HMGS, Hâkim-Savcı Adaylığı), her gün 5 yeni soru | Yeni, bu sürümle (`versionName 1.0.3`); içerik Firestore'a `tools/quizgen` ile yazılır |
| Günlük hukuk testi: 7 ülke (ABD, İngiltere, Almanya, Fransa, İspanya, İtalya, Türkiye), 5 soru/gün, seri, kariyer puanı | Var |
| Hukuk kütüphanesi: ülkenin anayasası/temel hukuku üzerine günlük ders + kendini sına | Var |
| Mahkeme simülasyonu: 7 ceza vakası (TR 4, ABD/İngiltere/Almanya 1'er), savunma/savcılık rolü, aşamalı duruşma, karar | Var – **karşı taraf yanıtları önceden yazılmış seçeneklerdir, canlı yapay zekâ değildir** |
| Sıralama: günlük/haftalık/aylık | Var |
| Topluluk akışı | Var |
| Rozetler, kariyer rütbeleri, ilerlemeyi paylaş | Var |
| 6 arayüz dili (TR, EN, DE, FR, ES, IT), açık/koyu tema, günlük hatırlatma | Var |
| Google ile giriş, uygulama içi hesap silme | Var |
| Reklam (AdMob) | Var → Play'de "Contains ads" işaretli olmalı |
| **Gruplar (workspaces)** | İçerik yok ("yakında" durumu) → mağazada **anılmaz** |

> Uyarı: Uygulamadaki açılış ekranı hâlâ "AI karşı avukat. AI hakim." diyor (`landing_subtitle`, `case_detail_role_prompt`).
> Simülasyon canlı yapay zekâ değil, senaryolu olduğu için bu ifade Google Play "Yanıltıcı davranış" politikası açısından riskli.
> Mağaza metinleri bu yüzden yapay zekâdan yalnızca "içeriğin bir kısmı yapay zekâ destekli hazırlanır ve otomatik kontrol edilir" diye söz eder.
> Uygulama içi metni de düzeltmenizi öneririm.

---

## 2. Anahtar kelime stratejisi (ASO)

Play Store'da ayrı bir "keyword" alanı **yoktur**. Arama, şu alanlardaki metne bakar: **başlık > kısa açıklama > uzun açıklama > (daha az) uygulama adı/geliştirici adı**. Bu yüzden anahtar kelimeler bu alanlara doğal cümle içinde yerleştirildi.

Kurallar (uygulandı):
1. **Başlık (30 kr):** marka + en güçlü 2–3 anahtar kelime. `Lexio: Bar Exam & Law Quiz` → "bar exam" (ABD birincil niyet) + "law quiz".
2. **Kısa açıklama (80 kr):** en çok aranan sınav adları (UBE, MBE, MPRE, SQE1…) + fayda. Play'de arama sonuçlarında görünür.
3. **Uzun açıklama:** sınav adları ilk paragrafta (ilk ~250 karakter en çok ağırlığı taşır), sonra başlıklı bölümler, sonda kısa "hızlı cevaplar". Aynı anahtar kelime **3–5 kez** geçer; 10+ tekrar "keyword stuffing" sayılıp reddedilebilir.
4. **Her dil için yerel arama niyeti:** Almanca listede "Staatsexamen", Fransızcada "CRFPA", İspanyolcada "acceso a la abogacía", İtalyancada "esame avvocato", Türkçede "HMGS" öne çıkar. Yerel ayar sayfasında aynı sınav listesi vardır ama sıra farklıdır.
5. Emoji/yıldız yalnızca bölüm başlıklarında; başlıkta ve kısa açıklamada yok (Play başlıktaki emojiyi kaldırır).
6. Marka adı "Lexio" tüm yerel ayarlarda aynı: varlık (entity) tutarlılığı hem Play hem yapay zekâ cevap motorları için önemlidir.

### 2.1 Sınav anahtar kelime ana listesi (ülkeye göre)

"Uygulamada var" sütunu, bu sürümde günlük testi bulunan sınavları gösterir. Diğerleri yalnızca kelime olarak açıklamaya girer **ve uygulama o sınavı kapsamıyorsa açıklamada "hazırlık" vaadi olarak geçmemelidir** (yanıltıcı metin olur).

| Ülke | Sınav / arama terimi | Uygulamada var | Not |
|---|---|---|---|
| ABD | UBE (Uniform Bar Exam) | ✅ | En yüksek hacim |
| ABD | MBE (Multistate Bar Exam), MEE, MPT | ✅ MBE, MEE konuları | "MBE practice questions" güçlü niyet |
| ABD | MPRE | ✅ | Etik sınavı, ayrı arama hacmi |
| ABD | California Bar Exam | ✅ | Eyalet sınavları çok aranır |
| ABD | New York / Texas / Florida bar exam | ❌ (NY = UBE) | NY için UBE sayfası kullanılabilir; TX/FL yok |
| ABD | LSAT, 1L, law school | ❌ | LSAT mantık/okuma testidir, hukuk bilgisi değil; ayrı ürün gerekir |
| İngiltere | SQE1 (FLK1, FLK2), SQE2 | ✅ SQE1 | SQE2 beceri sınavı, kapsam dışı |
| İngiltere | QLTS, BPTC / Bar Course | ❌ | QLTS yerini SQE'ye bıraktı |
| Almanya | Erstes / Zweites Juristisches Staatsexamen, Referendariat | ✅ 1. Examen | |
| Fransa | CRFPA (examen d'entrée), EFB, concours ENM | ✅ CRFPA | ENM yok |
| İspanya | Prueba de acceso a la abogacía | ✅ | |
| İspanya | Oposiciones judicatura/fiscal | ❌ | |
| İtalya | Esame di abilitazione forense (esame avvocato) | ✅ | |
| İtalya | Concorso in magistratura | ❌ | |
| Türkiye | HMGS | ✅ | Kısaltmanın açılımını resmî duyurudan doğrulayın |
| Türkiye | Adli Yargı Hâkim ve Savcı Adaylığı Sınavı | ✅ | Resmî sınav adını ve düzenleyici kurumu yayın öncesi doğrulayın |
| Türkiye | Avukatlık stajı, KPSS A grubu hukuk, noterlik | ❌ | Genişleme adayı |
| Hindistan | AIBE (All India Bar Examination), CLAT | ❌ | Büyük hacim; ayrı içerik + `en-IN` girişi gerekir |
| Kanada / Avustralya / Nijerya / Filipinler | NCA sınavları, Priestley 11, Nigerian Bar, Philippine Bar | ❌ | Genişleme adayları; `tools/quizgen/exams.py`'ye bir kayıt eklemek yeterli |

Yeni sınav eklemek: `tools/quizgen/exams.py` içindeki `EXAMS` sözlüğüne kayıt + `Exams.java` listesine satır. Başka değişiklik gerekmez.

### 2.2 Hedef arama ifadeleri (mağaza ve web için)

- **en-US:** bar exam practice questions · UBE practice app · MBE practice questions · MPRE practice questions · California bar exam app · law quiz app · bar exam prep free · constitutional law quiz
- **en-GB:** SQE1 practice questions · SQE1 app · law quiz UK
- **de-DE:** Staatsexamen Quiz · Jura Examen lernen App · Jura Fragen üben · Staatsexamen Vorbereitung
- **fr-FR:** CRFPA QCM · examen avocat préparation · quiz droit
- **es-ES:** examen acceso a la abogacía test · test derecho · preparar acceso abogacía
- **it-IT:** quiz esame avvocato · esame abilitazione forense simulazione · quiz diritto
- **tr-TR:** HMGS soru bankası · HMGS deneme · hakimlik savcılık sınavı soruları · hukuk testi · günlük hukuk sorusu

---

## 3. Play Console'a yapıştırılacak metinler

Ana (varsayılan) dil: **English (United States)**. Diğerleri "Ana mağaza girişi → Çeviri ekle" ile eklenir.
Play'de `es-ES`, `de-DE`, `fr-FR`, `it-IT`, `tr-TR`, `en-GB` ayrı yerel ayarlardır; ABD İspanyolcası için (`es-US`) `es-ES` metni kullanılabilir.

### en-US · English (United States) – varsayılan

**Başlık** (26/30)
```
Lexio: Bar Exam & Law Quiz
```

**Kısa açıklama** (80/80)
```
Daily bar exam practice: UBE, MBE, MPRE & more. Law quizzes, lessons, case sims.
```

**Uzun açıklama** (2680/4000)
```
Preparing for the bar exam or a law school test? Lexio gives you a fresh daily practice quiz for the exam you choose: UBE, MBE, MPRE, California Bar Exam, SQE1, the German Staatsexamen, the French CRFPA, the Spanish abogacía access exam, the Italian esame avvocato and Turkey's HMGS. Add constitutional-law lessons and courtroom simulations, and you can study anywhere in a few minutes a day.

★ LAW EXAM PREP
• Pick the law exam you are taking. It does not depend on the country you live in.
• 5 new exam-style questions every day, each with a clear explanation.
• U.S. bar subjects: Civil Procedure, Constitutional Law, Contracts, Criminal Law and Procedure, Evidence, Real Property, Torts, plus MEE topics and professional responsibility (MPRE).
• Also covers SQE1 (FLK1 and FLK2), the German Staatsexamen, CRFPA, the Spanish and Italian bar exams and Turkish HMGS topics.
• Exam practice never changes your career score or streak.

★ DAILY LAW QUIZ
• A new 5-question quiz every day for the United States, United Kingdom, Germany, France, Spain, Italy or Turkey.
• Build a streak, earn career points and unlock badges from Law Student to Supreme Justice.

★ LAW LIBRARY
• A new lesson from your country's constitution and core legal rules, with a short self-check quiz and its source.

★ COURTROOM SIMULATIONS
• Take the defense or the prosecution in criminal cases inspired by real precedents, choose your strategy at each stage of the trial and see the verdict.

★ COMPETE AND CONNECT
• Daily, weekly and monthly leaderboards.
• A community feed to share questions with other law students and legal professionals.

★ BUILT FOR STUDYING ON THE GO
• 6 languages: English, Türkçe, Deutsch, Français, Español, Italiano.
• Light and dark themes and a daily reminder to protect your streak.

WHO IS LEXIO FOR?
Law students, bar exam candidates, paralegals and anyone curious about how constitutions and courts work.

QUICK ANSWERS
What exams can I practice for? UBE, MBE, MPRE, California Bar Exam, SQE1, Erstes Juristisches Staatsexamen, CRFPA, Prueba de acceso a la abogacia, esame di abilitazione forense, HMGS and the Turkish judge and prosecutor exam.
Is Lexio free? Yes. It is free and supported by ads.
Does it replace a bar prep course? No. It is daily practice that fits between your courses and books.
Is it legal advice? No. It is educational content.

Lexio is an independent study app. It is not affiliated with, endorsed by or sponsored by the NCBE, any state bar, the SRA, OSYM or any other exam body; exam names are used only to describe what you can practice for. Some content is AI-assisted and checked automatically, and it can contain errors. Not legal advice.
```

**Yenilikler** (198/500)
```
New: Law Exam Prep. Choose your exam (UBE, MBE, MPRE, California Bar, SQE1, Staatsexamen, CRFPA, HMGS and more) and get 5 new practice questions every day. Also: a 6-language picker in your profile.
```

### en-GB · English (United Kingdom)

**Başlık** (26/30)
```
Lexio: SQE, Bar & Law Quiz
```

**Kısa açıklama** (77/80)
```
Daily law exam practice: SQE1, UBE, Staatsexamen & more. Quizzes and lessons.
```

**Uzun açıklama** (2684/4000)
```
Preparing for the SQE, the bar or a law degree exam? Lexio gives you a fresh daily practice quiz for the exam you choose: UBE, MBE, MPRE, California Bar Exam, SQE1, the German Staatsexamen, the French CRFPA, the Spanish abogacía access exam, the Italian esame avvocato and Turkey's HMGS. Add constitutional-law lessons and courtroom simulations, and you can study anywhere in a few minutes a day.

★ LAW EXAM PREP
• Pick the law exam you are taking. It does not depend on the country you live in.
• 5 new exam-style questions every day, each with a clear explanation.
• U.S. bar subjects: Civil Procedure, Constitutional Law, Contracts, Criminal Law and Procedure, Evidence, Real Property, Torts, plus MEE topics and professional responsibility (MPRE).
• Also covers SQE1 (FLK1 and FLK2), the German Staatsexamen, CRFPA, the Spanish and Italian bar exams and Turkish HMGS topics.
• Exam practice never changes your career score or streak.

★ DAILY LAW QUIZ
• A new 5-question quiz every day for the United States, United Kingdom, Germany, France, Spain, Italy or Turkey.
• Build a streak, earn career points and unlock badges from Law Student to Supreme Justice.

★ LAW LIBRARY
• A new lesson from your country's constitution and core legal rules, with a short self-check quiz and its source.

★ COURTROOM SIMULATIONS
• Take the defense or the prosecution in criminal cases inspired by real precedents, choose your strategy at each stage of the trial and see the verdict.

★ COMPETE AND CONNECT
• Daily, weekly and monthly leaderboards.
• A community feed to share questions with other law students and legal professionals.

★ BUILT FOR STUDYING ON THE GO
• 6 languages: English, Türkçe, Deutsch, Français, Español, Italiano.
• Light and dark themes and a daily reminder to protect your streak.

WHO IS LEXIO FOR?
Law students, bar exam candidates, paralegals and anyone curious about how constitutions and courts work.

QUICK ANSWERS
What exams can I practice for? UBE, MBE, MPRE, California Bar Exam, SQE1, Erstes Juristisches Staatsexamen, CRFPA, Prueba de acceso a la abogacia, esame di abilitazione forense, HMGS and the Turkish judge and prosecutor exam.
Is Lexio free? Yes. It is free and supported by ads.
Does it replace a bar prep course? No. It is daily practice that fits between your courses and books.
Is it legal advice? No. It is educational content.

Lexio is an independent study app. It is not affiliated with, endorsed by or sponsored by the NCBE, any state bar, the SRA, OSYM or any other exam body; exam names are used only to describe what you can practice for. Some content is AI-assisted and checked automatically, and it can contain errors. Not legal advice.
```

**Yenilikler** (198/500)
```
New: Law Exam Prep. Choose your exam (UBE, MBE, MPRE, California Bar, SQE1, Staatsexamen, CRFPA, HMGS and more) and get 5 new practice questions every day. Also: a 6-language picker in your profile.
```

### tr-TR · Türkçe

**Başlık** (27/30)
```
Lexio: Hukuk Sınavı ve HMGS
```

**Kısa açıklama** (77/80)
```
HMGS ve hakimlik-savcılık sınavı için günlük hukuk testi, ders ve simülasyon.
```

**Uzun açıklama** (2588/4000)
```
Hukuk sınavına mı hazırlanıyorsun? Lexio, seçtiğin sınav için her gün yeni bir çalışma testi sunar: HMGS, Adli Yargı Hâkim ve Savcı Adaylığı Sınavı, ayrıca UBE, MBE, MPRE, California Bar, SQE1, Alman Staatsexamen, Fransız CRFPA, İspanya ve İtalya avukatlık sınavları. Anayasa dersleri ve mahkeme simülasyonlarıyla günde birkaç dakikada, her yerde çalış.

★ HUKUK SINAVI HAZIRLIK
• Gireceğin hukuk sınavını seç. Yaşadığın ülkeden bağımsızdır.
• Her gün sınav tarzında 5 yeni soru, her biri net bir açıklamayla.
• HMGS için Anayasa, İdare, Medeni, Borçlar, Ticaret, İcra ve İflas, Medeni Usul, Ceza ve Ceza Muhakemesi, İş Hukuku ve daha fazlası.
• ABD baro konuları (Civil Procedure, Constitutional Law, Contracts, Evidence, Torts...), SQE1 ve Avrupa sınavları da var.
• Sınav çalışması kariyer puanını ve serini etkilemez.

★ GÜNLÜK HUKUK TESTİ
• ABD, İngiltere, Almanya, Fransa, İspanya, İtalya veya Türkiye için her gün yeni 5 soruluk test.
• Seri yap, kariyer puanı kazan, Hukuk Öğrencisi'nden Yargıtay Hâkimi'ne uzanan rozetleri aç.

★ HUKUK KÜTÜPHANESİ
• Seçtiğin ülkenin anayasasından ve temel hukuk kurallarından yeni bir ders, kısa bir kendini sına testi ve kaynağıyla.

★ MAHKEME SİMÜLASYONLARI
• Gerçek emsallerden esinlenen ceza davalarında savunma veya savcılık rolünü üstlen, duruşmanın her aşamasında stratejini seç, kararı gör.

★ YARIŞ VE PAYLAŞ
• Günlük, haftalık ve aylık sıralama tabloları.
• Diğer hukuk öğrencileri ve hukukçularla soru paylaşabileceğin topluluk akışı.

★ HER YERDE ÇALIŞMAK İÇİN
• 6 dil: Türkçe, English, Deutsch, Français, Español, Italiano.
• Açık ve koyu tema, serini korumak için günlük hatırlatma.

LEXIO KİMLER İÇİN?
Hukuk fakültesi öğrencileri, HMGS ve hâkimlik-savcılık adayları, stajyer avukatlar ve anayasaların ve mahkemelerin nasıl çalıştığını merak eden herkes.

KISA CEVAPLAR
Hangi sınavlara çalışabilirim? HMGS, Hâkim ve Savcı Adaylığı Sınavı, UBE, MBE, MPRE, California Bar, SQE1, Staatsexamen, CRFPA, İspanya ve İtalya avukatlık sınavları.
Lexio ücretsiz mi? Evet, ücretsizdir ve reklamlarla desteklenir.
Dershane veya kitabın yerini tutar mı? Hayır; derslerin ve kitapların arasına sığan günlük pratiktir.
Hukuki tavsiye midir? Hayır, eğitim amaçlı içeriktir.

Lexio bağımsız bir çalışma uygulamasıdır; ÖSYM, Adalet Bakanlığı, NCBE, SRA, herhangi bir baro veya sınav kurumuyla bağlantılı değildir ve onlar tarafından onaylanmamıştır. Sınav adları yalnızca neye çalışabileceğini anlatmak için kullanılır. İçeriklerin bir kısmı yapay zekâ destekli hazırlanır, otomatik olarak kontrol edilir ve hata içerebilir. Hukuki tavsiye değildir.
```

**Yenilikler** (197/500)
```
Yeni: Hukuk Sınavı Hazırlık. Sınavını seç (HMGS, Hâkim-Savcı Adaylığı, UBE, MBE, SQE1, Staatsexamen, CRFPA ve daha fazlası) ve her gün 5 yeni çalışma sorusu çöz. Ayrıca profilde 6 dilli dil seçimi.
```

### de-DE · Deutsch

**Başlık** (25/30)
```
Lexio: Jura Examen & Quiz
```

**Kısa açıklama** (79/80)
```
Täglich Jura üben: Staatsexamen, UBE, SQE1 & mehr. Quiz, Lektionen, Simulation.
```

**Uzun açıklama** (2609/4000)
```
Du bereitest dich auf ein juristisches Examen vor? Lexio gibt dir jeden Tag ein neues Übungsquiz für die Prüfung deiner Wahl: Erstes Juristisches Staatsexamen, UBE, MBE, MPRE, California Bar Exam, SQE1, CRFPA, die spanische und italienische Anwaltsprüfung und die türkische HMGS. Dazu Lektionen zum Verfassungsrecht und Gerichtssimulationen, überall in wenigen Minuten am Tag.

★ EXAMENSVORBEREITUNG
• Wähle die juristische Prüfung, die du ablegst, unabhängig vom Land, in dem du lebst.
• Jeden Tag 5 neue Fragen im Prüfungsstil, jeweils mit klarer Erklärung.
• Für das Staatsexamen: BGB AT, Schuldrecht, Sachenrecht, StGB AT und BT, StPO, Staatsorganisationsrecht, Grundrechte, Verwaltungsrecht, VwGO, ZPO, Gesellschafts- und Europarecht.
• Auch enthalten: US-Bar-Fächer, SQE1 und weitere europäische Prüfungen.
• Das Prüfungstraining verändert weder deine Karrierepunkte noch deine Serie.

★ TÄGLICHES JURA-QUIZ
• Jeden Tag ein neues Quiz mit 5 Fragen für die USA, Großbritannien, Deutschland, Frankreich, Spanien, Italien oder die Türkei.
• Baue eine Serie auf, sammle Karrierepunkte und schalte Abzeichen frei, vom Jurastudenten bis zum Obersten Richter.

★ JURA-BIBLIOTHEK
• Jeden Tag eine neue Lektion aus der Verfassung und den Grundregeln deines Landes, mit kurzem Selbsttest und Quellenangabe.

★ GERICHTSSIMULATION
• Übernimm Verteidigung oder Anklage in Strafverfahren nach echten Vorbildern, wähle in jeder Phase deine Strategie und erlebe das Urteil.

★ MESSEN UND AUSTAUSCHEN
• Tages-, Wochen- und Monatsranglisten.
• Ein Community-Feed, um Fragen mit anderen Jurastudierenden und Juristen zu teilen.

★ ZUM LERNEN UNTERWEGS
• 6 Sprachen: Deutsch, English, Türkçe, Français, Español, Italiano.
• Helles und dunkles Design und eine tägliche Erinnerung für deine Serie.

FÜR WEN IST LEXIO?
Jurastudierende, Examenskandidaten, Referendare und alle, die verstehen wollen, wie Verfassungen und Gerichte funktionieren.

KURZE ANTWORTEN
Für welche Prüfungen kann ich üben? Erstes Staatsexamen, UBE, MBE, MPRE, California Bar Exam, SQE1, CRFPA, spanische und italienische Anwaltsprüfung, HMGS.
Ist Lexio kostenlos? Ja, mit Werbung.
Ersetzt es das Repetitorium? Nein, es ist tägliches Training zwischen Skript und Fallbuch.
Ist das Rechtsberatung? Nein, es sind Bildungsinhalte.

Lexio ist eine unabhängige Lern-App und steht in keiner Verbindung zu einem Prüfungsamt, einer Anwaltskammer oder Prüfungsorganisation (NCBE, SRA, ÖSYM u. a.); Prüfungsnamen beschreiben nur, wofür du üben kannst. Ein Teil der Inhalte ist KI-gestützt, wird automatisch geprüft und kann Fehler enthalten. Keine Rechtsberatung.
```

**Yenilikler** (186/500)
```
Neu: Examensvorbereitung. Wähle deine Prüfung (Staatsexamen, UBE, MBE, SQE1, CRFPA, HMGS und mehr) und übe jeden Tag mit 5 neuen Fragen. Außerdem: Sprachauswahl mit 6 Sprachen im Profil.
```

### fr-FR · Français

**Başlık** (26/30)
```
Lexio: Droit, CRFPA & Quiz
```

**Kısa açıklama** (73/80)
```
Quiz de droit quotidien : CRFPA, UBE, SQE1 et plus. Cours et simulations.
```

**Uzun açıklama** (2874/4000)
```
Vous préparez un examen de droit ? Lexio vous propose chaque jour un nouveau quiz d'entraînement pour l'examen de votre choix : examen d'entrée au CRFPA, UBE, MBE, MPRE, California Bar Exam, SQE1, Staatsexamen allemand, examens d'avocat espagnol et italien, HMGS turc. Avec des cours de droit constitutionnel et des simulations de procès, révisez partout en quelques minutes par jour.

★ PRÉPARATION AUX EXAMENS DE DROIT
• Choisissez l'examen que vous passez, quel que soit votre pays.
• 5 nouvelles questions par jour dans le style de l'examen, chacune avec une explication claire.
• Pour le CRFPA : droit des obligations, responsabilité civile, procédure civile et pénale, droit pénal, libertés fondamentales, droit administratif, droit des affaires, droit du travail, droit de l'Union européenne, déontologie.
• Également disponibles : matières du barreau américain, SQE1 et autres examens européens.
• L'entraînement aux examens ne modifie ni votre score de carrière ni votre série.

★ QUIZ DE DROIT QUOTIDIEN
• Un nouveau quiz de 5 questions chaque jour pour les États-Unis, le Royaume-Uni, l'Allemagne, la France, l'Espagne, l'Italie ou la Turquie.
• Construisez une série, gagnez des points de carrière et débloquez des badges, d'étudiant en droit à juge suprême.

★ BIBLIOTHÈQUE JURIDIQUE
• Une nouvelle leçon issue de la constitution et des règles juridiques fondamentales de votre pays, avec un court quiz d'auto-évaluation et sa source.

★ SIMULATIONS DE PROCÈS
• Endossez le rôle de la défense ou de l'accusation dans des affaires pénales inspirées de précédents réels, choisissez votre stratégie à chaque étape et découvrez le verdict.

★ CLASSEMENTS ET COMMUNAUTÉ
• Classements quotidien, hebdomadaire et mensuel.
• Un fil communautaire pour partager vos questions avec d'autres étudiants et juristes.

★ CONÇU POUR RÉVISER PARTOUT
• 6 langues : Français, English, Deutsch, Español, Italiano, Türkçe.
• Thèmes clair et sombre, rappel quotidien pour garder votre série.

POUR QUI ?
Étudiants en droit, candidats au CRFPA et aux examens du barreau, juristes en formation et curieux du fonctionnement des constitutions et des tribunaux.

RÉPONSES RAPIDES
Pour quels examens puis-je m'entraîner ? CRFPA, UBE, MBE, MPRE, California Bar Exam, SQE1, Staatsexamen, examens d'avocat espagnol et italien, HMGS.
Lexio est-il gratuit ? Oui, avec de la publicité.
Remplace-t-il une prépa ? Non, c'est un entraînement quotidien qui complète vos cours.
Est-ce un conseil juridique ? Non, ce sont des contenus pédagogiques.

Lexio est une application d'étude indépendante, sans lien avec un organisme d'examen ou un barreau (NCBE, SRA, ÖSYM, etc.) ; les noms d'examens servent uniquement à décrire ce que vous pouvez travailler. Une partie des contenus est générée avec l'aide de l'IA, vérifiée automatiquement, et peut contenir des erreurs. Ce n'est pas un conseil juridique.
```

**Yenilikler** (215/500)
```
Nouveau : préparation aux examens de droit. Choisissez votre examen (CRFPA, UBE, MBE, SQE1, Staatsexamen, HMGS et plus) et travaillez 5 nouvelles questions par jour. Également : choix parmi 6 langues dans le profil.
```

### es-ES · Español (España)

**Başlık** (25/30)
```
Lexio: Abogacía y Derecho
```

**Kısa açıklama** (76/80)
```
Test diario de Derecho: acceso a la abogacía, UBE, SQE1, Staatsexamen y más.
```

**Uzun açıklama** (2770/4000)
```
¿Preparas un examen de Derecho? Lexio te ofrece cada día un test de práctica nuevo para el examen que elijas: prueba de acceso a la abogacía, UBE, MBE, MPRE, California Bar Exam, SQE1, Staatsexamen alemán, CRFPA francés, examen de abogado italiano y HMGS turco. Con lecciones de Derecho constitucional y simulaciones de juicios, estudia en cualquier sitio en unos minutos al día.

★ PREPARACIÓN DE EXÁMENES DE DERECHO
• Elige el examen que vas a presentar, sin importar el país donde vivas.
• 5 preguntas nuevas al día al estilo del examen, cada una con una explicación clara.
• Para el acceso a la abogacía: deontología, Derecho constitucional, obligaciones y contratos, derechos reales, familia y sucesiones, procesal civil y penal, penal, administrativo, mercantil, laboral y Derecho de la UE.
• También incluye materias del bar estadounidense, SQE1 y otros exámenes europeos.
• La práctica de exámenes no cambia tu puntuación de carrera ni tu racha.

★ TEST DIARIO DE DERECHO
• Un test nuevo de 5 preguntas cada día para Estados Unidos, Reino Unido, Alemania, Francia, España, Italia o Turquía.
• Consigue una racha, gana puntos de carrera y desbloquea insignias, de estudiante de Derecho a magistrado supremo.

★ BIBLIOTECA JURÍDICA
• Una lección nueva de la constitución y las normas básicas de tu país, con un breve cuestionario de repaso y su fuente.

★ SIMULACIONES DE JUICIOS
• Asume la defensa o la acusación en casos penales inspirados en precedentes reales, elige tu estrategia en cada fase del juicio y descubre el veredicto.

★ COMPITE Y CONECTA
• Clasificaciones diaria, semanal y mensual.
• Un muro de comunidad para compartir dudas con otros estudiantes y profesionales del Derecho.

★ PARA ESTUDIAR EN CUALQUIER LUGAR
• 6 idiomas: Español, English, Deutsch, Français, Italiano, Türkçe.
• Tema claro y oscuro y un recordatorio diario para mantener tu racha.

¿PARA QUIÉN ES LEXIO?
Estudiantes de Derecho, candidatos a la prueba de acceso a la abogacía y a otros exámenes jurídicos, y cualquiera con curiosidad por cómo funcionan las constituciones y los tribunales.

RESPUESTAS RÁPIDAS
¿Para qué exámenes puedo practicar? Acceso a la abogacía, UBE, MBE, MPRE, California Bar Exam, SQE1, Staatsexamen, CRFPA, examen de abogado italiano, HMGS.
¿Lexio es gratis? Sí, con anuncios.
¿Sustituye a una academia? No, es práctica diaria que complementa tus apuntes y libros.
¿Es asesoramiento legal? No, es contenido educativo.

Lexio es una app de estudio independiente, sin vinculación con ningún organismo examinador ni colegio de abogados (NCBE, SRA, ÖSYM, etc.); los nombres de los exámenes solo describen para qué puedes practicar. Parte del contenido se crea con ayuda de IA, se revisa automáticamente y puede contener errores. No es asesoramiento legal.
```

**Yenilikler** (216/500)
```
Novedad: preparación de exámenes de Derecho. Elige tu examen (acceso a la abogacía, UBE, MBE, SQE1, Staatsexamen, CRFPA, HMGS y más) y practica 5 preguntas nuevas cada día. Además: selector de 6 idiomas en el perfil.
```

### it-IT · Italiano

**Başlık** (28/30)
```
Lexio: Esame Avvocato e Quiz
```

**Kısa açıklama** (77/80)
```
Quiz di diritto ogni giorno: esame avvocato, UBE, SQE1, Staatsexamen e altro.
```

**Uzun açıklama** (2821/4000)
```
Ti prepari per un esame di diritto? Lexio ti offre ogni giorno un nuovo quiz di allenamento per l'esame che scegli: esame di abilitazione alla professione forense, UBE, MBE, MPRE, California Bar Exam, SQE1, Staatsexamen tedesco, CRFPA francese, esame spagnolo di accesso all'avvocatura e HMGS turco. Con lezioni di diritto costituzionale e simulazioni di processi, studi ovunque in pochi minuti al giorno.

★ PREPARAZIONE AGLI ESAMI DI DIRITTO
• Scegli l'esame che sosterrai, a prescindere dal paese in cui vivi.
• 5 nuove domande al giorno nello stile dell'esame, ognuna con una spiegazione chiara.
• Per l'esame da avvocato: obbligazioni e contratti, responsabilità extracontrattuale, diritti reali, famiglia e successioni, diritto penale, procedura civile e penale, diritto amministrativo, costituzionale, commerciale, del lavoro, dell'Unione europea e deontologia.
• Disponibili anche le materie del bar statunitense, SQE1 e altri esami europei.
• L'allenamento agli esami non modifica il tuo punteggio di carriera né la tua serie.

★ QUIZ DI DIRITTO QUOTIDIANO
• Ogni giorno un nuovo quiz da 5 domande per Stati Uniti, Regno Unito, Germania, Francia, Spagna, Italia o Turchia.
• Costruisci una serie, guadagna punti carriera e sblocca i badge, da studente di legge a giudice supremo.

★ BIBLIOTECA GIURIDICA
• Una nuova lezione dalla costituzione e dalle norme fondamentali del tuo paese, con un breve quiz di verifica e la fonte.

★ SIMULAZIONI DI PROCESSO
• Assumi la difesa o l'accusa in casi penali ispirati a precedenti reali, scegli la strategia a ogni fase del processo e scopri il verdetto.

★ SFIDA E CONFRONTO
• Classifiche giornaliera, settimanale e mensile.
• Una bacheca della community per condividere domande con altri studenti e professionisti del diritto.

★ PER STUDIARE OVUNQUE
• 6 lingue: Italiano, English, Deutsch, Français, Español, Türkçe.
• Tema chiaro e scuro e un promemoria giornaliero per non perdere la serie.

PER CHI È LEXIO?
Studenti di giurisprudenza, candidati all'esame da avvocato e ad altri esami giuridici, e chiunque voglia capire come funzionano costituzioni e tribunali.

RISPOSTE RAPIDE
Per quali esami posso allenarmi? Esame da avvocato, UBE, MBE, MPRE, California Bar Exam, SQE1, Staatsexamen, CRFPA, esame spagnolo di accesso all'avvocatura, HMGS.
Lexio è gratis? Sì, con pubblicità.
Sostituisce un corso di preparazione? No, è allenamento quotidiano da affiancare a manuali e lezioni.
È una consulenza legale? No, sono contenuti educativi.

Lexio è un'app di studio indipendente, non affiliata né approvata da alcun ente d'esame o ordine forense (NCBE, SRA, ÖSYM e altri); i nomi degli esami servono solo a descrivere per cosa puoi allenarti. Parte dei contenuti è creata con l'aiuto dell'IA, controllata automaticamente e può contenere errori. Non è consulenza legale.
```

**Yenilikler** (219/500)
```
Novità: preparazione agli esami di diritto. Scegli il tuo esame (esame da avvocato, UBE, MBE, SQE1, Staatsexamen, CRFPA, HMGS e altri) e allenati con 5 nuove domande al giorno. Inoltre: selettore a 6 lingue nel profilo.
```

---

## 4. Play Console ayarları (ASO'nun metin dışı kısmı)

1. **Kategori:** Education (Eğitim). Etiketler: Education → "Exam prep" / "Study" benzeri en yakın etiketleri seçin.
2. **İletişim:** e-posta + gizlilik politikası: `https://cynorra.github.io/lexio/privacy-policy.html` (uygulamada da bağlı).
3. **Veri güvenliği / hesap silme:** Play, hesap silme yolunun mağaza formunda belirtilmesini ister. Uygulama içi silme var (Profil → Hesabı sil); web silme sayfası bağlantısı gerekiyorsa Play formu için bir URL hazırlayın.
4. **Reklamlar:** "Contains ads" = Evet (AdMob). `app-ads.txt` dosyasını geliştirici web sitesinin kök dizinine koyun (AdMob → Uygulamalar → app-ads.txt).
5. **Özel mağaza girişleri (Custom Store Listings):** Play Console → Store presence → Custom store listings. Her ülke/sınav için ayrı giriş: örn. "UBE" (ABD), "SQE1" (İngiltere), "Staatsexamen" (Almanya). Her birine kendi başlık öne çıkan görseli ve ilk 250 karakteri verin; reklam kampanyası bağlantısıyla eşleştirin.
6. **Mağaza girişi denemeleri (Store listing experiments):** önce ikon ve ilk ekran görüntüsü, sonra kısa açıklama. Her seferinde tek değişken, en az 2 hafta.
7. **Görseller:** telefon 4–8, 7" ve 10" tablet ekran görüntüleri, öne çıkan görsel 1024×500, ikon 512×512 → istemler `docs/store/gemini-screenshot-prompts.md`.
8. **Puan ve yorum:** günlük testi bitirdikten sonra (mutlu an) uygulama içi puanlama isteği ekleyin (Play In-App Review API). Yorumlara yerel dilde yanıt verin: yanıtlar da aranabilir metindir.
9. **Yeni dil = yeni pazar:** her yerel ayarda tüm alanlar (metin + görsel altyazısı) yerel olmalı; yarı çeviri sıralamayı düşürür.

---

## 5. AEO (cevap motoru optimizasyonu) ve GEO (üretken motor optimizasyonu)

Amaç: Google'ın AI Overview'u, Gemini, ChatGPT, Perplexity gibi sistemler "en iyi UBE/MBE çalışma uygulaması nedir?" gibi sorulara Lexio'yu doğru ve alıntılanabilir şekilde söylesin.

### 5.1 Mağaza metninde (uygulandı)
- **Soru–cevap bloğu** ("Quick answers"): cevap motorları kısa, kendi başına anlamlı cümleleri alıntılar. Her cevap ilk cümlede net: "Is Lexio free? Yes. It is free and supported by ads."
- **Tanım cümlesi** ilk paragrafta: "Lexio gives you a fresh daily practice quiz for the exam you choose…".
- **Adlandırılmış varlıklar**: sınav adları tam ve kısaltmasıyla (Uniform Bar Examination / UBE) geçer.
- **Dürüst sınırlar**: "Not legal advice", "not affiliated", "does not replace a bar prep course". Güvenilirlik sinyali ve politika koruması.

### 5.2 Web sitesinde (yapılacak; uygulamanın Next.js sitesi zaten var)
Şu an canlı bir alan adı yok; `cynorra.github.io/lexio/` üzerinde yalnızca gizlilik sayfası bulunuyor. Site canlıya alındığında:

1. **Sınav başına bir sayfa:** `/exams/ube`, `/exams/mbe`, `/exams/mpre`, `/exams/sqe1`, `/exams/staatsexamen`, `/exams/crfpa`, `/exams/hmgs` … Her sayfada: sınavın ne olduğu (2–3 cümle), kapsanan konular listesi (`tools/quizgen/exams.py`'deki konu başlıklarından üretilebilir), örnek 2 soru + açıklama, Play bağlantısı, açıklama ("bağlantılı değildir").
2. **Yapılandırılmış veri (JSON-LD)** ana sayfada:
```json
{
  "@context": "https://schema.org",
  "@type": "MobileApplication",
  "name": "Lexio: Bar Exam & Law Quiz",
  "operatingSystem": "ANDROID",
  "applicationCategory": "EducationalApplication",
  "offers": { "@type": "Offer", "price": "0", "priceCurrency": "USD" },
  "downloadUrl": "https://play.google.com/store/apps/details?id=com.cynorra.lexio",
  "description": "Daily law exam practice for the UBE, MBE, MPRE, California Bar, SQE1, Staatsexamen, CRFPA, HMGS and more, with constitutional-law lessons and courtroom simulations.",
  "inLanguage": ["en", "tr", "de", "fr", "es", "it"]
}
```
   Sınav sayfalarına `FAQPage` şeması (aynı "Quick answers" soruları) ekleyin.
3. **`llms.txt`** (site kökü): Lexio'nun ne olduğu, desteklenen sınavlar, ücret modeli, sınırlamalar, sürüm ve Play bağlantısı; 15–20 satır düz metin. Kaynak doğruluğu için tek bir "gerçek sayfa" (fact sheet) tutun; sayılar (11 sınav, 7 ülke, günlük 5 soru) değiştikçe güncelleyin.
4. **`robots.txt`**'de GPTBot, Google-Extended, PerplexityBot, ClaudeBot'a izin verin; `sitemap.xml` ve dil sürümleri için `hreflang` (en, tr, de, fr, es, it).
5. **Tutarlılık:** her yerde aynı ad ("Lexio"), aynı tek cümlelik tanım, aynı sınav adları. Cevap motorları çelişen bilgiyi güvenilmez sayar.

### 5.3 Site dışı sinyaller (GEO)
- Sınav hazırlığı topluluklarında (r/barexam, r/LawSchool, r/SQE, ilgili Discord/Telegram grupları) **kurallara uyarak, satış yapmadan** gerçek katkı: "MBE Evidence hearsay istisnaları" gibi ücretsiz mini içerik + uygulama bağlantısı. Kural ihlali yasaklanmaya yol açar; her topluluğun promosyon kuralını okuyun.
- Hukuk fakültesi kulüpleri, blog yazarları ve YouTube çalışma kanalları için kısa tanıtım paketi (logo, 3 ekran görüntüsü, tek paragraf).
- Yorumlar: 4.5+ puan ve yerel dilde yorum sayısı hem Play sıralamasını hem yapay zekâ önerilerini besler.

### 5.4 Ölçüm
- Play Console → **Acquisition → Search terms** ve **Store listing conversion** (yerel ayar bazında).
- Aylık elle test: ChatGPT, Gemini, Perplexity, Google'a şu soruları sorun ve Lexio'nun çıkıp çıkmadığını, doğru anlatılıp anlatılmadığını not edin: "best free app for UBE practice questions", "SQE1 practice questions app", "Staatsexamen Quiz App", "HMGS soru çözme uygulaması", "app per quiz esame avvocato".
- Tablo tutun: sorgu · motor · Lexio anıldı mı · doğru mu · tarih.

---

## 6. Politika ve marka uyarıları

1. **Ticari markalar:** UBE, MBE, MEE, MPT ve MPRE, NCBE'nin; SQE, SRA'nın; LSAT, LSAC'ın markalarıdır. Metinlerde yalnızca "hangi sınava çalışabilirsin" anlamında ve **bağlantısızlık beyanıyla** kullanıldı. Logo veya resmî görsel kullanmayın; "resmî", "onaylı", "gerçek sınav soruları" demeyin. (Sorular sınav *tarzında* özgün çalışma soruları; gerçek sınav soruları değil.)
2. **Metadata politikası:** başlıkta "#1", "free", "best", fiyat/ indirim ifadesi, emoji ve gereksiz anahtar kelime listesi yasak. Kısa açıklama da anahtar kelime listesi olmamalı (ikisi de okunabilir cümledir).
3. **Yanıltıcı iddia:** yalnızca §1 tablosundaki özellikler yazıldı. "Yapay zekâ karşı avukat" ve "gruplar" bilinçli olarak anılmadı.
4. **İçerik doğruluğu:** sorular yapay zekâ ile üretilip otomatik doğrulanıyor; hata olabilir. Metinlerdeki uyarı bu yüzden var; kaldırmayın.
5. **Eğitim/sağlık-hukuk beyanı:** "Not legal advice" cümlesi tüm dillerde son paragrafta.

---

## 7. Yayın öncesi kontrol listesi

- [ ] `versionCode 4` AAB'sini yükle (kod bu sürümde artırıldı)
- [ ] Firestore kurallarını yayınla (`examQuizzes` okuma kuralı eklendi, `firestore.rules`)
- [ ] `quizgen` workflow'unu elle çalıştır: `exams: us-ube` (ilk deneme), sonra hepsi
- [ ] Yeni ekran görüntülerini üret (bkz. `gemini-screenshot-prompts.md`) ve 7 yerel ayara yükle
- [ ] 7 yerel ayara başlık / kısa / uzun / yenilikleri yapıştır
- [ ] "Contains ads", veri güvenliği ve hesap silme formlarını kontrol et
- [ ] Uygulama içi "AI karşı avukat/hakim" ifadelerini düzelt
