export const CASES = [
  {
    id: 1,
    tag: 'ceza',
    tagLabel: 'Ceza Hukuku',
    title: 'Kasten Adam Öldürmeye Teşebbüs ve Yaralama Ayrımı',
    preview: 'Sanık, tartışma sonrasında katılanı bıçakla yaralamıştır. Kasten adam öldürmeye teşebbüs mü, kasten yaralama mı?',
    difficulty: 'Orta',
    duration: '~15 dk',
    ref: 'TCK m.81, 86; Yargıtay CGK 2021/1-142 K.',
    full: 'Sanık A.K., komşusu B.T. ile çıkan arazi anlaşmazlığı nedeniyle tartışmış; tartışmanın büyümesi üzerine mutfak bıçağıyla B.T.\'nin karın bölgesini tek kez yaralamıştır. B.T. hastanede ameliyata alınmış ve hayatı kurtarılmıştır. Sanık yaralama kastıyla hareket ettiğini savunmakta, savcılık ise hayati bölgenin hedef alınması nedeniyle öldürme kastını ileri sürmektedir.',
    evidence: [
      'Adli tıp raporu: Karın bölgesinde 1 adet delici-kesici alet yarası, hayati tehlike mevcut',
      'Tanık ifadesi: Sanığın "seni öldürürüm" dediği iddia ediliyor',
      'Sanık beyanı: Sinirlendiğini, korkutmak istediğini ve öldürme kastı olmadığını beyan ediyor',
      'Silah: 18 cm mutfak bıçağı, eyleme üçüncü kişilerin araya girmesiyle son verilmiş'
    ],
    steps: ['Açılış Beyanı', 'Delil Sunumu ve Kast Analizi', 'Yargıtay Kriterleri Tartışması', 'Kapanış', 'Karar']
  },
  {
    id: 2,
    tag: 'ceza',
    tagLabel: 'Ceza Hukuku',
    title: 'Meşru Müdafaa Sınırlarının Aşılması',
    preview: 'Sanık, kendisine yönelen silahlı saldırıyı bertaraf etmek için ateş etmiştir. Meşru müdafaa mı, orantısız güç mü?',
    difficulty: 'Zor',
    duration: '~20 dk',
    ref: 'TCK m.25, 27; Yargıtay 1. CD 2020/1182 K.',
    full: 'Sanık M.D., alacaklı olduğu şahsın üzerine ruhsatsız silah doğrultması üzerine ruhsatlı tabancasıyla 3 el ateş açmış ve saldırganı ağır yaralamıştır. Meşru müdafaa savunması yapılmış, savcılık ise ilk atıştan sonra saldırganın silahı düşürmesine rağmen ateş edilmeye devam edildiğini iddia etmiştir.',
    evidence: [
      'Güvenlik kamerası: Saldırganın ilk olarak silah çektiği net görülmektedir',
      'Kriminal rapor: Sanık 3 el ateş etmiş, 2 isabet tespit edilmiştir',
      'Tanık ifadesi: Sanığın ilk atış sonrası duraksadığı ancak telaşla tekrar ateş ettiği beyan edilmiştir',
      'Sanık beyanı: Can havliyle ve heyecanla hareket ettiğini söylemektedir'
    ],
    steps: ['Açılış Beyanı', 'Meşru Müdafaa Analizi', 'Orantılılık ve Heyecan Sınırı', 'Kapanış', 'Karar']
  },
  {
    id: 3,
    tag: 'borclar',
    tagLabel: 'Borçlar Hukuku',
    title: 'Nisbi Muvazaa ve İnançlı İşlem İddiası',
    preview: 'Borçlu, alacaklılarından mal kaçırmak için evini arkadaşına satış gibi göstererek devretmiştir.',
    difficulty: 'Zor',
    duration: '~18 dk',
    ref: 'TBK m.19; Yargıtay HGK 2023/1222 K.',
    full: 'Davacı banka, davalı borçlunun icra takibi başlamadan 3 gün önce tek taşınmazını yakın arkadaşına tapuda satış göstererek devrettiğini tespit etmiştir. Davacı işlemin muvazaalı (danışıklı) olduğunu öne sürerek tapunun iptalini talep etmektedir. Davalı ise gerçek bir satış yapıldığını ve bedelin nakit ödendiğini savunmaktadır.',
    evidence: [
      'Tapu devir belgesi: Satış bedeli emlak rayiç değerinin %30 altında gösterilmiş',
      'Banka kayıtları: Davalılar arasında hiçbir para transferi izine rastlanmamıştır',
      'Tanık beyanı: Davalı borçlunun evde oturmaya devam ettiği doğrulanmıştır',
      'İcra takip dosyası: Devirden hemen sonra borçlu aleyhine takip başlatılmıştır'
    ],
    steps: ['Dava Dilekçesi ve İddialar', 'Yolsuz Tescil Analizi', 'İspat Yükü ve Hayatın Olağan Akışı', 'Kapanış', 'Karar']
  },
  {
    id: 4,
    tag: 'ceza',
    tagLabel: 'Ceza Hukuku',
    title: 'Bilişim Sistemleri İle Nitelikli Dolandırıcılık',
    preview: 'Sahte ödeme portalı ile banka bilgileri ele geçirilerek hesaplar boşaltıldı.',
    difficulty: 'Kolay',
    duration: '~12 dk',
    ref: 'TCK m.158/1-f, 244; Yargıtay 11. CD 2022/4011 K.',
    full: 'Sanık Y.A., bilinen bir kamu bankasının internet şubesini taklit eden oltama (phishing) web sitesi kurmuş, 12 vatandaşın şifrelerini ele geçirerek hesaplarındaki 450.000 TL parayı kendi kripto varlık hesaplarına aktarmıştır.',
    evidence: [
      'IP log kayıtları ve BTK trafik verileri',
      'Kripto borsa cüzdan hareketleri sanık kimliğiyle eşleşmiştir',
      'Mağdur banka dekontları ve adli bilişim inceleme raporu',
      'Sanık savunması: Hesabını kiraladığını, oltama sitesini yönetmediğini iddia ediyor'
    ],
    steps: ['Suç Nitelendirmesi', 'Bilişim Delilleri Analizi', 'Kasıt ve İştirak Tartışması', 'Kapanış', 'Karar']
  },
  {
    id: 5,
    tag: 'usul',
    tagLabel: 'Usul Hukuku',
    title: 'Hukuka Aykırı Delille Elde Edilen İtiraf',
    preview: 'Kolluk tarafından hakim kararı olmaksızın yapılan aramadaki uyuşturucu delil sayılır mı?',
    difficulty: 'Zor',
    duration: '~20 dk',
    ref: 'CMK m.206/2-a, Anayasa m.38/6; Yargıtay CGK 2019/10-312 K.',
    full: 'Polis ekipleri, ihbar üzerine şüphelinin aracını durdurmuş, hakim veya mülki amir arama kararı olmaksızın bagajda arama yaparak maddeleri bulmuştur. Şüpheli ilk ifadesinde maddelerin kendisine ait olduğunu söylemiş ancak mahkemede aramanın hukuka aykırı olduğunu belirtmiştir.',
    evidence: [
      'Arama ve el koyma tutanağı: Arama kararı sütunu boş bırakılmış',
      'Sanık müdafii itirazı: Anayasa m.38/6 uyarınca hukuka aykırı deliller hükme esas alınamaz',
      'Kolluk fezlekesi: Gecikmesinde sakınca bulunan hal gerekçesi sunulmuş',
      'Yargıtay CGK İçtihadı: Hukuka aykırı arama sonucu elde edilen delil zehirli ağacın meyvesidir'
    ],
    steps: ['Hukuka Aykırılık İddiası', 'Delil Yasakları Değerlendirmesi', 'Kamu Yararı vs Hak İhlali', 'Kapanış', 'Karar']
  },
  {
    id: 6,
    tag: 'medeni',
    tagLabel: 'Medeni Hukuk',
    title: 'Muris Muvazaası ve Saklı Pay İhlali',
    preview: 'Mirasbırakan babanın tüm taşınmazlarını tek erkek çocuğuna satış göstererek devretmesi.',
    difficulty: 'Orta',
    duration: '~15 dk',
    ref: 'Yargıtay İBK 01.04.1974 T. 1/2 K., TMK m.1025',
    full: 'Mirasbırakan M.K., ölümünden 2 yıl önce 4 dairesini kız çocuklarından mal kaçırma amacıyla tek erkek evladına devretmiştir. Kız evlatlar muvazaa nedeniyle tapu iptal ve tescil davası açmıştır. Davalı ise babasına son yıllarında baktığını ve minnet karşılığı devredildiğini savunmaktadır.',
    evidence: [
      '01.04.1974 tarihli Yargıtay İçtihadı Birleştirme Kararı',
      'Tapu işlem dosyası: Devir akit tablosunda satış yazılmaktadır',
      'Banka hesap hareketleri: Satış bedelinin ödendiğine dair belge bulunmamaktadır',
      'Tanık anlatımları: Murisin "kızlara mal vermem" dediği beyan edilmiştir'
    ],
    steps: ['Dava Konusu ve İddialar', 'Muris Muvazaası Kriterleri', 'Bakım Edimi vs Mal Kaçırma Kastı', 'Kapanış', 'Karar']
  },
  {
    id: 7,
    tag: 'ticaret',
    tagLabel: 'Ticaret Hukuku',
    title: 'Bonoda Teminat Kaydı ve Kambiyo Vasfı',
    preview: 'Sözleşmeye teminat olarak verilen senet icraya konulabilir mi?',
    difficulty: 'Orta',
    duration: '~15 dk',
    ref: 'TTK m.776, İİK m.170/a; Yargıtay 12. HD 2021/8901 K.',
    full: 'Kiralayan, kira sözleşmesiyle birlikte kiracıdan aldığı 100.000 TL\'lik senedi takibe koymuştur. Senet üzerine "Teminat senedidir" ibaresi yazılmıştır. Borçlu kiracı takibin iptalini talep etmektedir.',
    evidence: [
      'Senet aslı: Arka yüzünde "Kira Sözleşmesi Teminatıdır" yazısı mevcut',
      'Kira sözleşmesi fotokopisi',
      'TTK m.776 gereğince bono kayıtsız şartsız ödeme vaadi içermelidir',
      'Yargıtay 12. HD Kararı: Teminat kaydı senedin kambiyo vasfını ortadan kaldırır'
    ],
    steps: ['Talebin Sunumu', 'Kambiyo Hukuku Analizi', 'Teminat İddiasının İspatı', 'Kapanış', 'Karar']
  },
  {
    id: 8,
    tag: 'ceza',
    tagLabel: 'Ceza Hukuku',
    title: 'Taksirle Ölüme Neden Olma ve İş Güvenliği Kusuru',
    preview: 'İnşaattan düşen işçi vakasında işveren ve şantiye şefinin ceza sorumluluğu.',
    difficulty: 'Orta',
    duration: '~15 dk',
    ref: 'TCK m.85/1, İş K. m.77; Yargıtay 12. CD 2020/5412 K.',
    full: 'İnşaatın 4. katında yaşam halatı ve emniyet kemeri bulunmaksızın çalıştırılan işçi düşerek vefat etmiştir. Müteahhit ve şantiye şefi hakkında taksirle ölüme neden olma suçundan kamu davası açılmıştır.',
    evidence: [
      'İş güvenliği uzmanı bilirkişi raporu: %80 işveren kusuru',
      'Kamera kaydı: Emniyet kemeri takma talimatı verilmediği görülüyor',
      'Sanık müteahhit beyanı: İş güvenliği ekipmanlarını aldıklarını, işçinin takmadığını savunuyor',
      'Şantiye denetim defteri: Eksikliklerin uyarılmasına rağmen giderilmediği tespit edilmiştir'
    ],
    steps: ['İddianame Özeti', 'Kusur Dereceleri Tartışması', 'Kaçınılmazlık ve Taksir Ayrımı', 'Kapanış', 'Karar']
  }
];
