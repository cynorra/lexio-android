import { useRouter } from 'next/router';
import styles from '../styles/Landing.module.css';

export default function Home() {
  const router = useRouter();

  return (
    <main className={styles.landing}>
      <div className={styles.inner}>
        <div className={styles.brandMark}>Lexio — AI Hukuk Simülasyonu</div>
        <h1 className={styles.title}>
          Davayı <em>Sen</em><br />Savun.
        </h1>
        <p className={styles.sub}>
          Gerçek Yargıtay kararlarından üretilmiş vakalar. AI karşı avukat. AI hakim.<br />
          Hukuku gerçek dava atmosferinde öğren.
        </p>
        <div className={styles.ctaGroup}>
          <button className="btn-primary" onClick={() => router.push('/vakalar')}>
            Simülasyona Başla
          </button>
          <button className="btn-ghost" style={{ color: 'rgba(249,246,240,0.8)', borderColor: 'rgba(249,246,240,0.25)' }} onClick={() => router.push('/vakalar')}>
            Vakaları İncele →
          </button>
        </div>
        <div className={styles.statsRow}>
          <div className={styles.stat}>
            <div className={styles.statNum}>10</div>
            <div className={styles.statLabel}>Ceza Davası</div>
          </div>
          <div className={styles.stat}>
            <div className={styles.statNum}>AI</div>
            <div className={styles.statLabel}>Karşı Avukat</div>
          </div>
          <div className={styles.stat}>
            <div className={styles.statNum}>TR</div>
            <div className={styles.statLabel}>Yargıtay Bazlı</div>
          </div>
        </div>
      </div>
    </main>
  );
}
