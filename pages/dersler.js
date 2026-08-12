import { useRouter } from 'next/router';
import { COURSES } from '../lib/courses';
import styles from '../styles/Dersler.module.css';

export default function Dersler() {
  const router = useRouter();

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.eyebrow}>Hukuk Müfredatı & Mevzuat</div>
        <h1 className={styles.title}>Ders Kataloğu</h1>
        <p className={styles.subtitle}>
          Türk Hukuku temel ana bilim dalları, Yargıtay emsal kararları, mevzuat maddeleri ve HMGS / Hakimlik sınav ağırlıklı teori modülleri.
        </p>
      </div>

      <div className={styles.grid}>
        {COURSES.map(course => (
          <div
            key={course.id}
            className={styles.courseCard}
            onClick={() => router.push(`/ders/${course.id}`)}
          >
            <div>
              <div className={styles.cardHeader}>
                <span className={styles.icon}>{course.icon}</span>
                <span className={styles.hmgsBadge}>{course.hmgsWeight}</span>
              </div>
              <h2 className={styles.courseTitle}>{course.title}</h2>
              <p className={styles.summary}>{course.summary}</p>
            </div>

            <div className={styles.metaRow}>
              <span>📖 {course.lessonCount} Modül</span>
              <span>⏱ {course.duration}</span>
              <span>📊 {course.difficulty}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
