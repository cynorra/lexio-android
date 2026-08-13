import { getFeedPosts, createFeedPost } from '../../lib/feed';

export default function handler(req, res) {
  if (req.method === 'GET') {
    const posts = getFeedPosts();
    return res.status(200).json({ success: true, posts });
  }

  if (req.method === 'POST') {
    const { author, role, content, category } = req.body;

    const result = createFeedPost({ author, role, content, category });
    if (!result.success) {
      return res.status(400).json({ success: false, error: result.error });
    }

    return res.status(201).json({ success: true, post: result.post, message: 'Gönderiniz başarıyla yayınlandı.' });
  }

  return res.status(405).json({ error: 'Method not allowed' });
}
