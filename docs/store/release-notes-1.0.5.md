# Sürüm notları: 1.0.5 (versionCode 6)

Play Console → Yayınla → Dahili test (veya Üretim) → Yeni sürüm → **Bu sürümde yenilikler**.
Aşağıdaki bloğu olduğu gibi yapıştır: Play etiketleri (`<en-US>...</en-US>`) dilleri ayırır. Her dil 500 karakterin altında.
Sayı iddiası yok (kural: mağaza metninde sayı yok). Bu notta olmayan hiçbir özellik vaat edilmiyor.

```
<en-US>
New: fresh trial-simulation cases keep arriving, so there is always something new to argue. Set your own daily reminder time (or let the app learn when you play), and share your quiz result with friends. Your streak reminder now warns you before a streak ends. Fixes: crashes in the trial chat and the leaderboard, plus smoother loading. Ad privacy choices are in your profile where required.
</en-US>
<en-GB>
New: fresh trial-simulation cases keep arriving, so there is always something new to argue. Set your own daily reminder time (or let the app learn when you play), and share your quiz result with friends. Your streak reminder now warns you before a streak ends. Fixes: crashes in the trial chat and the leaderboard, plus smoother loading. Ad privacy choices are in your profile where required.
</en-GB>
<tr-TR>
Yeni: Yeni dava simülasyonları gelmeye devam ediyor, her zaman tartışacak yeni bir dosya var. Hatırlatma saatini kendin seç (ya da uygulama ne zaman oynadığını öğrensin), quiz sonucunu arkadaşlarınla paylaş. Seri hatırlatması artık serin bitmeden uyarıyor. Düzeltmeler: duruşma sohbetinde ve liderlik tablosunda yaşanan çökmeler, daha akıcı yükleme. Gerekli bölgelerde reklam gizliliği seçenekleri profilinde.
</tr-TR>
<de-DE>
Neu: Laufend neue Verhandlungssimulationen, es gibt immer etwas Neues zu plädieren. Lege deine Erinnerungszeit selbst fest (oder lass die App lernen, wann du spielst) und teile dein Quiz-Ergebnis mit Freunden. Die Serien-Erinnerung warnt dich jetzt, bevor deine Serie endet. Behoben: Abstürze im Verhandlungs-Chat und in der Bestenliste, flüssigeres Laden. Werbe-Datenschutzoptionen findest du, wo erforderlich, im Profil.
</de-DE>
<fr-FR>
Nouveau : de nouvelles simulations de procès arrivent régulièrement, il y a toujours une affaire à plaider. Choisissez l'heure de votre rappel quotidien (ou laissez l'appli apprendre quand vous jouez) et partagez votre résultat de quiz. Le rappel de série vous prévient désormais avant qu'elle ne s'arrête. Correctifs : plantages dans le chat d'audience et le classement, chargement plus fluide. Les choix de confidentialité des annonces sont dans votre profil là où c'est requis.
</fr-FR>
<es-ES>
Novedad: siguen llegando nuevas simulaciones de juicio, siempre hay un caso nuevo que defender. Elige tu hora de recordatorio diario (o deja que la app aprenda cuándo juegas) y comparte tu resultado del cuestionario. El recordatorio de racha ahora te avisa antes de que termine. Correcciones: cierres en el chat del juicio y en la clasificación, y carga más fluida. Las opciones de privacidad de anuncios están en tu perfil donde se requiere.
</es-ES>
<it-IT>
Novità: continuano ad arrivare nuove simulazioni di processo, c'è sempre un caso nuovo da sostenere. Scegli l'ora del promemoria giornaliero (o lascia che l'app impari quando giochi) e condividi il risultato del quiz. Il promemoria della serie ora ti avvisa prima che finisca. Correzioni: arresti anomali nella chat del processo e nella classifica, caricamento più fluido. Le scelte sulla privacy degli annunci sono nel profilo dove richiesto.
</it-IT>
```

## Kontrol listesi (yayından önce)

- Play Console'da yüklü en yüksek `versionCode`'a bak. Bu sürüm **6**; ondan yüksek bir kod zaten yüklendiyse `android/app/build.gradle.kts` içinde artır.
- Dava üretimi çalıştı mı: Firestore'da `caseLibrary/*/cases` altında dava var mı. Yoksa vaka listesi "yüklenemedi / boş" gösterir.
- Firestore kuralları yayınlandı mı (`firestore.rules`, `caseLibrary` bloğu).
- AdMob'da AB düzenlemeleri mesajı oluşturuldu mu (yoksa onay formu çıkmaz).
- Play Console → Uygulama içeriği → Reklam kimliği ve Veri güvenliği formu güncel mi (analytics, reklam kimliği, e-posta).
