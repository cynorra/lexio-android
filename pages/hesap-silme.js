import Head from 'next/head';
import styles from '../styles/Legal.module.css';

// Account & data deletion page. Google Play requires a public URL that explains how users can
// request deletion of their account and data; enter this page's URL in Play Console -> Data safety.
export default function HesapSilme() {
  return (
    <div className={styles.container}>
      <Head>
        <title>Hesap ve Veri Silme — Lexio</title>
        <meta
          name="description"
          content="Lexio hesabınızı ve verilerinizi nasıl silebileceğiniz. How to delete your Lexio account and data."
        />
      </Head>

      <div className={styles.eyebrow}>Yasal</div>
      <h1>Hesap ve Veri Silme</h1>

      <h2>Uygulama içinden (önerilen)</h2>
      <ol>
        <li>Lexio uygulamasını açın ve giriş yapın.</li>
        <li>Alt menüden <b>Profil</b> sekmesine gidin.</li>
        <li><b>“Hesabımı ve verilerimi sil”</b> seçeneğine dokunun ve onaylayın.</li>
        <li>Güvenlik için Google hesabınızla yeniden giriş yapmanız istenir; ardından silme işlemi tamamlanır.</li>
      </ol>

      <h2>Neler silinir?</h2>
      <ul>
        <li>Hesabınız (Google ile giriş kaydınız), adınız ve e-posta adresiniz</li>
        <li>Puanlarınız, seriniz, çözdüğünüz vakalar ve Instagram kullanıcı adınız</li>
        <li>Sıralama tablolarındaki kayıtlarınız</li>
        <li>Topluluk akışında paylaştığınız gönderiler</li>
        <li>Cihazınızda saklanan yerel kayıtlar</li>
      </ul>
      <p>Silme işlemi anında uygulanır ve geri alınamaz. Reklam ve analiz sağlayıcılarının kendi sistemlerinde tuttuğu veriler kendi politikalarına tabidir.</p>

      <h2>Uygulamaya erişemiyorsanız</h2>
      <p>
        Hesabınıza kayıtlı e-posta adresinden{' '}
        <a href="mailto:eren.gp54@gmail.com?subject=Lexio%20hesap%20silme">eren.gp54@gmail.com</a>{' '}
        adresine “Hesap silme” konulu bir e-posta gönderin. Talebinizi en geç 30 gün içinde işleyip size bilgi veririz.
      </p>

      <hr />

      <h1>Account and Data Deletion</h1>
      <p>
        In the Lexio app: <b>Profile → “Delete my account and data”</b>, confirm, and sign in with Google once more to
        verify it is you. This permanently deletes your account, name and email, points and streak, leaderboard entries
        and posts. If you can no longer access the app, email{' '}
        <a href="mailto:eren.gp54@gmail.com?subject=Lexio%20account%20deletion">eren.gp54@gmail.com</a> from the address
        registered to your account and we will process the request within 30 days.
      </p>
    </div>
  );
}
