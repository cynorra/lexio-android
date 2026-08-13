import { searchLiveOnlineLegalSources } from '../../../lib/legalApis';
import { QUIZZES } from '../../../lib/quizzes';

export default async function handler(req, res) {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const country = (req.body.country || 'TR').toUpperCase();
  const apiKey = process.env.GEMINI_API_KEY;

  try {
    // 1. Fetch live legal source data from internet API
    const liveLegalData = await searchLiveOnlineLegalSources({ country, query: 'law' });
    const rawSources = liveLegalData.sources.slice(0, 5);

    let generatedQuestions = [];

    if (apiKey && rawSources.length > 0) {
      // 2. Pass legal sources to Gemini 2.0 Flash to synthesize ÖSYM/HMGS exam questions
      const prompt = `Sen Türk Hukuku ve Uluslararası Hukuk Profesörüsün.
Aşağıdaki CANLI HUKUKİ KAYNAKLARI (Kanun maddeleri ve mahkeme emsalleri) kullanarak, ÖSYM / HMGS standartlarında tam 3 adet 5 şıklı orijinal çoktan seçmeli test sorusu hazırla.

HUKUKİ KAYNAKLAR:
${JSON.stringify(rawSources, null, 2)}

ÇIKTIYI SADECE AŞAĞIDAKİ JSON DİZİSİ (ARRAY) FORMATINDA VER (başka hiç metin yazma):
[
  {
    "id": "daily-q1",
    "question": "Soru metni (Pratik olay veya mevzuat sorusu)",
    "options": ["A) ...", "B) ...", "C) ...", "D) ...", "E) ..."],
    "correctIndex": 0,
    "explanation": "Detaylı gerekçeli hukuki açıklama",
    "sourceCode": "İlgili Kanun Maddesi veya Karar Esas No",
    "sourceDetail": "Maddenin açık metni veya ilkesi"
  }
]`;

      const geminiRes = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [{ role: 'user', parts: [{ text: prompt }] }],
            generationConfig: {
              temperature: 0.3,
              responseMimeType: "application/json"
            }
          })
        }
      );

      const geminiData = await geminiRes.json();
      const rawText = geminiData.candidates?.[0]?.content?.parts?.[0]?.text;

      if (rawText) {
        generatedQuestions = JSON.parse(rawText);
      }
    }

    // Fallback if Gemini key is missing or error: Synthesize from backend legal sources directly
    if (!generatedQuestions || generatedQuestions.length === 0) {
      generatedQuestions = rawSources.map((src, idx) => ({
        id: `daily-q-${idx + 1}`,
        question: `${src.lawName} kapsamındaki ${src.code} uyarınca, ${src.text.slice(0, 120)}... Hangi ilke esastır?`,
        options: [
          `A) ${src.article || src.code} ilkesinin doğrudan uygulanması`,
          'B) Eylemin taksirli sayılarak cezasız bırakılması',
          'C) Şahsi cezasızlık sebebinin bulunması',
          'D) Zamanaşımının dolması sebebiyle davanın düşmesi',
          'E) Görevsizlik kararı verilerek dosyanın iadesi'
        ],
        correctIndex: 0,
        explanation: `${src.code} açık hükmü ve içtihatlar uyarınca eylemde A şıkkındaki ilke esas alınır.`,
        sourceCode: src.code,
        sourceDetail: src.lawName
      }));
    }

    // 3. Save / Register into Backend Central Quiz Store for everyone
    const dailyQuizId = `quiz-daily-${country.toLowerCase()}`;
    let existingQuiz = QUIZZES.find(q => q.id === dailyQuizId);

    if (existingQuiz) {
      existingQuiz.questions = generatedQuestions;
    } else {
      QUIZZES.unshift({
        id: dailyQuizId,
        courseId: 'günlük-test',
        title: `🔥 Günün Ortak Sınavı (${country})`,
        category: `Günün Testi (${country})`,
        examType: 'HMGS & Adli Yargı',
        questions: generatedQuestions
      });
    }

    return res.status(200).json({
      success: true,
      message: 'Günün ortak testi başarıyla Gemini ve Hukuk API ile üretilip backend havuzuna kaydedildi.',
      quizId: dailyQuizId,
      questionsCount: generatedQuestions.length,
      questions: generatedQuestions
    });

  } catch (err) {
    console.error('Generate Daily Quiz Error:', err);
    return res.status(500).json({ error: 'Günün testi üretilirken hata oluştu.' });
  }
}
