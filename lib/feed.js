import { moderateText } from './moderation';

export let FEED_STORE = [];

export function getFeedPosts() {
  return FEED_STORE;
}

export function createFeedPost({ author, role, content, category }) {
  const moderation = moderateText(content);
  if (!moderation.isValid) {
    return { success: false, error: moderation.reason };
  }

  const newPost = {
    id: `post-${Date.now()}`,
    author: author || 'Anonim Hukukçu',
    role: role || 'Hukuksever',
    avatar: '⚖️',
    category: category || 'Genel Hukuk Tartışması',
    content: moderation.cleanText,
    createdAt: 'Az önce',
    likes: 0,
    commentsCount: 0
  };

  FEED_STORE.unshift(newPost);
  return { success: true, post: newPost };
}
