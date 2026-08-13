import { useRef } from 'react';
import styles from '../styles/InstagramStoryCard.module.css';

export default function InstagramStoryCard({ user, caseTitle, score, streak, rank, instagramHandle, onClose }) {
  const cardRef = useRef(null);

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: 'Lexio Hukuk Başarısı',
        text: `Lexio'da ${caseTitle || 'Dava Simülasyonu'} başarısı! Skor: %${score || 85} ⚖️`,
        url: window.location.href
      }).catch(() => {});
    } else {
      alert('📸 Görsel veya bağlantı kopyalandı! Instagram Hikayenizde paylaşabilirsiniz.');
    }
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
        <button className={styles.closeBtn} onClick={onClose}>×</button>
        
        {/* 9:16 Instagram Story Canvas Template */}
        <div className={styles.storyCanvas} ref={cardRef}>
          <div className={styles.storyHeader}>
            <div className={styles.brandMark}>Lex<span>io</span></div>
            <div className={styles.storyBadge}>Hukuk Akademi</div>
          </div>

          <div className={styles.storyBody}>
            <div className={styles.trophyIcon}>🏆</div>
            <div className={styles.achievementTitle}>
              {caseTitle ? 'DAVA SİMÜLASYONU BAŞARISI' : 'HMGS & HUKUK PERFORMANSI'}
            </div>

            {caseTitle && (
              <div className={styles.caseName}>"{caseTitle}"</div>
            )}

            <div className={styles.scoreCircle}>
              <span className={styles.scoreVal}>%{score || 85}</span>
              <span className={styles.scoreLbl}>Hukuki Başarı</span>
            </div>

            <div className={styles.statsRow}>
              <div className={styles.statBox}>
                <span className={styles.statIcon}>🔥</span>
                <span className={styles.statVal}>{streak || 7} Gün</span>
                <span className={styles.statLbl}>Seri (Streak)</span>
              </div>
              <div className={styles.statBox}>
                <span className={styles.statIcon}>⚖️</span>
                <span className={styles.statVal}>{rank || 'Kıdemli Avukat'}</span>
                <span className={styles.statLbl}>Unvan</span>
              </div>
            </div>

            <div className={styles.userInfo}>
              <div className={styles.userName}>{user?.name || 'Av. Mehmet Yılmaz'}</div>
              {instagramHandle && (
                <div className={styles.instaTag}>📸 @{instagramHandle.replace('@', '')}</div>
              )}
            </div>
          </div>

          <div className={styles.storyFooter}>
            <span>www.lexio.app • Türkiye’nin İlk AI Hukuk Platformu</span>
          </div>
        </div>

        {/* Story Action Controls */}
        <div className={styles.actionRow}>
          <button className="btn-primary" style={{ background: 'linear-gradient(135deg, #833AB4, #FD1D1D, #F56040)' }} onClick={handleShare}>
            📸 Instagram Hikayede Paylaş
          </button>
        </div>
      </div>
    </div>
  );
}
