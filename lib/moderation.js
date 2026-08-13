/**
 * Content Moderation & Spam Protection Engine
 * Filters profanity, offensive language, +18 content, and spam.
 */

const FORBIDDEN_WORDS = [
  // Profanity & offensive words (Turkish & English)
  'amk', 'aq', 'amq', 'sik', 'sikerim', 'sikiş', 'piç', 'orospu', 'göt', 'yarrak', 'yarak', 'ibne',
  'puşt', 'dalyarak', 'fuck', 'shit', 'bitch', 'asshole', 'bastard', 'cunt', 'dick', 'pussy',
  // +18 & NSFW terms
  'porn', 'porno', 'sex', 'seks', '+18', 'nude', 'escort', 'eskort', 'bet', 'kumar', 'bahis'
];

export function moderateText(text) {
  if (!text || typeof text !== 'string') {
    return { isValid: false, reason: 'Gönderi metni boş olamaz.' };
  }

  const trimmed = text.trim();
  if (trimmed.length < 5) {
    return { isValid: false, reason: 'Gönderi en az 5 karakter olmalıdır.' };
  }

  if (trimmed.length > 1000) {
    return { isValid: false, reason: 'Gönderi en fazla 1000 karakter olabilir.' };
  }

  const lower = trimmed.toLowerCase();

  // 1. Check for profanity and forbidden terms
  for (const word of FORBIDDEN_WORDS) {
    // Exact word or substring check
    const regex = new RegExp(`\\b${word}\\b`, 'i');
    if (regex.test(lower) || lower.includes(word)) {
      return {
        isValid: false,
        reason: 'Gönderiniz topluluk kurallarına aykırı öğeler (argo, küfür veya +18 içerik) içermektedir.'
      };
    }
  }

  // 2. Check for excessive URL spam
  const urlCount = (lower.match(/https?:\/\//g) || []).length;
  if (urlCount > 2) {
    return { isValid: false, reason: 'Gönderi içerisinde çok fazla bağlantı (spam) tespit edildi.' };
  }

  // 3. Check for repetitive character spam (e.g. "aaaaaa...")
  if (/(.)\1{9,}/.test(lower)) {
    return { isValid: false, reason: 'Spam karakter harf tekrarı tespit edildi.' };
  }

  return { isValid: true, cleanText: trimmed };
}
