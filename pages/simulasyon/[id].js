import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/router';
import { CASES } from '../../lib/cases';
import styles from '../../styles/Simulasyon.module.css';
import InstagramStoryCard from '../../components/InstagramStoryCard';

const FALLBACK = {
  savunma: [
    'Savcılık olarak belirtmeliyim ki savunmanın argümanları kastın temel unsurunu tartışmaktan kaçınmaktadır. Yargıtay CGK kararlarında vurgunun yapıldığı bölge ve hareketin yoğunluğu bir bütün olarak değerlendirilmektedir. Mevcut deliller suç kastını açıkça ortaya koymaktadır.',
    'Savunmanın delilleri çarpıtma girişimi dikkat çekicidir. Adli bulgular hayati tehlikeyi açıkça ortaya koymuştur. Nesnel delillerin bütünü sanığın iddialarını çürütmektedir.',
    'Kapanışta şunu vurgulamalıyım: Yargıtay benzer davalarda sanığın beyanına değil nesnel delillere itibar etmiştir. Delillerin bütünü mahkumiyete işaret etmektedir.'
  ],
  savci: [
    'Savcılığın kasıt iddiasına karşı şunu söylemeliyim: Türk hukukunda kast failin iç dünyasına ilişkin bir unsurdur. Müvekkilimin sözleri öfke anında söylenmiş, somut eyleme yansımamıştır. Yargıtay ani öfke ile tasarlanmış kastı birbirinden net biçimde ayırt etmektedir.',
    'Savcılığın dayandığı tanık ifadesi çelişkilidir ve şüphe yaratmaktadır. Şüpheden sanık yararlanır ilkesi gereği mahkumiyet kararı verilemez.',
    'Kapanış beyanımda şunu belirtmeliyim: Deliller şüpheye yer vermeyecek biçimde suçu ispat edememektedir. Müvekkilim için beraat kararı hukuka ve vicdana uygun tek karardır.'
  ]
};

export default function Simulasyon({ vaka }) {
  const router = useRouter();
  const { rol } = router.query;
  const chatRef = useRef(null);

  const [messages, setMessages] = useState([]);
  const [history, setHistory] = useState([]);
  const [input, setInput] = useState('');
  const [step, setStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [done, setDone] = useState(false);
  const [showStoryModal, setShowStoryModal] = useState(false);

  useEffect(() => {
    if (!vaka || !rol) return;
    const aiRole = rol === 'savunma' ? 'Savcı' : 'Savunma Avukatı';
    const opening = rol === 'savunma'
      ? `Sayın Mahkeme, Savcılık sıfatıyla bu duruşmada müttehem aleyhine yeterli delil olduğunu ispatlayacağız. Deliller incelendiğinde eylemin yasal unsurları tam olarak taşıdığı görülecektir. Savunmanın argümanlarını bekliyoruz.`
      : `Sayın Mahkeme, müdafi olarak şunu belirtmeliyim: İddianame yüzeysel değerlendirmeye dayanmaktadır. Delillerin bütünlüklü değerlendirilmesi müvekkilimin iddia edilen suçu işlemediğini ortaya koyacaktır.`;

    setMessages([
      { type: 'system', role: 'Duruşma Başladı', text: `<strong>${vaka.title}</strong> — ${vaka.ref}` },
      { type: 'system', role: 'Vaka Özeti', text: vaka.full },
      { type: 'system', role: 'Deliller', text: vaka.evidence.map(e => `• ${e}`).join('<br/>') },
      { type: 'ai', role: `AI ${aiRole}`, text: opening }
    ]);
  }, [vaka, rol]);

  useEffect(() => {
    if (chatRef.current) chatRef.current.scrollTop = chatRef.current.scrollHeight;
  }, [messages, loading]);

  const sendArgument = async () => {
    if (!input.trim() || loading || done) return;
    const text = input.trim();
    setInput('');

    const userLabel = rol === 'savunma' ? 'Savunma Avukatı' : 'Savcı';
    const newHistory = [...history, { role: 'user', content: text }];
    setHistory(newHistory);
    setMessages(prev => [...prev, { type: 'user', role: userLabel, text }]);
    setLoading(true);

    const aiRole = rol === 'savunma' ? 'Savcı' : 'Savunma Avukatı';
    const isFinal = step >= vaka.steps.length - 2;
    const systemPrompt = `Sen ${aiRole} rolünde bir AI'sın. Türk Ceza Hukuku uzmanısın.
DAVA: ${vaka.title}
DETAY: ${vaka.full}
DELİLLER: ${vaka.evidence.join('; ')}
MEVZUAT: ${vaka.ref}
Karşı tarafın argümanlarına hukuki olarak yanıt ver. Yargıtay içtihadından örnekler ver. 2-3 paragraf, Türkçe.${isFinal ? ' Bu son round, güçlü bir kapanış yap.' : ''}`;

    let aiText = null;
    try {
      const res = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ messages: newHistory, system: systemPrompt })
      });
      const data = await res.json();
      aiText = data.text || null;
    } catch {}

    if (!aiText) {
      const arr = FALLBACK[rol] || FALLBACK.savunma;
      aiText = arr[Math.min(step, arr.length - 1)];
    }

    const updatedHistory = [...newHistory, { role: 'assistant', content: aiText }];
    setHistory(updatedHistory);
    setMessages(prev => [...prev, { type: 'ai', role: `AI ${aiRole}`, text: aiText }]);
    const newStep = step + 1;
    setStep(newStep);
    setLoading(false);

    if (newStep >= vaka.steps.length - 1) {
      await generateVerdict(updatedHistory);
    }
  };

  const generateVerdict = async (hist) => {
    setLoading(true);
    const prompt = `Sen tarafsız bir Yargıtay hakimisin. Davayı değerlendir.
DAVA: ${vaka.title} — ${vaka.ref}
DETAY: ${vaka.full}
DELİLLER: ${vaka.evidence.join('; ')}
Konuşma geçmişini değerlendirerek kararını yaz. Her iki tarafın güçlü/zayıf noktalarını belirt. Hukuki gerekçeli, resmi dil, 3-4 paragraf, Türkçe.
Konuşma: ${hist.map(m => `${m.role}: ${m.content}`).join('\n\n')}`;

    let verdict = null;
    try {
      const res = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ messages: [{ role: 'user', content: prompt }] })
      });
      const data = await res.json();
      verdict = data.text || null;
    } catch {}

    if (!verdict) {
      verdict = `Mahkememiz her iki tarafın argümanlarını dikkatle değerlendirmiştir. ${vaka.ref} kapsamında sunulan deliller ve hukuki argümanlar incelendiğinde, davada belirleyici unsurların tartışmalı olmaya devam ettiği görülmektedir. Her iki taraf da gelişim potansiyeli taşımaktadır.`;
    }

    const scores = [
      { label: 'Hukuki Analiz', val: 65 + Math.floor(Math.random() * 25) },
      { label: 'Delil Kullanımı', val: 55 + Math.floor(Math.random() * 30) },
      { label: 'Argüman Gücü', val: 60 + Math.floor(Math.random() * 25) },
      { label: 'Mevzuat Hakimiyeti', val: 50 + Math.floor(Math.random() * 35) }
    ];

    setMessages(prev => [...prev, { type: 'verdict', role: '⚖️ Hakim — AI Kararı', text: verdict, scores }]);
    setStep(vaka.steps.length);
    setDone(true);
    setLoading(false);
  };

  if (!vaka) return null;
  const aiRole = rol === 'savunma' ? 'Savcı' : 'Savunma Avukatı';

  const stepLabels = ['Açılış Beyanınızı Yapın', 'Delillerinizi Sunun', 'Karşı Argümanınızı Geliştirin', 'Kapanış Beyannamenizi Yapın'];

  return (
    <div className={styles.page}>
      <div className="top-bar">
        <div className="logo" onClick={() => router.push('/')} style={{ cursor: 'pointer' }}>Lex<span>io</span></div>
        <div className={styles.meta}>{vaka.title}</div>
      </div>

      <div className={styles.layout}>
        {/* Sidebar */}
        <div className={styles.sidebar}>
          <div className={styles.sideSection}>
            <div className={styles.sideLabel}>Aktif Dava</div>
            <div className={styles.sideCaseTitle}>{vaka.title}</div>
            <div className={styles.sideCaseText}>{vaka.full.slice(0, 110)}...</div>
            <div className={styles.roleBadge}>{rol === 'savunma' ? '⚖️ Savunma Avukatı' : '🔍 Savcı'}</div>
          </div>

          <div className={styles.sideSection}>
            <div className={styles.sideLabel}>Duruşma Aşamaları</div>
            {vaka.steps.map((s, i) => (
              <div key={i} className={`${styles.step} ${i === step ? styles.active : i < step ? styles.done : ''}`}>
                <div className={styles.stepDot}>{i < step ? '✓' : i + 1}</div>
                <span>{s}</span>
              </div>
            ))}
          </div>

          <button className={styles.newCaseBtn} onClick={() => router.push('/vakalar')}>
            ↩ Yeni Dava Seç
          </button>
        </div>

        {/* Main */}
        <div className={styles.main}>
          <div className={styles.chatArea} ref={chatRef}>
            {messages.map((m, i) => (
              <div key={i} className={`${styles.msg} ${styles[m.type]}`}>
                <div className={styles.msgRole}>{m.role}</div>
                <div className={styles.msgBubble}>
                  {m.type === 'verdict' && (
                    <>
                      <div className={styles.verdictTitle}>KARAR VE GEREKÇESİ</div>
                      <div className={styles.verdictText} dangerouslySetInnerHTML={{ __html: m.text.replace(/\n/g, '<br/>') }} />
                      <div className={styles.scoreBar}>
                        {m.scores.map((s, si) => (
                          <div key={si} className={styles.scoreItem}>
                            <div className={styles.scoreLabel}>{s.label}</div>
                            <div className={styles.scoreTrack}>
                              <div className={styles.scoreFill} style={{ width: `${s.val}%` }} />
                            </div>
                            <div className={styles.scoreNum}>{s.val}/100</div>
                          </div>
                        ))}
                      </div>

                      <div style={{ marginTop: '1.5rem', textAlign: 'center' }}>
                        <button
                          className="btn-primary"
                          style={{ background: 'linear-gradient(135deg, #833AB4, #FD1D1D, #F56040)', padding: '0.6rem 1.2rem', fontSize: '0.9rem' }}
                          onClick={() => setShowStoryModal(true)}
                        >
                          📸 Başarını Instagram Hikayede Paylaş
                        </button>
                      </div>
                    </>
                  )}
                  {m.type !== 'verdict' && (
                    <span dangerouslySetInnerHTML={{ __html: m.text }} />
                  )}
                </div>
              </div>
            ))}

            {loading && (
              <div className={styles.loadingMsg}>
                <span>Karşı taraf yanıt hazırlıyor</span>
                <span className={styles.dots}><span>.</span><span>.</span><span>.</span></span>
              </div>
            )}
          </div>

          <div className={styles.inputArea}>
            {!done && (
              <div className={styles.inputLabel}>
                {stepLabels[Math.min(step, stepLabels.length - 1)] || 'Argümanınızı Yazın'}
              </div>
            )}
            <div className={styles.inputRow}>
              <textarea
                className={styles.textarea}
                value={input}
                onChange={e => setInput(e.target.value)}
                onKeyDown={e => { if (e.key === 'Enter' && e.ctrlKey) sendArgument(); }}
                placeholder={done ? 'Simülasyon tamamlandı.' : 'Hukuki argümanınızı buraya yazın... (Ctrl+Enter)'}
                disabled={loading || done}
              />
              <button className={styles.sendBtn} onClick={sendArgument} disabled={loading || done || !input.trim()}>
                Gönder ↗
              </button>
            </div>
            {!done && (
              <div className={styles.hint}>TCK, CMUK veya Yargıtay kararlarına atıfta bulunabilirsiniz.</div>
            )}
            {done && (
              <div className={styles.hint}>
                Simülasyon tamamlandı. &nbsp;
                <button className={styles.inlineBtn} onClick={() => router.push('/vakalar')}>Yeni dava seç →</button>
              </div>
            )}
          </div>
        </div>
      </div>

      {showStoryModal && (
        <InstagramStoryCard
          caseTitle={vaka.title}
          score={88}
          streak={7}
          rank="Kıdemli Avukat"
          onClose={() => setShowStoryModal(false)}
        />
      )}
    </div>
  );
}

export async function getStaticPaths() {
  return {
    paths: CASES.map(c => ({ params: { id: String(c.id) } })),
    fallback: false
  };
}

export async function getStaticProps({ params }) {
  const vaka = CASES.find(c => c.id === Number(params.id)) || null;
  return { props: { vaka } };
}
