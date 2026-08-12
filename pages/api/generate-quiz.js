import { QUIZZES } from '../../lib/quizzes';

export default async function handler(req, res) {
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  const { category, topic } = req.body;
  const apiKey = process.env.GEMINI_API_KEY;

  if (!apiKey) {
    // Return fallback question from QUIZZES
    const fallbackQuiz = QUIZZES[0];
    return res.status(200).json({
      question: fallbackQuiz.questions[0],
      source: 'Local Quiz Bank (API Key missing)'
    });
  }

  const prompt = `Sen Türk Hukuku uzmanı, HMGS (Hukuk Mesleklerine Giriş Sınavı) ve Adli Yargı Hakimlik Sınavı komite üyesisin.
KATEGORİ: ${category || 'Ceza Hukuku'}
KONU: ${topic || 'Kasten Adam Öldürmeye Teşebbüs vs Yaralama'}

Lütfen bu konuda tam ÖSYM / HMGS standartlarında 1 adet zor ve olay bazlı (pratik çalışmalı) çoktan seçmeli test sorusu üret.
Soru 5 şıktan (A, B, C, D, E) oluşmalıdır.

ÇIKTIYI SADECE AŞAĞIDAKİ JSON FORMATINDA VER (başka hiç metin yazma):
{
  "id": "ai-gen-1",
  "question": "Soru metni (Pratik olay ve hukuki soru)",
  "options": [
    "A) Şık 1",
    "B) Şık 2",
    "C) Şık 3",
    "D) Şık 4",
    "E) Şık 5"
  ],
  "correctIndex": 0,
  "explanation": "Detaylı hukuki gerekçe ve Yargıtay emsal içtihat mantığı",
  "sourceCode": "Kanun Maddesi veya Yargıtay Karar Esas/Karar No (örn: TCK m.81 & Yargıtay 1. CD 2021/...)",
  "sourceDetail": "Kanun maddesinin açıklaması ve emsal kararın temel ilkesi"
}`;

  try {
    const response = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ role: 'user', parts: [{ text: prompt }] }],
          generationConfig: {
            temperature: 0.4,
            responseMimeType: "application/json"
          }
        })
      }
    );

    const data = await response.json();
    const rawText = data.candidates?.[0]?.content?.parts?.[0]?.text;

    if (!rawText) {
      throw new Error('Empty response from Gemini');
    }

    const questionObj = JSON.parse(rawText);
    return res.status(200).json({ question: questionObj, source: 'Gemini 2.0 Flash AI' });

  } catch (err) {
    console.error('Generate quiz error:', err);
    // Fallback
    const fallbackQuiz = QUIZZES[0];
    return res.status(200).json({
      question: fallbackQuiz.questions[0],
      source: 'Fallback local bank'
    });
  }
}
