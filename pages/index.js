import { useRouter } from 'next/router';
import styles from '../styles/Landing.module.css';

export default function Home() {
  const router = useRouter();

  return (
    <main className={styles.landing}>
      <div className={styles.inner}>
        <div className={styles.brandMark}>Lexio — Türkiye’nin İlk AI Hukuk Akademi Platformu</div>
        <h1 className={styles.title}>
          Mevzuatı Öğren.<br />
          Davayı <em>Sen</em> Savun.
        </h1>
        <p className={styles.sub}>
          Yargıtay ve Anayasa Mahkemesi kararlarından üretilmiş vakalar. Kapsamlı hukuk ders kataloğu.<br />
          HMGS / Hakimlik uyumlu ÖSYM tarzı test soruları ve AI karşı taraf simülasyonu.
        </p>

        <div className={styles.ctaGroup}>
          <button className="btn-primary" onClick={() => router.push('/dersler')}>
            📚 Ders Kataloğunu İncele
          </button>
          <button className="btn-primary" style={{ background: 'linear-gradient(135deg, #D4AF37, #B8860B)' }} onClick={() => router.push('/vakalar')}>
            ⚖️ Dava Simülasyonu
          </button>
          <button className="btn-ghost" style={{ color: 'rgba(249,246,240,0.9)', borderColor: 'rgba(249,246,240,0.3)' }} onClick={() => router.push('/testler')}>
            📝 HMGS Testleri →
          </button>
        </div>

        <div className={styles.statsRow}>
          <div className={styles.stat}>
            <div className={styles.statNum}>6+</div>
            <div className={styles.statLabel}>Hukuk Ana Bilim Dalı</div>
          </div>
          <div className={styles.stat}>
            <div className={styles.statNum}>HMGS</div>
            <div className={styles.statLabel}>Uyumlu Test Bankası</div>
          </div>
          <div className={styles.stat}>
            <div className={styles.statNum}>Yargıtay</div>
            <div className={styles.statLabel}>Emsal Karar Kaynağı</div>
          </div>
          <div className={styles.stat}>
            <div className={styles.statNum}>AI</div>
            <div className={styles.statLabel}>Simülatör & Soru Üreteci</div>
          </div>
        </div>
      </div>
    </main>
  );
}
