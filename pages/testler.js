import { useState } from 'react';
import { useRouter } from 'next/router';
import { QUIZZES } from '../lib/quizzes';
import styles from '../styles/Testler.module.css';

export default function Testler() {
  const router = useRouter();
  const [selectedCategory, setSelectedCategory] = useState('hepsi');
  const [loadingBackend, setLoadingBackend] = useState(false);
  const [randomQuestion, setRandomQuestion] = useState(null);

  const handleFetchBackendQuestion = async () => {
    setLoadingBackend(true);
    setRandomQuestion(null);
    try {
      const res = await fetch('/api/generate-quiz', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ category: selectedCategory })
      });
      const data = await res.json();
      if (data.question) {
        setRandomQuestion(data.question);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingBackend(false);
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
          ÖSYM ve Adalet Bakanlığı standartlarında 5 şıklı test soruları. Her sorunun altında Yargıtay ilamı ve Kanun Maddesi gerekçesi.
        </p>
      </div>

      {/* Backend Soru Bankası Hızlı Soru Getirici */}
      <div className={styles.aiBox}>
        <div>
          <div className={styles.aiBoxTitle}>🏛 Hukuk Veritabanı Hızlı Soru Getirici</div>
          <div className={styles.aiBoxDesc}>
            Seçtiğiniz hukuk dalında backend soru bankasından rastgele mevzuat ve Yargıtay emsal sorusu getirin.
          </div>
        </div>
        <div className={styles.aiControls}>
          <select
            className={styles.select}
            value={selectedCategory}
            onChange={e => setSelectedCategory(e.target.value)}
          >
            <option value="hepsi">Tüm Hukuk Dalları</option>
            <option value="Ceza Hukuku">Ceza Hukuku</option>
            <option value="Medeni Hukuk">Medeni Hukuk</option>
            <option value="Borçlar Hukuku">Borçlar Hukuku</option>
          </select>
          <button className="btn-primary" onClick={handleFetchBackendQuestion} disabled={loadingBackend}>
            {loadingBackend ? 'Yükleniyor...' : 'Soru Getir ↗'}
          </button>
        </div>
      </div>

      {/* Render Backend question if fetched */}
      {randomQuestion && (
        <div className={styles.questionCard} style={{ borderColor: '#D4AF37', marginBottom: '3rem' }}>
          <div className={styles.qMeta}>
            <span>🏛 Hukuk Veritabanı Soru Bankası</span>
            <span>Kaynak: {randomQuestion.sourceCode}</span>
          </div>
          <div className={styles.qText}>{randomQuestion.question}</div>
          <div className={styles.optionsList}>
            {randomQuestion.options.map((opt, i) => (
              <div key={i} className={styles.optionBtn} style={{ cursor: 'default' }}>
                {opt}
              </div>
            ))}
          </div>
          <div className={styles.sourceBox} style={{ marginTop: '1.5rem' }}>
            <strong>💡 Hukuki Gerekçe:</strong> {randomQuestion.explanation}
            <div style={{ marginTop: '0.5rem', fontWeight: 600 }}>📜 {randomQuestion.sourceDetail}</div>
          </div>
        </div>
      )}

      <h2 style={{ fontFamily: 'var(--font-serif)', color: '#F9F6F0', marginBottom: '1.5rem' }}>
        ÖSYM & HMGS Deneme Testleri
      </h2>

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

