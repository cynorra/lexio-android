export const BLOG_POSTS = [
  {
    id: '1',
    title: 'HMGS 2026 Sınav Müfredatı ve Yargıtay İçtihat Ağırlıkları Açıklandı',
    slug: 'hmgs-2026-mufredat-yargitay-ictihatlari',
    category: 'Sınav & Mevzuat',
    date: '12 Ağustos 2026',
    author: 'Lexio Hukuk Editörlüğü',
    summary: 'Hukuk Mesleklerine Giriş Sınavı (HMGS) için Adalet Bakanlığı ve ÖSYM yeni soru dağılımını yayımladı. Ceza Hukuku ve Medeni Usul sorularında Yargıtay emsal kararları ön plana çıkıyor.',
    readTime: '4 dk okuma',
    imageUrl: '/images/blog_hmgs.jpg',
    content: `
      <h2>HMGS 2026 Yılı Değişiklikleri Neler Getiriyor?</h2>
      <p>ÖSYM ve Adalet Bakanlığı tarafından yayımlanan kılavuz uyarınca, HMGS sınavlarında teori bilginin yanı sıra pratik olay çözümü ve Yargıtay emsal kararlarının analizi kritik önem kazandı.</p>
      
      <h3>Öne Çıkan Hukuk Dalları Dağılımı</h3>
      <ul>
        <li><strong>Ceza Hukuku ve Ceza Muhakemesi:</strong> Olası kast - bilinçli taksir ayrımı ve meşru müdafaa sınırının aşılması.</li>
        <li><strong>Medeni Hukuk ve Tapu İptal:</strong> Muris muvazaası ve 01.04.1974 tarihli Yargıtay İçtihadı Birleştirme Kararı uygulamaları.</li>
        <li><strong>Borçlar Hukuku:</strong> Haksız fiil sorumluluğunda kusur aranmayan haller ve TBK m.49 uygulaması.</li>
      </ul>

      <blockquote style="border-left: 4px solid #D4AF37; padding-left: 1rem; color: #D4AF37; margin: 1.5rem 0;">
        "Hukukçunun başarısı, mevzuatı ezberlemekle değil; somut olaya doğru emsal içtihatı uygulayabilmekle ölçülür."
      </blockquote>
    `
  },
  {
    id: '2',
    title: 'Yargıtay Ceza Genel Kurulu’ndan Kasten Öldürme ve Yaralama Ayrımında Yeni Karar',
    slug: 'yargitay-cgk-kasten-oldurme-yaralama-karari',
    category: 'Yargıtay İçtihatları',
    date: '10 Ağustos 2026',
    author: 'Av. Dr. Caner Erdem',
    summary: 'Yargıtay CGK, kasten öldürmeye teşebbüs ile yaralama eylemlerinin sınırını belirlerken failin kullandığı aletin niteliği ve darbe bölgesinin hayati organlara yakınlığını yeniden yorumladı.',
    readTime: '6 dk okuma',
    imageUrl: '/images/blog_yargitay.jpg',
    content: `
      <h2>Suç Kastının Tespitinde 5 Temel Kriter</h2>
      <p>Yargıtay Ceza Genel Kurulu, failin eylem anındaki iç dünyasını ve kastını belirlemek için şu 5 temel ilkeyi vurgulamıştır:</p>
      <ol>
        <li>Suçta kullanılan vasıtanın öldürmeye elverişliliği,</li>
        <li>Hedef alınan vücut bölgesinin hayati önem taşıması,</li>
        <li>Darbe sayısı ve darbenin şiddeti,</li>
        <li>Fail ile mağdur arasındaki husumetin derecesi,</li>
        <li>Eyleme kendiliğinden mi yoksa engel bir nedenden dolayı mı son verildiği.</li>
      </ol>
    `
  },
  {
    id: '3',
    title: 'Yapay Zeka ve Hukuk Teknolojileri: Mahkemelerde Duruşma Simülasyonları Dönemi',
    slug: 'yapay-zeka-ve-hukuk-teknolojileri',
    category: 'Hukuk & Teknoloji',
    date: '08 Ağustos 2026',
    author: 'Lexio Teknoloji Ekibi',
    summary: 'Gelişen yapay zeka modelleri, avukatların duruşma öncesinde strateji geliştirmesini ve karşı taraf argümanlarını öngörmesini kolaylaştırıyor.',
    readTime: '5 dk okuma',
    imageUrl: '/images/blog_ai_law.jpg',
    content: `
      <h2>AI Duruşma Simülatörleri Nasıl Çalışıyor?</h2>
      <p>Yapay zeka modelleri, Yargıtay emsal kararları ve kanun maddeleri üzerinde eğitilerek duruşmada karşı taraf veya hakim rolü üstlenebilmektedir.</p>
      <p>Bu sayede hukuk öğrencileri ve stajyer avukatlar gerçek bir duruşmaya çıkmadan önce savunma pratiği yapabilmektedir.</p>
    `
  }
];

export function getBlogPosts() {
  return BLOG_POSTS;
}

export function getBlogPostById(id) {
  return BLOG_POSTS.find(p => p.id === id || p.slug === id) || null;
}
