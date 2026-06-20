import { useRouter } from 'next/router';
import { CASES } from '../lib/cases';
import styles from '../styles/Vakalar.module.css';

export default function Vakalar() {
  const router = useRouter();

  return (
    <div>
      <div className="top-bar">
        <div className="logo" onClick={() => router.push('/')} style={{ cursor: 'pointer' }}>
          Lex<span>io</span>
        </div>
        <div className={styles.meta}>Vaka Seçimi</div>
      </div>

      <div className={styles.header}>
        <div className={styles.eyebrow}>Ceza Hukuku</div>
        <h1 className={styles.title}>Hangi Davayı Üstleniyorsunuz?</h1>
        <p className={styles.desc}>
          Yargıtay kararlarından uyarlanmış gerçek senaryolar. Savunma veya savcılık rolünü seçin, AI karşı tarafı oynasın.
        </p>
      </div>

      <div className={styles.grid}>
        {CASES.map(c => (
          <div
            key={c.id}
            className={styles.card}
            onClick={() => router.push(`/vaka/${c.id}`)}
          >
            <div className={`${styles.tag} ${styles[c.tag]}`}>{c.tagLabel}</div>
            <div className={styles.cardTitle}>{c.title}</div>
            <div className={styles.preview}>{c.preview}</div>
            <div className={styles.cardMeta}>
              <span>📋 {c.ref}</span>
              <span>⏱ {c.duration}</span>
              <span>📊 {c.difficulty}</span>
            </div>
            <div className={styles.arrow}>→</div>
          </div>
        ))}
      </div>
    </div>
  );
}
