import { useRouter } from 'next/router';
import { COURSES } from '../../lib/courses';
import { SOURCES } from '../../lib/sources';
import styles from '../../styles/Dersler.module.css';

export default function DersDetay({ course }) {
  const router = useRouter();

  if (!course) return null;

  return (
    <div className={styles.detailContainer}>
      <button className={styles.backBtn} onClick={() => router.push('/dersler')}>
        ← Ders Kataloğuna Dön
      </button>

      <div className={styles.detailHeader}>
        <div className={styles.eyebrow}>{course.tagLabel} • HMGS Ağırlığı: {course.hmgsWeight}</div>
        <h1 className={styles.detailTitle}>{course.icon} {course.title}</h1>
        <p className={styles.subtitle} style={{ textAlign: 'left', margin: 0 }}>{course.summary}</p>
      </div>

      <h2 style={{ fontFamily: 'var(--font-serif)', color: '#F9F6F0', marginBottom: '1.5rem' }}>
        Ders Modülleri & Mevzuat İncelemesi
      </h2>

      <div className={styles.moduleList}>
        {course.modules.map(mod => (
          <div key={mod.id} className={styles.moduleCard}>
            <div className={styles.moduleTitle}>{mod.title}</div>
            <p style={{ color: 'rgba(249,246,240,0.7)', fontSize: '0.9rem', margin: '0.4rem 0' }}>
              {mod.description}
            </p>
            <div className={styles.moduleContent}>
              {mod.content}
            </div>
            <div style={{ marginTop: '1rem' }}>
              <span style={{ fontSize: '0.8rem', color: 'rgba(249,246,240,0.5)' }}>Dayanak ve İçtihat Kaynakları: </span>
              {mod.sources.map((s, idx) => (
                <span key={idx} className={styles.sourcePill}>📜 {s}</span>
              ))}
            </div>
          </div>
        ))}
      </div>

      <div style={{ display: 'flex', gap: '1rem', marginTop: '2rem' }}>
        <button
          className="btn-primary"
          onClick={() => router.push(`/testler`)}
        >
          📝 Bu Derse Ait Testleri Çöz →
        </button>
        <button
          className="btn-ghost"
          style={{ color: '#F9F6F0', borderColor: 'rgba(249,246,240,0.3)' }}
          onClick={() => router.push('/vakalar')}
        >
          ⚖️ İlgili Dava Simülasyonlarına Git
        </button>
      </div>
    </div>
  );
}

export async function getStaticPaths() {
  return {
    paths: COURSES.map(c => ({ params: { id: c.id } })),
    fallback: false
  };
}

export async function getStaticProps({ params }) {
  const course = COURSES.find(c => c.id === params.id) || null;
  return { props: { course } };
}
