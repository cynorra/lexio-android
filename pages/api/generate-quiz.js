import { QUIZZES } from '../../lib/quizzes';

export default function handler(req, res) {
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  const { category } = req.body;

  let questions = [];
  QUIZZES.forEach(q => {
    if (!category || category === 'hepsi' || q.category.toLowerCase().includes(category.toLowerCase())) {
      questions.push(...q.questions);
    }
  });

  if (questions.length === 0) {
    questions = QUIZZES[0].questions;
  }

  const randomIndex = Math.floor(Math.random() * questions.length);
  const selectedQuestion = questions[randomIndex];

  return res.status(200).json({
    question: selectedQuestion,
    source: 'Lexio Hukuk Veritabanı (Backend Soru Bankası)'
  });
}

