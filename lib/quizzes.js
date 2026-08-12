export const QUIZZES = [
  {
    id: 'quiz-ceza-1',
    courseId: 'ceza-hukuku',
    title: 'Ceza Hukuku Genel Hükümler & Kast-Taksir Testi',
    category: 'Ceza Hukuku',
    examType: 'HMGS & Hakimlik Uyumlu',
    questions: [
      {
        id: 'q1',
        question: 'Sanık A, hasmı B’nin içinde bulunduğu kalabalık kahvehaneye doğru tüfekle 3 el ateş etmiş ve B ile birlikte olayla ilgisi olmayan C’yi de yaralamıştır. Yargıtay uygulamalarına göre sanığın C’ye karşı eyleminin hukuki nitelendirmesi nedir?',
        options: [
          'A) B’ye karşı kasten yaralama, C’ye karşı basit taksirle yaralama',
          'B) B’ye karşı kasten öldürmeye teşebbüs, C’ye karşı olası kastla yaralama',
          'C) Her iki kişiye karşı da bilinçli taksirle yaralama',
          'D) B’ye karşı kasten öldürmeye teşebbüs, C’ye karşı kastın aşılması suretiyle yaralama',
          'E) C’ye karşı eylem kaza/tesadüf sayılacağından ceza verilemez'
        ],
        correctIndex: 1,
        explanation: 'Yargıtay Ceza Genel Kurulu’nun yerleşik içtihatlarına göre; kalabalık ortama hedef gözeterek ateş eden kişi, hedef dışındaki kişilerin de yaralanabileceğini veya ölebileceğini öngörmesine rağmen "olursa olsun" düşüncesiyle hareket ettiğinden, hedef dışındaki mağdur C yönünden eylem "olası kastla yaralama" suçunu oluşturur.',
        sourceCode: 'TCK m.21/2 & Yargıtay CGK 2020/1-89 K.',
        sourceDetail: '5237 Sayılı TCK m.21/2 uyarınca failin suçun kanuni tanımındaki unsurların gerçekleşebileceğini öngörmesine rağmen fiili işlemesi halinde olası kast mevcuttur.'
      },
      {
        id: 'q2',
        question: 'Aşağıdakilerden hangisi Yargıtay 1. Ceza Dairesi’nin kasten öldürmeye teşebbüs ile kasten yaralama suçunu birbirinden ayırırken kullandığı temel kriterlerden biri DEĞİLDİR?',
        options: [
          'A) Suçta kullanılan aletin öldürmeye elverişli olup olmaması',
          'B) Darbelerin şiddeti ve vurulan vücut bölgesinin hayati önem taşıması',
          'C) Sanığın mağdurun tedavi masraflarını ödeyip ödememesi',
          'D) Fail ile mağdur arasında olay öncesine dayanan öldürmeyi gerektirir husumetin varlığı',
          'E) Failin eylemine kendiliğinden mi yoksa engel bir sebeple mi son verdiği'
        ],
        correctIndex: 2,
        explanation: 'Mağdurun tedavi masraflarının ödenmesi eylem sonrasındaki şahsi hallere veya etkin pişmanlığa müteallik olup, eylem anındaki suç kastının (öldürme mi yaralama mı) belirlenmesinde Yargıtay tarafından dikkate alınan bir kriter değildir.',
        sourceCode: 'TCK m.81, 86 & Yargıtay 1. CD Emsal Kararları',
        sourceDetail: 'Yargıtay 1. CD emsal kararlarında kast tespiti: Aletin elverişliliği, hedef bölge, darbe sayısı, husumet ve engellenme durumudur.'
      },
      {
        id: 'q3',
        question: 'TCK m.25 kapsamında meşru müdafaa (savunma) kurumunun uygulanabilmesi için aşağıdakilerden hangisi şart DEĞİLDİR?',
        options: [
          'A) Saldırının haksız bir eylemden kaynaklanması',
          'B) Saldırının gerçekleşmesi veya tekrarının muhakkak olması',
          'C) Savunma yapan kişinin saldırıyı önceden polise bildirmemiş olması',
          'D) Savunma ile saldırı arasında orantı bulunması',
          'E) Savunmanın o anda zorunlu olması'
        ],
        correctIndex: 2,
        explanation: 'Meşru müdafaada saldırının aniliğinde kolluk kuvvetlerine önceden bildirim yapma şartı aranmaz. O anki meşru hakkın korunması için oran ve zorunluluk yeterlidir.',
        sourceCode: 'TCK m.25/1',
        sourceDetail: 'TCK m.25/1: Gerek kendisine gerek başkasına ait hakka yönelmiş haksız saldırıyı orantılı biçimde defetmek kaydıyla faile ceza verilmez.'
      }
    ]
  },
  {
    id: 'quiz-medeni-1',
    courseId: 'medeni-hukuk',
    title: 'Medeni Hukuk & Tapu İptal / Muvazaa Testi',
    category: 'Medeni Hukuk',
    examType: 'HMGS ÖSYM Tarzı',
    questions: [
      {
        id: 'q-med-1',
        question: 'Mirasbırakan (M), kız çocuklarından mal kaçırmak amacıyla tek taşınmazını tapuda satış olarak göstererek oğlu (O)’ya devretmiştir. M’nin ölümünden sonra kız çocukları tapu iptal ve tescil davası açmıştır. Davada uygulanacak hukuki rejim ve dayanak nedir?',
        options: [
          'A) İİK m.277 İptal davası - Sadece alacaklılar açabilir',
          'B) 01.04.1974 t. Yargıtay İBK kararı ve TBK m.19 / TMK m.1025 uyarınca Muris Muvazaası davası',
          'C) TMK m.500 İflas davası',
          'D) TBK m.49 Haksız fiil tazminatı davası',
          'E) Taşınmaz devri geçerli sayılır, kız çocukları hiçbir hak talep edemez'
        ],
        correctIndex: 1,
        explanation: '01.04.1974 tarihli Yargıtay İçtihadı Birleştirme Kararı gereğince; muris tarafından mal kaçırma amacıyla yapılan nisbi muvazaalı (görünürde satış, gizlide bağış) devirlerde, saklı pay sahibi olsun olmasın tüm mirasçılar TBK m.19 ve TMK m.1025 uyarınca tapu iptali talep edebilirler.',
        sourceCode: 'Yargıtay İBK 01.04.1974 T., 1/2 K. & TMK m.1025',
        sourceDetail: 'Yargıtay İBK kararına göre devrin muvazaalı olduğu kanıtlandığında tapudaki tescil yolsuz hale gelir ve terkin edilir.'
      },
      {
        id: 'q-med-2',
        question: 'Yolsuz olarak tescil edilmiş bir taşınmazı tapu kütüğüne güvenerek iyiniyetle iktisap eden 3. kişinin bu kazanımı hangi kanun maddesi uyarınca korunur?',
        options: [
          'A) TMK m.1023',
          'B) TMK m.1025',
          'C) TBK m.19',
          'D) HMK m.190',
          'E) TCK m.157'
        ],
        correctIndex: 0,
        explanation: 'TMK m.1023 uyarınca: "Tapu kütüğündeki tescile iyiniyetle dayanarak ayni hak kazanan üçüncü kişinin bu kazanımı korunur." Bu tapu siciline güven ilkesinin doğrudan sonucudur.',
        sourceCode: 'TMK m.1023',
        sourceDetail: '4721 Sayılı Türk Medeni Kanunu m.1023 - Tapu kütüğüne güven ilkesi.'
      }
    ]
  },
  {
    id: 'quiz-borclar-1',
    courseId: 'borclar-hukuku',
    title: 'Borçlar Hukuku Haksız Fiil & Sorumluluk Testi',
    category: 'Borçlar Hukuku',
    examType: 'HMGS & Hakimlik',
    questions: [
      {
        id: 'q-borc-1',
        question: 'TBK m.49 uyarınca haksız fiilden doğan borç ilişkilerinde zararın tazmin edilebilmesi için aşağıdakilerden hangisinin varlığı ZORUNLU DEĞİLDİR?',
        options: [
          'A) Fiilin hukuka aykırı olması',
          'B) Zararın gerçekleşmiş olması',
          'C) Uygun illiyet bağının bulunması',
          'D) Zarar verenin zenginleşmiş olması',
          'E) Failin kusurlu olması (veya kanuni kusursuz sorumluluk halleri)'
        ],
        correctIndex: 3,
        explanation: 'Haksız fiil sorumluluğunda failin zenginleşmesi şart değildir. Önemli olan mağdurun zarara uğraması, eylemin hukuka aykırılığı, kusur ve illiyet bağıdır. Zenginleşme şartı sebepsiz zenginleşme kurumunda aranır.',
        sourceCode: 'TBK m.49 & m.77',
        sourceDetail: '6098 Sayılı Türk Borçlar Kanunu m.49 (Haksız fiil) vs m.77 (Sebepsiz Zenginleşme).'
      }
    ]
  }
];
