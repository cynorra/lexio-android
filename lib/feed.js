import { moderateText } from './moderation';

export const INITIAL_POSTS = [
  {
    id: 'post-1',
    author: 'Av. Mehmet Yılmaz',
    role: 'Ceza Hukuku Uzmanı',
    avatar: '⚖️',
    category: 'Emsal Karar Tartışması',
    content: 'Yargıtay 1. Ceza Dairesi’nin son kararında kasten öldürmeye teşebbüs ile yaralama ayrımında darbe sayısından ziyade hayati organ hedeflemesi öne çıkarıldı. Dosyalarınızda bu içtihata atıf yapmayı unutmayın!',
    createdAt: '2 saat önce',
    likes: 24,
    commentsCount: 5
  },
  {
    id: 'post-2',
    author: 'Stj. Av. Zeynep Kaya',
    role: 'HMGS Hazırlık',
    avatar: '🎓',
    category: 'Sınav & Soru',
    content: 'HMGS sınavında Medeni Hukuk muris muvazaası sorularında 01.04.1974 t. İBK gerekçesi sorulabiliyor. Saklı pay sahibi olmayan mirasçıların da dava açma hakkı olduğunu unutmayın.',
    createdAt: '5 saat önce',
    likes: 42,
    commentsCount: 12
  },
  {
    id: 'post-3',
    author: 'Dr. Ahmet Demir',
    role: 'Borçlar Hukuku Öğretim Üyesi',
    avatar: '🏛',
    category: 'Mevzuat Gelişmesi',
    content: 'TBK m.49 haksız fiil sorumluluğunda kusur aranırken kanuni kusursuz sorumluluk halleri (adam çalıştıranın sorumluluğu vb.) istisna teşkil etmektedir.',
    createdAt: '1 gün önce',
    likes: 38,
    commentsCount: 3
  }
];

export let FEED_STORE = [...INITIAL_POSTS];

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
