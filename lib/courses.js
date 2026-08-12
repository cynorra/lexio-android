export const COURSES = [
  {
    id: 'ceza-hukuku',
    slug: 'ceza-hukuku',
    title: 'Ceza Hukuku Genel & Özel Hükümler',
    tag: 'ceza',
    tagLabel: 'Ceza Hukuku',
    icon: '⚖️',
    summary: 'Suç teorisi, kast-taksir ayrımı, teşebbüs, iştirak, meşru müdafaa ve kasten öldürme/yaralama suçlarının Yargıtay kriterleriyle analizi.',
    lessonCount: 8,
    duration: '4 Saat',
    difficulty: 'Orta',
    hmgsWeight: '%12,5 (15 Soru)',
    modules: [
      {
        id: 'mod-1',
        title: 'Suçun Maddi ve Manevi Unsuru (Kast ve Taksir)',
        description: 'Doğrudan kast, olası kast, bilinçli taksir ve basit taksir kavramlarının hukuki ayrımı.',
        content: `Ceza Hukukunda kast, failin suçun kanuni tanımındaki unsurlarını bilerek ve isteyerek gerçekleştirmesidir (TCK m.21/1). 

Olası kastta ise fail, suçun unsurlarının gerçekleşebileceğini öngörmesine rağmen "olursa olsun" diyerek kabullenir.

Bilinçli taksirde fail neticeyi öngörür ancak şahsi becerisine veya şansına güvenerek neticenin gerçekleşmeyeceğine inanır.`,
        sources: ['TCK m.21', 'TCK m.22', 'Yargıtay CGK 2021/1-142 K.']
      },
      {
        id: 'mod-2',
        title: 'Suça Teşebbüs ve Gönüllü Vazgeçme',
        description: 'İcraya başlama kriteri, elverişli vasıta ve teşebbüs halinde ceza indirimi.',
        content: `Suça teşebbüs, failin elverişli hareketlerle doğrudan doğruya icraya başlayıp elinde olmayan nedenlerle neticeyi gerçekleştirememesidir (TCK m.35).

İcraya başlama ile hazırlık hareketleri arasındaki sınır Yargıtay tarafından "objektif tehlike" esasına göre çizilir.`,
        sources: ['TCK m.35', 'TCK m.36', 'Yargıtay 1. CD 2020/1182 K.']
      },
      {
        id: 'mod-3',
        title: 'Hukuka Aykırılığı Kaldıran Haller ve Meşru Müdafaa',
        description: 'TCK m.25 kapsamında haksız saldırı, orantılılık ilkesi ve sınırın aşılması.',
        content: `Meşru müdafaada 3 temel unsur aranır:
1. Mevcut ve haksız bir saldırı olmalı.
2. Savunma zorunlu olmalı.
3. Savunma ile saldırı arasında orantı bulunmalı.

Sınırın heyecan, korku veya telaştan ötürü aşılması halinde faile ceza verilmez (TCK m.27/2).`,
        sources: ['TCK m.25', 'TCK m.27', 'Yargıtay CGK 2019/3-441 K.']
      },
      {
        id: 'mod-4',
        title: 'Kasten Öldürme ve Kasten Yaralama Suçları',
        description: 'TCK m.81 ve m.86 kapsamında kast tespiti ve Yargıtay 1. Ceza Dairesi emsal kararları.',
        content: `Kasten öldürmeye teşebbüs ile kasten yaralama suçunun ayrımında Yargıtay şu kriterleri arar:
• Suç aletinin öldürmeye elverişliliği
• Darbelerin hayati bölgelere yönelmesi
• Darbe sayısı ve şiddeti
• Olay öncesi öldürmeyi gerektirecek husumet
• Failin kendi arzusuyla mı yoksa engelle mi durduğu`,
        sources: ['TCK m.81', 'TCK m.86', 'Yargıtay 1. CD Emsal Kriterleri']
      }
    ],
    linkedQuizzes: ['quiz-ceza-1', 'quiz-ceza-2'],
    linkedCases: [1, 2, 4]
  },
  {
    id: 'medeni-hukuk',
    slug: 'medeni-hukuk',
    title: 'Medeni Hukuk & Tapu İptal / Tescil',
    tag: 'medeni',
    tagLabel: 'Medeni Hukuk',
    icon: '🏠',
    summary: 'Kişilik hakları, tapu sicilinin güvenliği, TMK 1025 yolsuz tescil davası, muris muvazaası ve aile hukuku ilkeleri.',
    lessonCount: 6,
    duration: '3.5 Saat',
    difficulty: 'Zor',
    hmgsWeight: '%12,5 (15 Soru)',
    modules: [
      {
        id: 'mod-med-1',
        title: 'Tapu Siciline Güven ve Yolsuz Tescil (TMK m.1025)',
        description: 'Hukuki nedenden yoksun tescillerin iptali ve dürüstlük kuralı.',
        content: `Bir ayni hak yolsuz olarak tescil edilmişse, hak sahibi TMK m.1025 uyarınca tapu iptal ve tescil davası açabilir.

İyiniyetli 3. kişilerin tapu kütüğündeki tescile dayanarak kazandıkları ayni haklar korunur (TMK m.1024). Ancak kötüniyetli 3. kişilere karşı her zaman yolsuz tescil davası açılabilir.`,
        sources: ['TMK m.1024', 'TMK m.1025', 'Yargıtay HGK 2021/65 K.']
      },
      {
        id: 'mod-med-2',
        title: 'Muris Muvazaası ve 1974 İçtihadı Birleştirme Kararı',
        description: 'Mirasbırakanın mal kaçırma amacıyla yaptığı gizli bağışlamaların hukuki rejimı.',
        content: `Mirasbırakan, mirasçısını miras hakkından yoksun bırakmak için gerçekte bağışladığı taşınmazı tapuda satış gibi gösterirse "muris muvazaası" oluşur.

01.04.1974 tarihli Yargıtay İBK kararına göre, saklı pay sahibi olsun veya olmasın her mirasçı bu devrin iptalini isteyebilir.`,
        sources: ['Yargıtay İBK 01.04.1974 T. 1/2 K.', 'TBK m.19', 'TMK m.1025']
      }
    ],
    linkedQuizzes: ['quiz-medeni-1'],
    linkedCases: [7, 8]
  },
  {
    id: 'borclar-hukuku',
    slug: 'borclar-hukuku',
    title: 'Borçlar Hukuku Genel & Özel Esaslar',
    tag: 'borclar',
    tagLabel: 'Borçlar Hukuku',
    icon: '📜',
    summary: 'Sözleşmenin kurulması, irade bozuklukları, TBK m.19 nisbi muvazaa, haksız fiil sorumluluğu ve sebepsiz zenginleşme.',
    lessonCount: 7,
    duration: '4 Saat',
    difficulty: 'Orta',
    hmgsWeight: '%10 (12 Soru)',
    modules: [
      {
        id: 'mod-borc-1',
        title: 'İrade Bozuklukları ve Muvazaa (TBK m.19)',
        description: 'Yanılma, aldatma, korkutma ve tarafların danışıklı (muvazaalı) işlemleri.',
        content: `Muvazaada taraflar görünürde bir sözleşme yaparlar ancak kendi aralarında bu sözleşmenin hüküm doğurmaması konusunda anlaşırlar.

Mutlak muvazaada görünürdeki işlem tamamen geçersizdir. Nisbi muvazaada ise gizli işlem şartları uyuyorsa geçerli sayılabilir.`,
        sources: ['TBK m.19', 'TBK m.30-39', 'Yargıtay HGK 2023/1222 K.']
      },
      {
        id: 'mod-borc-2',
        title: 'Haksız Fiil Tazminatı ve Kusur Kusursuzluk Sorumluluğu',
        description: 'Maddi-manevi tazminat şartları, illiyet bağı ve zamanaşımı usulü.',
        content: `Haksız fiil sorumluluğunun 4 temel unsuru vardır:
1. Hukuka aykırı fiil
2. Kusur (Kast veya Taksir)
3. Zarar (Maddi veya Manevi)
4. Uygun illiyet bağı

Zamanaşımı, zararın ve failin öğrenilmesinden itibaren 2 yıl, her halde 10 yıldır (TBK m.72).`,
        sources: ['TBK m.49', 'TBK m.50', 'TBK m.72']
      }
    ],
    linkedQuizzes: ['quiz-borclar-1'],
    linkedCases: [3, 9]
  },
  {
    id: 'ticaret-hukuku',
    slug: 'ticaret-hukuku',
    title: 'Ticaret Hukuku & Kıymetli Evrak',
    tag: 'ticaret',
    tagLabel: 'Ticaret Hukuku',
    icon: '💼',
    summary: 'Tacir sıfatı, ticaret şirketleri organ sorumlulukları, bono, çek ve poliçede kambiyo def\'ileri.',
    lessonCount: 5,
    duration: '3 Saat',
    difficulty: 'Zor',
    hmgsWeight: '%10 (12 Soru)',
    modules: [
      {
        id: 'mod-tic-1',
        title: 'Bono ve Çekte Şekil Şartları ile İllilik / Mücerretlik',
        description: 'Kambiyo senetlerinin zorunlu unsurları, şahsi ve mutlak def\'iler.',
        content: `Bono kayıtsız şartsız belirli bir bedeli ödeme vaadini içermelidir (TTK m.776). Teminat kaydı içeren senetler kambiyo vasfını yitirir.

Mutlak def'iler (imza inkarı, tahrifat) herkese karşı ileri sürülebilirken, şahsi def'iler (bedelsizlik) iyiniyetli hamilere karşı ileri sürülemez.`,
        sources: ['TTK m.776', 'TTK m.777', 'Yargıtay 11. HD 2021/3091 K.']
      }
    ],
    linkedQuizzes: ['quiz-ticaret-1'],
    linkedCases: [10]
  },
  {
    id: 'usul-hukuku',
    slug: 'usul-hukuku',
    title: 'Usul Hukuku (HMK & CMK Yargılaması)',
    tag: 'usul',
    tagLabel: 'Usul Hukuku',
    icon: '🏛️',
    summary: 'İspat yükü, senetle ispat zorunluluğu, hukuka aykırı deliller, ceza yargılamasında tutuklama ve arama kararları.',
    lessonCount: 6,
    duration: '3.5 Saat',
    difficulty: 'Orta',
    hmgsWeight: '%15 (18 Soru)',
    modules: [
      {
        id: 'mod-usul-1',
        title: 'HMK m.190 İspat Yükü ve Senetle İspat Kuralı',
        description: 'Hukuki işlemlerde senet sınırı ve senetle ispat kuralının istisnaları.',
        content: `HMK m.190 uyarınca ispat yükü, iddia ettiği vakıadan lehine hak çıkaran tarafa aittir.

Belli bir tutarı aşan hukuki işlemler senetle (yazılı delille) ispat edilmek zorundadır. Muvazaa iddiasında taraflar yazılı delil getirmek zorundayken, 3. kişiler her türlü delille ispat edebilir.`,
        sources: ['HMK m.190', 'HMK m.200', 'HMK m.203']
      }
    ],
    linkedQuizzes: ['quiz-usul-1'],
    linkedCases: [5, 11]
  }
];
