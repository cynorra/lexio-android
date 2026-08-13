import { QUIZZES } from '../../lib/quizzes';

export default function handler(req, res) {
  const country = (req.query.country || req.body?.country || 'TR').toUpperCase();
  const dateStr = req.query.date || new Date().toISOString().split('T')[0];

  // 1. Check if a quiz matching the jurisdiction exists in backend store
  let matchingQuiz = QUIZZES.find(q => 
    q.id.includes(`daily-${country.toLowerCase()}`) || 
    q.category.toUpperCase().includes(country)
  );

  if (!matchingQuiz) {
    matchingQuiz = QUIZZES[0];
  }

  return res.status(200).json({
    success: true,
    quizId: matchingQuiz.id || `daily-${country.toLowerCase()}-${dateStr}`,
    date: dateStr,
    countryCode: country,
    title: matchingQuiz.title || `🔥 Günün Ortak Hukuk Testi (${country}) — ${dateStr}`,
    examType: matchingQuiz.examType || 'HMGS, ÖSYM & Adli Yargı Uyumlu',
    questionsCount: matchingQuiz.questions.length,
    questions: matchingQuiz.questions
  });
}
