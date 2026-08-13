import { useRouter } from 'next/router';
import { BLOG_POSTS } from '../lib/blog';
import styles from '../styles/Blog.module.css';

export default function Blog() {
  const router = useRouter();

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.eyebrow}>Güncel Hukuk & Yargı Gündemi</div>
        <h1 className={styles.title}>Hukuk Haberleri & Blog</h1>
        <p className={styles.subtitle}>
          Yargıtay içtihat analizleri, Resmi Gazete mevzuat değişiklikleri, HMGS sınav duyuruları ve hukuk teknolojileri gündemi.
        </p>
      </div>

      <div className={styles.grid}>
        {BLOG_POSTS.map(post => (
          <div
            key={post.id}
            className={styles.card}
            onClick={() => router.push(`/blog/${post.id}`)}
          >
            <div>
              <span className={styles.categoryTag}>{post.category}</span>
              <h2 className={styles.cardTitle}>{post.title}</h2>
              <p className={styles.cardSummary}>{post.summary}</p>
            </div>

            <div className={styles.cardMeta}>
              <span>✍️ {post.author}</span>
              <span>📅 {post.date}</span>
              <span style={{ color: '#D4AF37' }}>{post.readTime}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
