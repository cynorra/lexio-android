import { useState } from 'react';
import { useRouter } from 'next/router';
import { QUIZZES } from '../../lib/quizzes';
import styles from '../../styles/Testler.module.css';

export default function TestDetay({ quiz }) {
  const router = useRouter();
  const [currentIdx, setCurrentIdx] = useState(0);
  const [answers, setAnswers] = useState({});
  const [submitted, setSubmitted] = useState(false);
  const [evalResult, setEvalResult] = useState(null);
  const [loading, setLoading] = useState(false);

  if (!quiz) return null;

  const currentQ = quiz.questions[currentIdx];

  const handleSelectOption = (optIndex) => {
    if (submitted) return;
    setAnswers(prev => ({ ...prev, [currentQ.id]: optIndex }));
  };

  const handleNext = () => {
    if (currentIdx < quiz.questions.length - 1) {
      setCurrentIdx(currentIdx + 1);
    }
  };

  const handlePrev = () => {
    if (currentIdx > 0) {
      setCurrentIdx(currentIdx - 1);
    }
  };

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const res = await fetch('/api/evaluate-quiz', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ quizId: quiz.id, answers })
      });
      const data = await res.json();
      setEvalResult(data);
      setSubmitted(true);
    } catch (err) {
      console.error('Evaluate error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.examContainer}>
      <button className={styles.backBtn} onClick={() => router.push('/testler')}>
        ← Sınav Merkezine Dön
      </button>

      {!submitted ? (
        <div>
          <div className={styles.qMeta}>
            <span>{quiz.title} ({currentIdx + 1} / {quiz.questions.length})</span>
            <span>Kategori: {quiz.category}</span>
          </div>

          <div className={styles.questionCard}>
            <div className={styles.qText}>{currentQ.question}</div>

            <div className={styles.optionsList}>
              {currentQ.options.map((opt, i) => {
                const isSelected = answers[currentQ.id] === i;
                return (
                  <button
                    key={i}
                    className={`${styles.optionBtn} ${isSelected ? styles.selectedOption : ''}`}
                    onClick={() => handleSelectOption(i)}
                  >
                    {opt}
                  </button>
                );
              })}
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '1.5rem' }}>
            <button
              className="btn-ghost"
              style={{ color: '#F9F6F0', borderColor: 'rgba(249,246,240,0.3)' }}
              onClick={handlePrev}
              disabled={currentIdx === 0}
            >
              ← Önceki Soru
            </button>

            {currentIdx < quiz.questions.length - 1 ? (
              <button className="btn-primary" onClick={handleNext}>
                Sonraki Soru →
              </button>
            ) : (
              <button
                className="btn-primary"
                style={{ background: 'linear-gradient(135deg, #D4AF37, #B8860B)' }}
                onClick={handleSubmit}
                disabled={loading}
              >
                {loading ? 'Değerlendiriliyor...' : 'Sınavı Bitir ve Sonucu Gör 🎯'}
              </button>
            )}
          </div>
        </div>
      ) : (
        <div>
          {/* Result Summary Screen */}
          <div className={styles.resultBox}>
            <h2 style={{ fontFamily: 'var(--font-serif)', color: '#F9F6F0' }}>Sınav Raporu & Hukuki Çözümler</h2>
            <div className={styles.scoreCircle}>
              %{evalResult?.scorePercentage}
            </div>
            <p style={{ color: 'rgba(249,246,240,0.8)', fontSize: '1.1rem' }}>
              {evalResult?.passed ? '🎉 Tebrikler! HMGS baraj puanını (%70) geçtiniz.' : '⚠️ Barajın altındasınız. Çözümlerdeki mevzuatı tekrar inceleyiniz.'}
            </p>
            <div style={{ display: 'flex', justifyContent: 'center', gap: '2rem', marginTop: '1.5rem', fontSize: '0.95rem' }}>
              <span>Doğru: <strong style={{ color: '#4CAF50' }}>{evalResult?.correctCount}</strong></span>
              <span>Toplam Soru: <strong>{evalResult?.totalQuestions}</strong></span>
            </div>
          </div>

          <h3 style={{ fontFamily: 'var(--font-serif)', color: '#F9F6F0', marginBottom: '1.5rem' }}>
            Detaylı Soru Çözümleri & Yargıtay Kaynakları
          </h3>

          {evalResult?.results.map((res, idx) => (
            <div key={idx} className={styles.explanationCard} style={{ borderLeftColor: res.isCorrect ? '#4CAF50' : '#E53935' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontWeight: 600 }}>
                <span>Soru {idx + 1}: {res.isCorrect ? '✅ Doğru' : '❌ Yanlış'}</span>
                <span style={{ color: '#D4AF37' }}>{res.sourceCode}</span>
              </div>
              <p style={{ color: '#F9F6F0', marginBottom: '0.8rem', fontWeight: 500 }}>{res.questionText}</p>

              <div style={{ fontSize: '0.9rem', marginBottom: '0.5rem' }}>
                <span style={{ color: 'rgba(249,246,240,0.6)' }}>Sizin Seçiminiz: </span>
                <strong style={{ color: res.isCorrect ? '#4CAF50' : '#E53935' }}>{res.chosenOption}</strong>
              </div>

              {!res.isCorrect && (
                <div style={{ fontSize: '0.9rem', marginBottom: '0.8rem' }}>
                  <span style={{ color: 'rgba(249,246,240,0.6)' }}>Doğru Cevap: </span>
                  <strong style={{ color: '#4CAF50' }}>{res.correctOption}</strong>
                </div>
              )}

              <div className={styles.sourceBox}>
                <strong>💡 Hukuki Gerekçe:</strong> {res.explanation}
                <div style={{ marginTop: '0.5rem', fontWeight: 600 }}>📜 {res.sourceDetail}</div>
              </div>
            </div>
          ))}

          <div style={{ textAlign: 'center', marginTop: '2.5rem' }}>
            <button className="btn-primary" onClick={() => router.push('/testler')}>
              Diğer Sınavlara Göz At →
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export async function getStaticPaths() {
  return {
    paths: QUIZZES.map(q => ({ params: { id: q.id } })),
    fallback: 'blocking'
  };
}

export async function getStaticProps({ params }) {
  let quiz = QUIZZES.find(q => q.id === params.id) || null;
  if (!quiz && params.id.includes('daily')) {
    quiz = QUIZZES[0];
  }
  return { props: { quiz } };
}
