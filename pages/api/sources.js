import { searchLiveOnlineLegalSources } from '../../lib/legalApis';

export default async function handler(req, res) {
  if (req.method !== 'GET' && req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const country = req.query.country || req.body?.country || 'TR';
  const q = req.query.q || req.body?.q || '';

  try {
    const liveData = await searchLiveOnlineLegalSources({ country, query: q });
    return res.status(200).json(liveData);
  } catch (err) {
    console.error('API /api/sources Error:', err);
    return res.status(500).json({ error: 'Failed to fetch live legal sources from internet' });
  }
}
