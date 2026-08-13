import { useState, useEffect } from 'react';
import styles from '../styles/Akis.module.css';

export default function Akis() {
  const [posts, setPosts] = useState([]);
  const [author, setAuthor] = useState('');
  const [role, setRole] = useState('Avukat / Hukukçu');
  const [category, setCategory] = useState('Emsal Karar Tartışması');
  const [content, setContent] = useState('');
  const [instaHandle, setInstaHandle] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState(null);
  const [successMsg, setSuccessMsg] = useState(null);

  const fetchPosts = async () => {
    try {
      const res = await fetch('/api/feed');
      const data = await res.json();
      if (data.posts) {
        setPosts(data.posts);
      }
    } catch (err) {
      console.error('Fetch feed error:', err);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  const handleCreatePost = async (e) => {
    e.preventDefault();
    setErrorMsg(null);
    setSuccessMsg(null);

    if (!content.trim()) {
      setErrorMsg('Lütfen bir gönderi içeriği yazın.');
      return;
    }

    setLoading(true);

    try {
      const res = await fetch('/api/feed', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          author: author.trim() || 'Anonim Hukukçu',
          role: instaHandle.trim() ? `${role} • 📸 @${instaHandle.replace('@', '')}` : role,
          category,
          content: content.trim()
        })
      });

      const data = await res.json();

      if (!res.ok || !data.success) {
        setErrorMsg(data.error || 'Gönderi paylaşılırken bir hata oluştu.');
      } else {
        setSuccessMsg('🎉 Gönderiniz başarıyla denetimden geçti ve yayınlandı!');
        setContent('');
        fetchPosts();
      }
    } catch (err) {
      setErrorMsg('Bağlantı hatası oluştu.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.eyebrow}>Hukuk Topluluğu & Canlı Forum</div>
        <h1 className={styles.title}>Hukuk Sosyal Akışı</h1>
        <p className={styles.subtitle}>
          Meslektaşlarınızla emsal kararları tartışın, mevzuat soruları sorun ve hukuki görüşlerinizi paylaşın.
        </p>
      </div>

      {/* Post Creator Box */}
      <div className={styles.createCard}>
        <h2 style={{ fontFamily: 'var(--font-serif)', color: '#F9F6F0', fontSize: '1.4rem', marginBottom: '1rem' }}>
          ✍️ Yeni Hukuki Gönderi Paylaş
        </h2>

        {errorMsg && (
          <div className={styles.errorAlert}>
            <strong>🛑 Topluluk Kuralları Engeli:</strong> {errorMsg}
          </div>
        )}

        {successMsg && (
          <div className={styles.successAlert}>
            {successMsg}
          </div>
        )}

        <form onSubmit={handleCreatePost}>
          <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem', flexWrap: 'wrap' }}>
            <input
              type="text"
              className={styles.input}
              placeholder="Adınız (Örn: Av. Selin Yılmaz)"
              value={author}
              onChange={e => setAuthor(e.target.value)}
              style={{ flex: 1, minWidth: '180px' }}
            />
            <input
              type="text"
              className={styles.input}
              placeholder="📸 Instagram (Opsiyonel: @kullaniciadi)"
              value={instaHandle}
              onChange={e => setInstaHandle(e.target.value)}
              style={{ width: '220px' }}
            />
            <select
              className={styles.select}
              value={role}
              onChange={e => setRole(e.target.value)}
              style={{ width: '200px' }}
            >
              <option value="Avukat / Hukukçu">Avukat / Hukukçu</option>
              <option value="Stajyer Avukat">Stajyer Avukat</option>
              <option value="Hukuk Fakültesi Öğrencisi">Hukuk Öğrencisi</option>
              <option value="Akademisyen">Akademisyen</option>
              <option value="Hukuksever">Hukuksever</option>
            </select>
            <select
              className={styles.select}
              value={category}
              onChange={e => setCategory(e.target.value)}
              style={{ width: '220px' }}
            >
              <option value="Emsal Karar Tartışması">Emsal Karar Tartışması</option>
              <option value="Sınav & Soru">Sınav & Soru (HMGS)</option>
              <option value="Mevzuat Gelişmesi">Mevzuat Gelişmesi</option>
              <option value="Genel Görüş">Genel Hukuki Görüş</option>
            </select>
          </div>

          <textarea
            className={styles.textarea}
            placeholder="Hukuki görüşünüzü, emsal karar tartışmanızı veya sorunuzu buraya yazın... (Otomatik spam ve +18/argo filtresi aktiftir)"
            value={content}
            onChange={e => setContent(e.target.value)}
            rows={4}
          />

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '1rem' }}>
            <span style={{ fontSize: '0.82rem', color: 'rgba(249,246,240,0.5)' }}>
              🛡️ Otomatik Küfür, Argo, +18 ve Spam Filtresi Koruması Aktiftir.
            </span>
            <button className="btn-primary" type="submit" disabled={loading}>
              {loading ? 'Denetleniyor...' : 'Gönderiyi Yayınla ↗'}
            </button>
          </div>
        </form>
      </div>

      {/* Feed List Header */}
      <h2 style={{ fontFamily: 'var(--font-serif)', color: '#F9F6F0', marginBottom: '1.5rem' }}>
        💬 Güncel Paylaşımlar
      </h2>

      {/* Posts Grid */}
      <div className={styles.postList}>
        {posts.map(post => (
          <div key={post.id} className={styles.postCard}>
            <div className={styles.postHeader}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.8rem' }}>
                <div className={styles.avatar}>{post.avatar || '⚖️'}</div>
                <div>
                  <div className={styles.authorName}>{post.author}</div>
                  <div className={styles.authorRole}>{post.role}</div>
                </div>
              </div>
              <div className={styles.categoryPill}>{post.category}</div>
            </div>

            <p className={styles.postContent}>{post.content}</p>

            <div className={styles.postFooter}>
              <span>⏱ {post.createdAt}</span>
              <div style={{ display: 'flex', gap: '1.5rem' }}>
                <span>❤️ {post.likes} Beğeni</span>
                <span>💬 {post.commentsCount} Yorum</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
