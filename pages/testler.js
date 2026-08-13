import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { QUIZZES } from '../lib/quizzes';
import styles from '../styles/Testler.module.css';

export default function Testler() {
  const router = useRouter();
  const [selectedCategory, setSelectedCategory] = useState('hepsi');
  const [loadingPipeline, setLoadingPipeline] = useState(false);
  const [dailyQuizInfo, setDailyQuizInfo] = useState(null);

  useEffect(() => {
    fetch('/api/daily-quiz?country=TR')
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          setDailyQuizInfo(data);
        }
      })
      .catch(console.error);
  }, []);

  const handleGenerateBackendDailyQuiz = async () => {
    setLoadingPipeline(true);
    try {
      const res = await fetch('/api/admin/generate-daily-quiz', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ country: 'TR' })
      });
      const data = await res.json();
      if (data.success) {
        alert('✅ Günün testi canlı mevzuat kaynaklarından Gemini ve Hukuk API ile başarıyla üretilip backend sunucusuna kaydedildi. Artık tüm kullanıcılar bu testi çözecek!');
        router.reload();
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingPipeline(false);
    }
  };

  const filteredQuizzes = selectedCategory === 'hepsi'
    ? QUIZZES
    : QUIZZES.filter(q => q.category.toLowerCase().includes(selectedCategory.toLowerCase()));

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.eyebrow}>HMGS & Adli Yargı Hakimlik Sınav Bankası</div>
        <h1 className={styles.title}>Test ve Sınav Merkezi</h1>
        <p className={styles.subtitle}>
          Hukuki kaynaklardan otomatik derlenen ve sunucu üzerinde saklanan güncel ortak sınavlar. Tüm kullanıcılar için senkronize test bankası.
        </p>
      </div>

      {/* Shared Daily Quiz Banner */}
      {dailyQuizInfo && (
        <div className={styles.aiBox} style={{ background: 'linear-gradient(135deg, rgba(212, 175, 55, 0.2), rgba(22, 28, 42, 0.95))', borderColor: '#D4AF37' }}>
          <div>
            <div className={styles.aiBoxTitle}>🔥 Günün Ortak Testi (Tüm Kullanıcılar İçin Canlı Sınav)</div>
            <div className={styles.aiBoxDesc}>
              {dailyQuizInfo.title} • {dailyQuizInfo.questionsCount} Soru • Canlı Hukuk API ve Mevzuat Kaynaklı
            </div>
          </div>
          <div className={styles.aiControls}>
            <button
              className="btn-primary"
              style={{ background: 'linear-gradient(135deg, #D4AF37, #B8860B)' }}
              onClick={() => router.push(`/test/${dailyQuizInfo.quizId || 'quiz-ceza-1'}`)}
            >
              Günün Testini Çöz 🎯
            </button>
            <button
              className="btn-ghost"
              style={{ color: '#F9F6F0', borderColor: 'rgba(212,175,55,0.4)', fontSize: '0.85rem' }}
              onClick={handleGenerateBackendDailyQuiz}
              disabled={loadingPipeline}
            >
              {loadingPipeline ? 'Sunucu Soru Üretiyor...' : '⚡ Günün Testini Yenile (Backend Pipeline)'}
            </button>
          </div>
        </div>
      )}

      {/* Category Filter Controls */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <h2 style={{ fontFamily: 'var(--font-serif)', color: '#F9F6F0', margin: 0 }}>
          ÖSYM & HMGS Deneme Testleri
        </h2>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          {['hepsi', 'Ceza Hukuku', 'Medeni Hukuk', 'Borçlar Hukuku'].map(cat => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              style={{
                background: selectedCategory === cat ? 'rgba(212, 175, 55, 0.2)' : 'rgba(22, 28, 42, 0.6)',
                border: `1px solid ${selectedCategory === cat ? '#D4AF37' : 'rgba(249, 246, 240, 0.15)'}`,
                color: selectedCategory === cat ? '#D4AF37' : 'rgba(249, 246, 240, 0.8)',
                padding: '0.4rem 0.8rem',
                borderRadius: '16px',
                cursor: 'pointer',
                fontSize: '0.85rem'
              }}
            >
              {cat === 'hepsi' ? 'Tüm Testler' : cat}
            </button>
          ))}
        </div>
      </div>

      <div className={styles.grid}>
        {filteredQuizzes.map(quiz => (
          <div
            key={quiz.id}
            className={styles.quizCard}
            onClick={() => router.push(`/test/${quiz.id}`)}
          >
            <div>
              <span className={styles.quizTag}>{quiz.category} • {quiz.examType}</span>
              <h3 className={styles.quizTitle}>{quiz.title}</h3>
            </div>
            <div className={styles.quizMeta}>
              <span>📋 {quiz.questions.length} Soru</span>
              <span>⏱ ~10 dk</span>
              <span style={{ color: '#D4AF37' }}>Sınava Başla →</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
