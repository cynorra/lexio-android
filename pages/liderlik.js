import { useState } from 'react';
import styles from '../styles/Liderlik.module.css';

export default function Liderlik() {
  const [period, setPeriod] = useState('daily');
  // Rankings live in the mobile app (Firestore); the website has no data source, so nothing is faked here.
  const leaderboard = [];

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.eyebrow}>Hukukçular Yarışıyor</div>
        <h1 className={styles.title}>🏆 Liderlik Tablosu &amp; Sıralama</h1>
        <p className={styles.subtitle}>
          Dava simülasyonları çözerek ve testlerde başarı göstererek puan kazanın, Türkiye geneli sıralamada zirveye tırmanın!
        </p>

        {/* Period Selector Tabs */}
        <div className={styles.tabRow}>
          <button
            className={`${styles.tabBtn} ${period === 'daily' ? styles.activeTab : ''}`}
            onClick={() => setPeriod('daily')}
          >
            🔥 Günlük
          </button>
          <button
            className={`${styles.tabBtn} ${period === 'weekly' ? styles.activeTab : ''}`}
            onClick={() => setPeriod('weekly')}
          >
            📅 Haftalık
          </button>
          <button
            className={`${styles.tabBtn} ${period === 'monthly' ? styles.activeTab : ''}`}
            onClick={() => setPeriod('monthly')}
          >
            🏆 Aylık
          </button>
        </div>
      </div>

      {/* Leaderboard Table Card */}
      <div className={styles.card}>
        <div className={styles.tableHeader}>
          <span style={{ width: '60px', textAlign: 'center' }}>Sıra</span>
          <span style={{ flex: 1 }}>Hukukçu</span>
          <span style={{ width: '140px', textAlign: 'center' }}>Unvan</span>
          <span style={{ width: '100px', textAlign: 'center' }}>Dava</span>
          <span style={{ width: '100px', textAlign: 'right' }}>Puan</span>
        </div>

        {leaderboard.length === 0 && (
          <p className={styles.subtitle} style={{ textAlign: 'center', padding: '32px 16px' }}>
            Canlı sıralama Lexio mobil uygulamasında tutulur. Puan kazanmak ve sıralamaya girmek için uygulamayı indirin.
          </p>
        )}

        <div className={styles.userList}>
          {leaderboard.map(user => (
            <div
              key={user.rank}
              className={`${styles.userRow} ${user.isUser ? styles.highlightRow : ''}`}
            >
              <div className={styles.rankBadge}>
                {user.rank === 1 ? '🥇 1' : user.rank === 2 ? '🥈 2' : user.rank === 3 ? '🥉 3' : `#${user.rank}`}
              </div>

              <div className={styles.userInfo}>
                <span className={styles.avatar}>{user.avatar}</span>
                <span className={styles.userName}>{user.name}</span>
              </div>

              <div className={styles.titleBadge}>{user.title}</div>
              <div className={styles.casesCount}>{user.casesSolved} Dava</div>
              <div className={styles.scoreVal}>{user.score} P</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
