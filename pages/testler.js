import { useState } from 'react';
import { useRouter } from 'next/router';
import { QUIZZES } from '../lib/quizzes';
import styles from '../styles/Testler.module.css';

export default function Testler() {
  const router = useRouter();
  const [selectedTopic, setSelectedTopic] = useState('Ceza Hukuku - Kast Yaralama');
  const [loadingAi, setLoadingAi] = useState(false);
  const [aiQuestion, setAiQuestion] = useState(null);

  const handleGenerateAiQuestion = async () => {
    setLoadingAi(true);
    setAiQuestion(null);
    try {
      const res = await fetch('/api/generate-quiz', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ category: selectedTopic, topic: selectedTopic })
      });
      const data = await res.json();
      if (data.question) {
        setAiQuestion(data.question);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingAi(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.eyebrow}>HMGS & Adli Yargı Hakimlik</div>
        <h1 className={styles.title}>Test ve Sınav Merkezi</h1>
        <p className={styles.subtitle}>
          ÖSYM ve Adalet Bakanlığı standartlarında 5 şıklı test soruları. Her sorunun altında Yargıtay ilamı ve Kanun Maddesi gerekçesi.
        </p>
      </div>

      {/* Dynamic AI Generator Box */}
      <div className={styles.aiBox}>
        <div>
          <div className={styles.aiBoxTitle}>✨ Yapay Zeka (AI) Dinamik Soru Üreteci</div>
          <div className={styles.aiBoxDesc}>
            Gemini 2.0 Flash ile seçtiğiniz hukuk dalında veya Yargıtay emsal kararında anlık çoktan seçmeli test sorusu üretin.
          </div>
        </div>
        <div className={styles.aiControls}>
          <select
            className={styles.select}
            value={selectedTopic}
            onChange={e => setSelectedTopic(e.target.value)}
          >
            <option value="Ceza Hukuku - Kast Yaralama">Ceza Hukuku - Kast/Yaralama</option>
            <option value="Medeni Hukuk - Muris Muvazaası">Medeni Hukuk - Muris Muvazaası</option>
            <option value="Borçlar Hukuku - Haksız Fiil">Borçlar Hukuku - Haksız Fiil</option>
            <option value="Ticaret Hukuku - Bono Teminat Kaydı">Ticaret Hukuku - Senetler</option>
            <option value="Usul Hukuku - Hukuka Aykırı Delil">Usul Hukuku - Deliller</option>
          </select>
          <button className="btn-primary" onClick={handleGenerateAiQuestion} disabled={loadingAi}>
            {loadingAi ? 'Üretiliyor...' : 'Soru Üret ↗'}
          </button>
        </div>
      </div>

      {/* Render AI generated question modal/box if available */}
      {aiQuestion && (
        <div className={styles.questionCard} style={{ borderColor: '#D4AF37', marginBottom: '3rem' }}>
          <div className={styles.qMeta}>
            <span>✨ Yapay Zeka Tarafından Üretilen Soru</span>
            <span>Kaynak: {aiQuestion.sourceCode}</span>
          </div>
          <div className={styles.qText}>{aiQuestion.question}</div>
          <div className={styles.optionsList}>
            {aiQuestion.options.map((opt, i) => (
              <div key={i} className={styles.optionBtn} style={{ cursor: 'default' }}>
                {opt}
              </div>
            ))}
          </div>
          <div className={styles.sourceBox} style={{ marginTop: '1.5rem' }}>
            <strong>💡 Hukuki Açıklama & İçtihat:</strong> {aiQuestion.explanation}
            <div style={{ marginTop: '0.5rem', fontWeight: 600 }}>📜 {aiQuestion.sourceDetail}</div>
          </div>
        </div>
      )}

      <h2 style={{ fontFamily: 'var(--font-serif)', color: '#F9F6F0', marginBottom: '1.5rem' }}>
        ÖSYM & HMGS Deneme Testleri
      </h2>

      <div className={styles.grid}>
        {QUIZZES.map(quiz => (
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
