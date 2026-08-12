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
    <div className={styles.container}>
      <button className={styles.backBtn} onClick={() => router.push('/vakalar')}>
        ← Vakalara Dön
      </button>

      <div className={styles.caseDisplay}>
        <div className={styles.caseLabel}>Seçilen Dava Senaryosu</div>
        <div className={`${styles.tag} ${styles[vaka.tag] || ''}`}>{vaka.tagLabel}</div>
        <h1 className={styles.caseTitle}>{vaka.title}</h1>
        <p className={styles.caseText}>{vaka.full}</p>
        
        <div style={{ marginTop: '1rem', marginBottom: '1rem' }}>
          <strong style={{ color: '#D4AF37', fontSize: '0.9rem' }}>📂 Somut Deliller ve Belgeler:</strong>
          <div className={styles.evidenceList} style={{ marginTop: '0.5rem' }}>
            {vaka.evidence.map((e, i) => (
              <span key={i} className={styles.evidencePill}>• {e}</span>
            ))}
          </div>
        </div>

        <div className={styles.caseMeta} style={{ marginTop: '1.5rem', borderTop: '1px solid rgba(249,246,240,0.1)', paddingTop: '1rem' }}>
          <span style={{ color: '#D4AF37' }}>📜 Mevzuat ve İçtihat: {vaka.ref}</span>
          <span>⏱ Tahmini Süre: {vaka.duration}</span>
          <span>📊 Zorluk Derecesi: {vaka.difficulty}</span>
        </div>
      </div>

      <h2 className={styles.roleQuestion}>Hangi Tarafı Temsil Ediyorsunuz?</h2>

      <div className={styles.roleGrid}>
        <div
          className={`${styles.roleCard} ${role === 'savunma' ? styles.selected : ''}`}
          onClick={() => setRole('savunma')}
        >
          <div className={styles.roleIcon}>⚖️</div>
          <div className={styles.roleName}>Savunma Avukatı / Müdafi</div>
          <div className={styles.roleDesc}>Müvekkilinizin haklarını korur, meşru savunma ve şüpheden sanık yararlanır ilkelerini ileri sürersiniz. AI Karşı Taraf olarak davranır.</div>
        </div>
        <div
          className={`${styles.roleCard} ${role === 'savci' ? styles.selected : ''}`}
          onClick={() => setRole('savci')}
        >
          <div className={styles.roleIcon}>🔍</div>
          <div className={styles.roleName}>Savcı / İddia Makamı</div>
          <div className={styles.roleDesc}>Kamu adına iddiayı ve kast unsurlarını ispat etmeye çalışırsınız. AI Savunma Avukatı olarak cevap verir.</div>
        </div>
      </div>

      <button
        className="btn-primary"
        style={{ width: '100%', padding: '1rem', marginTop: '1.5rem', fontSize: '1.1rem' }}
        onClick={handleStart}
        disabled={!role}
      >
        Duruşma Simülasyonunu Başlat →
      </button>
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
