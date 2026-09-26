# Lexio – Play Store görselleri: hazır Gemini istemleri (v3, referanssız)

Gerçek ekran görüntüsü kullanılmıyor: Gemini **hem uygulama ekranını hem sahneyi** istemden çiziyor. Bu yüzden her istem
kendi başına yeterli (ek dosya yok) ve uygulamanın gerçek metinlerini (`strings.xml`), renklerini (koyu tema) ve düzenini eksiksiz içeriyor.

## Dosyalar

`docs/store/prompts/<yerel-ayar>/` klasörlerinde, dil başına 9 dosya. **Bir dosyanın içeriğinin tamamını Gemini'ye yapıştır = 1 görsel.**

| Dosya | Görsel | Mağazadaki sıra |
|---|---|---|
| `01-exam-hub.txt` | Sınav Hazırlık merkezi (hero) | 1 |
| `02-exam-picker.txt` | 11 sınavlık seçici | 2 |
| `03-exam-question.txt` | Sınav sorusu | 3 |
| `04-answer-explanation.txt` | Cevap + açıklama | 4 |
| `05-law-library.txt` | Hukuk kütüphanesi | 5 |
| `06-courtroom-practice.txt` | Mahkeme pratiği | 6 |
| `07-leaderboard.txt` | Sıralama | 7 |
| `08-profile-streak.txt` | Profil, seri, rozetler | 8 |
| `09-feature-graphic.txt` | Öne çıkan görsel 1024×500 | ayrı alan |

Yerel ayarlar: `en-US` (en-GB de aynı görselleri kullanabilir), `tr-TR`, `de-DE`, `fr-FR`, `es-ES`, `it-IT`.
Her dilde arayüz metni, başlık ve örnek içerik o dilde yazılı; seçili sınav da dile göre (UBE, HMGS, Staatsexamen, CRFPA, Acceso a la abogacía, Esame avvocato).

## Nasıl kullanılır (önerilen akış)

1. Gemini'de **yeni sohbet** aç (her görsel için ayrı sohbet, stil karışmasın) ve görsel üretimi açık olsun.
2. `01-...txt` içeriğini yapıştır. Sonuç sana yakınsa, **aynı sohbette** 2-8'i sırayla yapıştır: ışık ve renk dili tutarlı kalır.
3. İlk sonuç "tam premium" değilse aşağıdaki iyileştirme cümlelerinden birini gönder.
4. İndirdiğin görselleri 1080×1920'ye küçült (2160×3840 üretmek keskinlik verir) ve PNG olarak Play'e yükle.
5. Yazıyı **%200 yakınlaştırıp oku**: aksanlar (ö ü ç ı ş ğ é è à ñ ä) ve uzun cümleler bozulabilir. Bozuksa aynı sohbette: *"The text in [element] is misspelled. Redraw only that text exactly as: “...”. Keep everything else identical."*

### İyileştirme cümleleri (sırayla dene)
- *"Increase the depth: push the back card further away and blur it 30% more; add a subtle orange rim light on the phone's left edge."*
- *"Make it feel more expensive: reduce background saturation by 20%, add fine film grain, deepen shadows, keep the app screen untouched."*
- *"The phone looks flat. Add realistic glass reflection (10% diagonal), correct titanium bezel highlight and a soft ground shadow."*
- *"Headline: set it to 14% of the image height, left-aligned, two lines maximum, 6% margin from all edges."*
- *"The app UI text is too small to read at thumbnail size. Enlarge the app screen by 12% and simplify the surrounding floating cards to two."*
- *"Fewer floating elements: keep only two, larger, one overlapping the phone's top-right corner."*

## Tablet (7" ve 10")

Uygulamanın ayrı bir tablet düzeni yok; tablet görselleri telefon çerçevesiyle yapılır. Herhangi bir istemin **sonuna** ekle:
```
FORMAT OVERRIDE: make this a 16:9 landscape image (2560x1440) for the tablet listing. Headline on the left third (large, left-aligned, 3 lines max),
the phone on the right two-thirds tilted 8 degrees, the floating cards spread wider, plus two extra blurred phone silhouettes far behind for depth (no readable UI on them).
Same lighting, colors and app screen as described.
```
7" için `1920x1080`. Play en az 4 tablet ekran görüntüsü öneriyor.

## Boyutlar

| Görsel | Üret | Yükle |
|---|---|---|
| Telefon | 2160×3840 (9:16) | 1080×1920 PNG (kenarlar 320–3840 px, uzun kenar ≤ 2× kısa) |
| Tablet | 2560×1440 | 2560×1440 / 1920×1080 |
| Öne çıkan | 1024×500 | 1024×500 PNG/JPG, saydamsız |

Play bu ölçüleri değiştirebilir; yüklerken Console'un uyarılarına bak.

## Play politikası uyarısı (önemli)

Play, ekran görüntülerinin **uygulamanın gerçek arayüzünü** göstermesini ister. Yapay zekâ ile çizilen ekranlar gerçek uygulamadan sapabilir.
Bu istemler gerçek ekranların düzenini ve metinlerini birebir tarif ediyor, ama iki bilinen fark var:

1. **Alt gezinme çubuğu:** İstemler "ince çizgi ikonlu" bir çubuk çiziyor; gerçek uygulamada şu an **emoji** ikonlar var (⚖️ 💬 👥 📚 🏆 👤).
2. **Rozet adları:** Gerçek uygulamada rozet adları yalnızca **Türkçe** sabit yazılı (`BadgeData.java`); istemlerde her dilde çevirisi çizilir.

Yayınlamadan önce ya bu iki noktayı uygulamada gerçek hâline getirmek (ikon seti + rozet çevirisi) ya da 8. görseli (profil) mağazadan çıkarmak en güvenlisi.
İstersen uygulamada bu iki değişikliği ben yaparım. Görsellerde "yapay zekâ" iddiası bilerek yok (simülasyon canlı yapay zekâ değil).

## Kalite kontrol listesi

- [ ] Görsellerdeki tüm metin doğru yazılmış, aksanlar tam
- [ ] 8 görsel aynı ışık ve renk dilinde, ama farklı kompozisyonlarda
- [ ] 1. ve 2. görselde sınav adları (UBE, MBE, SQE1, HMGS) net okunuyor
- [ ] Hiçbir görselde kurum logosu, yıldız puanı, "#1", fiyat, yüz yok
- [ ] Arayüz metni küçük ekranda (thumbnail) hâlâ okunuyor
- [ ] Boyutlar doğru, PNG
