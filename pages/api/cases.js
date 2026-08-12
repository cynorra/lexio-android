import { CASES } from '../../lib/cases';

export default function handler(req, res) {
  if (req.method !== 'GET') return res.status(405).json({ error: 'Method not allowed' });

  const { tag, id } = req.query;

  if (id) {
    const caseItem = CASES.find(c => String(c.id) === String(id));
    if (!caseItem) return res.status(404).json({ error: 'Case not found' });
    return res.status(200).json(caseItem);
  }

  let filtered = CASES;
  if (tag && tag !== 'hepsi') {
    filtered = CASES.filter(c => c.tag === tag);
  }

  return res.status(200).json(filtered);
}
