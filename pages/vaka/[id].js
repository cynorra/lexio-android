import { useState } from 'react';
import { useRouter } from 'next/router';
import { CASES } from '../../lib/cases';
import styles from '../../styles/VakaDetay.module.css';

export default function VakaDetay({ vaka }) {
  const router = useRouter();
  const [role, setRole] = useState(null);

  if (!vaka) return null;

  const handleStart = () => {
    if (!role) return;
    router.push(`/simulasyon/${vaka.id}?rol=${role}`);
  };

  return (
    <div>
      <div className="top-bar">
        <div className="logo" onClick={() => router.push('/')} style={{ cursor: 'pointer' }}>
          Lex<span>io</span>
        </div>
        <button className={styles.backBtn} onClick={() => router.push('/vakalar')}>
          ← Vakalara Dön
        </button>
      </div>

      <div className={styles.container}>
        <div className={styles.caseDisplay}>
          <div className={styles.caseLabel}>Seçilen Dava</div>
          <div className={`${styles.tag} ${styles[vaka.tag]}`}>{vaka.tagLabel}</div>
          <h1 className={styles.caseTitle}>{vaka.title}</h1>
          <p className={styles.caseText}>{vaka.full}</p>
          <div className={styles.evidenceList}>
            {vaka.evidence.map((e, i) => (
              <span key={i} className={styles.evidencePill}>{e}</span>
            ))}
          </div>
          <div className={styles.caseMeta}>
            <span>📋 {vaka.ref}</span>
            <span>⏱ {vaka.duration}</span>
            <span>📊 {vaka.difficulty}</span>
          </div>
        </div>

        <h2 className={styles.roleQuestion}>Hangi Tarafı Temsil Ediyorsunuz?</h2>

        <div className={styles.roleGrid}>
          <div
            className={`${styles.roleCard} ${role === 'savunma' ? styles.selected : ''}`}
            onClick={() => setRole('savunma')}
          >
            <div className={styles.roleIcon}>⚖️</div>
            <div className={styles.roleName}>Savunma Avukatı</div>
            <div className={styles.roleDesc}>Sanığın haklarını korursunuz. AI savcı olarak karşınıza çıkar.</div>
          </div>
          <div
            className={`${styles.roleCard} ${role === 'savci' ? styles.selected : ''}`}
            onClick={() => setRole('savci')}
          >
            <div className={styles.roleIcon}>🔍</div>
            <div className={styles.roleName}>Savcı</div>
            <div className={styles.roleDesc}>Suçu ispat etmeye çalışırsınız. AI savunma avukatı olarak karşınıza çıkar.</div>
          </div>
        </div>

        <button
          className="btn-primary"
          style={{ width: '100%', padding: '1rem' }}
          onClick={handleStart}
          disabled={!role}
        >
          Simülasyonu Başlat →
        </button>
      </div>
    </div>
  );
}

export async function getStaticPaths() {
  return {
    paths: CASES.map(c => ({ params: { id: String(c.id) } })),
    fallback: false
  };
}

export async function getStaticProps({ params }) {
  const vaka = CASES.find(c => c.id === Number(params.id)) || null;
  return { props: { vaka } };
}
