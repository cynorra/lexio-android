import { useRouter } from 'next/router';
import { BLOG_POSTS, getBlogPostById } from '../../lib/blog';
import styles from '../../styles/Blog.module.css';

export default function BlogDetay({ post }) {
  const router = useRouter();

  if (!post) return null;

  return (
    <div className={styles.detailContainer}>
      <button className={styles.backBtn} onClick={() => router.push('/blog')}>
        ← Haberlere Dön
      </button>

      <div className={styles.postHeader}>
        <span className={styles.categoryTag}>{post.category}</span>
        <h1 className={styles.postTitle}>{post.title}</h1>
        <div className={styles.postMeta}>
          <span>✍️ {post.author}</span>
          <span>📅 {post.date}</span>
          <span style={{ color: '#D4AF37' }}>⏱ {post.readTime}</span>
        </div>
      </div>

      <div
        className={styles.articleBody}
        dangerouslySetInnerHTML={{ __html: post.content }}
      />
    </div>
  );
}

export async function getStaticPaths() {
  return {
    paths: BLOG_POSTS.map(p => ({ params: { id: p.id } })),
    fallback: 'blocking'
  };
}

export async function getStaticProps({ params }) {
  const post = getBlogPostById(params.id);
  return { props: { post } };
}
