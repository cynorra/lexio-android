import { QUIZZES } from '../../lib/quizzes';

export default function handler(req, res) {
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  const { quizId, answers } = req.body;
  if (!quizId || !answers) return res.status(400).json({ error: 'quizId and answers required' });

  const quiz = QUIZZES.find(q => q.id === quizId);
  if (!quiz) return res.status(404).json({ error: 'Quiz not found' });

  let correctCount = 0;
  const total = quiz.questions.length;

  const results = quiz.questions.map(q => {
    const chosen = answers[q.id];
    const isCorrect = chosen === q.correctIndex;
    if (isCorrect) correctCount++;

    return {
      questionId: q.id,
      questionText: q.question,
      chosenOption: chosen !== undefined ? q.options[chosen] : 'Boş bırakıldı',
      correctOption: q.options[q.correctIndex],
      isCorrect,
      explanation: q.explanation,
      sourceCode: q.sourceCode,
      sourceDetail: q.sourceDetail
    };
  });

  const scorePercentage = Math.round((correctCount / total) * 100);
  const passed = scorePercentage >= 70; // HMGS passing mark is 70%

  return res.status(200).json({
    quizTitle: quiz.title,
    category: quiz.category,
    correctCount,
    totalQuestions: total,
    scorePercentage,
    passed,
    results
  });
}
