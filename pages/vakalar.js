import { useState } from 'react';
import { useRouter } from 'next/router';
import { CASES } from '../lib/cases';
import styles from '../styles/Vakalar.module.css';

export default function Vakalar() {
  const router = useRouter();
  const [selectedTag, setSelectedTag] = useState('hepsi');

  const filteredCases = selectedTag === 'hepsi' 
    ? CASES 
    : CASES.filter(c => c.tag === selectedTag);

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '2rem 1.5rem' }}>
      <div className={styles.header}>
        <div className={styles.eyebrow}>Yargıtay İçtihatları & Gerçek Davalar</div>
        <h1 className={styles.title}>Dava Simülasyonu Kataloğu</h1>
        <p className={styles.desc}>
          Yargıtay ve Anayasa Mahkemesi kararlarından uyarlanmış gerçek hukuki senaryolar. Tarafınızı seçin, AI hakim ve karşı taraf ile duruşmaya çıkın.
        </p>

        {/* Filter Pills */}
        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center', marginTop: '1.5rem', flexWrap: 'wrap' }}>
          {[
            { tag: 'hepsi', label: 'Tüm Davalar' },
            { tag: 'ceza', label: 'Ceza Hukuku' },
            { tag: 'medeni', label: 'Medeni Hukuk' },
            { tag: 'borclar', label: 'Borçlar Hukuku' },
            { tag: 'ticaret', label: 'Ticaret Hukuku' },
            { tag: 'usul', label: 'Usul Hukuku' }
          ].map(item => (
            <button
              key={item.tag}
              onClick={() => setSelectedTag(item.tag)}
              style={{
                background: selectedTag === item.tag ? 'rgba(212, 175, 55, 0.2)' : 'rgba(22, 28, 42, 0.6)',
                border: `1px solid ${selectedTag === item.tag ? '#D4AF37' : 'rgba(249, 246, 240, 0.15)'}`,
                color: selectedTag === item.tag ? '#D4AF37' : 'rgba(249, 246, 240, 0.8)',
                padding: '0.5rem 1rem',
                borderRadius: '20px',
                cursor: 'pointer',
                fontSize: '0.88rem',
                fontWeight: 500,
                transition: 'all 0.2s'
              }}
            >
              {item.label}
            </button>
          ))}
        </div>
      </div>

      <div className={styles.grid}>
        {filteredCases.map(c => (
          <div
            key={c.id}
            className={styles.card}
            onClick={() => router.push(`/vaka/${c.id}`)}
          >
            <div className={`${styles.tag} ${styles[c.tag] || ''}`}>{c.tagLabel}</div>
            <div className={styles.cardTitle}>{c.title}</div>
            <div className={styles.preview}>{c.preview}</div>
            <div className={styles.cardMeta}>
              <span style={{ color: '#D4AF37' }}>📜 {c.ref}</span>
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
