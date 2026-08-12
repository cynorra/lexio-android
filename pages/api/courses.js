import { COURSES } from '../../lib/courses';

export default function handler(req, res) {
  if (req.method !== 'GET') return res.status(405).json({ error: 'Method not allowed' });

  const { id } = req.query;

  if (id) {
    const course = COURSES.find(c => c.id === id || c.slug === id);
    if (!course) return res.status(404).json({ error: 'Course not found' });
    return res.status(200).json(course);
  }

  return res.status(200).json(COURSES);
}
