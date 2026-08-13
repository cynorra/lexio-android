export const LEADERBOARD_USERS = [
  { rank: 1, name: 'Av. Selin Yılmaz', score: 2840, casesSolved: 42, title: 'Yargıç (Senior Judge)', avatar: '⚖️', isUser: false },
  { rank: 2, name: 'Stj. Av. Caner Erdem', score: 2450, casesSolved: 36, title: 'Kıdemli Avukat', avatar: '🎓', isUser: false },
  { rank: 3, name: 'Dr. Ahmet Demir', score: 2100, casesSolved: 30, title: 'Akademisyen', avatar: '🏛', isUser: false },
  { rank: 4, name: 'Av. Zeynep Kaya (Sen)', score: 1850, casesSolved: 25, title: 'Kıdemli Avukat', avatar: '📸', isUser: true },
  { rank: 5, name: 'Hukukçu Mehmet Can', score: 1620, casesSolved: 21, title: 'Avukat', avatar: '⚖️', isUser: false },
  { rank: 6, name: 'Stj. Av. Ayşe Öztürk', score: 1400, casesSolved: 18, title: 'Stajyer Avukat', avatar: '🎓', isUser: false },
  { rank: 7, name: 'Burak Şahin', score: 1250, casesSolved: 15, title: 'Hukuk Öğrencisi', avatar: '📚', isUser: false }
];

export function getLeaderboard(period = 'daily') {
  if (period === 'weekly') {
    return LEADERBOARD_USERS.map(u => ({ ...u, score: Math.round(u.score * 0.8) }));
  }
  if (period === 'monthly') {
    return LEADERBOARD_USERS.map(u => ({ ...u, score: Math.round(u.score * 2.5) }));
  }
  return LEADERBOARD_USERS;
}
