import { QUIZZES } from '../../lib/quizzes';

export default function handler(req, res) {
  if (req.method !== 'GET') return res.status(405).json({ error: 'Method not allowed' });

  const { id, courseId } = req.query;

  if (id) {
    const quiz = QUIZZES.find(q => q.id === id);
    if (!quiz) return res.status(404).json({ error: 'Quiz not found' });
    return res.status(200).json(quiz);
  }

  if (courseId) {
    const list = QUIZZES.filter(q => q.courseId === courseId);
    return res.status(200).json(list);
  }

  return res.status(200).json(QUIZZES);
}
