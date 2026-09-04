import Head from 'next/head';
import styles from '../styles/Legal.module.css';

export default function Gizlilik() {
  return (
    <div className={styles.container}>
      <Head>
        <title>Gizlilik Politikası — Lexio</title>
        <meta name="description" content="Lexio web sitesi ve mobil uygulamasının gizlilik politikası: hangi verileri topluyoruz, nasıl kullanıyoruz ve haklarınız nelerdir." />
      </Head>

      <div className={styles.eyebrow}>Yasal</div>
      <h1 className={styles.title}>Gizlilik Politikası</h1>
      <p className={styles.updated}>Son güncelleme: 4 Eylül 2026</p>

      <div className={styles.section}>
        <p>
          Bu Gizlilik Politikası, Lexio web sitesi ve Lexio Android mobil uygulaması
          (&quot;Lexio&quot;, &quot;biz&quot;) tarafından toplanan kişisel verilerin nasıl işlendiğini
          açıklar. Lexio&apos;yu kullanarak bu politikayı kabul etmiş olursunuz.
        </p>
      </div>

      <div className={styles.section}>
        <h2>1. Topladığımız Veriler</h2>
        <p>Lexio&apos;yu kullanırken aşağıdaki veriler işlenebilir:</p>
        <ul>
          <li>
            <strong>Hesap bilgileri:</strong> Google ile Giriş yaptığınızda, Firebase Authentication
            aracılığıyla adınız, e-posta adresiniz ve profil fotoğrafınız alınır.
          </li>
          <li>
            <strong>Uygulama içi etkinlik verisi:</strong> Vaka simülasyonu sonuçlarınız, quiz/test
            skorlarınız, liderlik tablosu puanınız, rozet ilerlemeniz, Akış (feed) bölümünde
            paylaştığınız gönderiler ve Workspace (çalışma grubu) etkileşimleriniz Firebase
            Cloud Firestore üzerinde saklanır.
          </li>
          <li>
            <strong>Reklam kimlik bilgisi:</strong> Mobil uygulamada Google AdMob ile reklam
            gösterilir; AdMob, reklamları sunmak için cihazınızın reklam kimliğini (Advertising ID)
            ve bazı cihaz/kullanım bilgilerini işleyebilir.
          </li>
          <li>
            <strong>Bildirim izni:</strong> İzin verirseniz, günlük quiz hatırlatma bildirimleri
            tamamen cihazınızda (WorkManager ile) zamanlanır; bu bildirimler için herhangi bir
            sunucuya veri veya push token gönderilmez.
          </li>
        </ul>
      </div>

      <div className={styles.section}>
        <h2>2. Verileri Nasıl Kullanıyoruz</h2>
        <ul>
          <li>Hesabınızı oluşturmak ve oturumunuzu yönetmek için,</li>
          <li>İlerlemenizi, skorlarınızı ve liderlik tablosu sıralamanızı göstermek için,</li>
          <li>Akış ve Workspace gibi topluluk özelliklerinin çalışması için,</li>
          <li>Mobil uygulamada reklam göstermek ve reklam sağlayıcısının (Google AdMob) hizmetini sunabilmesi için,</li>
          <li>Uygulamayı iyileştirmek ve teknik sorunları gidermek için.</li>
        </ul>
      </div>

      <div className={styles.section}>
        <h2>3. Reklamlar (Google AdMob)</h2>
        <p>
          Lexio Android uygulaması, gelir elde etmek amacıyla Google AdMob aracılığıyla banner
          reklamlar gösterir. AdMob, reklamları kişiselleştirmek için cihaz tanımlayıcıları
          kullanabilir. AdMob&apos;un verileri nasıl işlediği hakkında bilgi için Google&apos;ın{' '}
          <a href="https://policies.google.com/technologies/ads" target="_blank" rel="noreferrer">
            Reklam Teknolojileri
          </a>{' '}
          ve{' '}
          <a href="https://policies.google.com/privacy" target="_blank" rel="noreferrer">
            Gizlilik Politikası
          </a>{' '}
          sayfalarını inceleyebilirsiniz. Lexio web sitesinde şu anda reklam gösterilmemektedir.
        </p>
      </div>

      <div className={styles.section}>
        <h2>4. Verilerin Paylaşımı</h2>
        <p>
          Verileriniz, hizmeti sağlayabilmemiz için aşağıdaki üçüncü taraf altyapı
          sağlayıcılarıyla paylaşılır:
        </p>
        <ul>
          <li><strong>Google Firebase</strong> (Authentication, Cloud Firestore) — hesap ve uygulama verisi depolama,</li>
          <li><strong>Google AdMob</strong> — mobil uygulamada reklam gösterimi.</li>
        </ul>
        <p>Verilerinizi hiçbir şekilde satmıyor veya pazarlama amacıyla üçüncü taraflara aktarmıyoruz.</p>
      </div>

      <div className={styles.section}>
        <h2>5. Veri Güvenliği</h2>
        <p>
          Verileriniz, Google Firebase&apos;in endüstri standardı güvenlik önlemleriyle korunan
          sunucularında saklanır. Buna rağmen internet üzerinden hiçbir aktarım yönteminin
          %100 güvenli olmadığını hatırlatırız.
        </p>
      </div>

      <div className={styles.section}>
        <h2>6. Çocukların Gizliliği</h2>
        <p>
          Lexio, hukuk eğitimi almakta olan yetişkin ve üniversite/staj seviyesi kullanıcılara
          yöneliktir; 13 yaşın altındaki çocuklardan bilerek veri toplamayız.
        </p>
      </div>

      <div className={styles.section}>
        <h2>7. Haklarınız (KVKK ve ilgili mevzuat kapsamında)</h2>
        <p>6698 sayılı Kişisel Verilerin Korunması Kanunu kapsamında şu haklara sahipsiniz:</p>
        <ul>
          <li>Hakkınızda hangi verilerin işlendiğini öğrenme,</li>
          <li>Verilerinizin düzeltilmesini veya silinmesini talep etme,</li>
          <li>Hesabınızın ve ilişkili verilerinizin tamamen silinmesini talep etme.</li>
        </ul>
        <p>Bu talepleri aşağıdaki iletişim adresi üzerinden bize iletebilirsiniz.</p>
      </div>

      <div className={styles.section}>
        <h2>8. Politikadaki Değişiklikler</h2>
        <p>
          Bu politikayı zaman zaman güncelleyebiliriz. Önemli değişiklikler bu sayfada
          yayımlanarak duyurulacaktır.
        </p>
      </div>

      <div className={styles.section}>
        <h2>9. İletişim</h2>
        <div className={styles.contactBox}>
          <p>Gizlilikle ilgili sorularınız veya veri talepleriniz için:</p>
          <p><a href="mailto:eren.gp54@gmail.com">eren.gp54@gmail.com</a></p>
        </div>
      </div>
    </div>
  );
}
