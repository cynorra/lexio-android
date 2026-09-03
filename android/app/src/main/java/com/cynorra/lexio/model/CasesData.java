package com.cynorra.lexio.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CasesData {
    private static final List<Case> CASES = new ArrayList<>();

    static {
        // CASE 1: Kasten Adam Öldürmeye Teşebbüs
        List<Case.TrialStep> steps1 = new ArrayList<>();
        
        // Step 1
        steps1.add(new Case.TrialStep(
            "Açılış Beyanı",
            "Duruşmayı başlatmak için açılış tezlerinizi sunun.",
            Arrays.asList(
                new Case.Choice(
                    "Müvekkilin öldürme kastı yoktur. Eylem ani bir kavga sonucu gelişmiş ve tek bir yaralamayla sınırlı kalmıştır. Eylem kasten yaralamadır.",
                    "Savcılık olarak karşı çıkıyoruz: Müştekinin karın bölgesi gibi hayati bir organına 18 cm bıçakla vurulması öldürme kastını açıkça gösterir.",
                    25
                ),
                new Case.Choice(
                    "Müvekkil tamamen masumdur. Bıçağı sadece korkutmak amacıyla sallamıştır, kastı bulunmamaktadır.",
                    "Savcılık: Korkutma amacıyla 18 cm mutfak bıçağının karın bölgesine saplanması hayatın olağan akışına terstir. Sanık doğrudan öldürmeye teşebbüs etmiştir.",
                    15
                ),
                new Case.Choice(
                    "Olay bir kazadır. Müvekkil arazi kavgası esnasında dengesini kaybederek müştekinin üzerine düşmüştür.",
                    "Savcılık: Tanık ifadeleri sanığın 'seni öldüreceğim' diyerek saldırdığını doğrulamaktadır. Kaza savunması suçtan kurtulmaya yöneliktir.",
                    5
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Sanık, önceden husumetli olduğu müştekiyi öldürmek amacıyla hayati bölgesine ölümcül bıçak darbesi vurmuştur. 'Seni öldüreceğim' beyanı kastı kanıtlar.",
                    "Savunma: Müvekkilimin sözleri kavga esnasında öfkeyle söylenmiş klasik ifadelerdir. Tek darbeden sonra eylemine devam etme imkanı varken durmuştur.",
                    25
                ),
                new Case.Choice(
                    "Sanık vahşi bir şekilde komşusuna saldırmış ve onu öldürmeye çalışmıştır. Ağırlaştırılmış ceza talep ediyoruz.",
                    "Savunma: İddia makamı hukuki nitelendirme yerine duygusal ifadeler kullanmaktadır. Eylemin yaralama kastıyla yapıldığı sabittir.",
                    15
                )
            )
        ));

        // Step 2
        steps1.add(new Case.TrialStep(
            "Delil Sunumu",
            "Dosyadaki delilleri kendi lehinize yorumlayarak sunun.",
            Arrays.asList(
                new Case.Choice(
                    "Adli tıp raporuna göre darbe sayısı tektir. Sanık isteseydi eylemine devam edebilirdi. Bu durum öldürme kastının olmadığını, yaralama kastı olduğunu gösterir.",
                    "Savcılık: Tek darbe olması öldürme kastını dışlamaz. Darbenin karın gibi hayati bir bölgeye vurulmuş olması ve müştekinin acil ameliyatla kurtulması teşebbüsü kanıtlar.",
                    25
                ),
                new Case.Choice(
                    "Olayda kullanılan silah ev tipi mutfak bıçağıdır. Sanık önceden plan yapmamış, olay yerinde tesadüfen bulunan aleti kullanmıştır.",
                    "Savcılık: Mutfak bıçağının öldürmeye elverişli bir silah olduğu Yargıtay kararlarında sabittir. Planlama olmaması öldürme kastını ortadan kaldırmaz.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Adli tıp raporu darbenin karın bölgesine geldiğini ve müştekinin hayatını tehlikeye soktuğunu açıkça belirtmektedir. Hedef alınan bölge kastı gösterir.",
                    "Savunma: Müvekkil darbeden hemen sonra müştekiye müdahale edilmesini engellememiş, olay yerinden kaçmamıştır. Bu durum gönüllü vazgeçmeye işaret eder.",
                    25
                ),
                new Case.Choice(
                    "18 cm uzunluğundaki mutfak bıçağı öldürmeye elverişli bir silahtır. Sanığın bunu bilerek kullanması doğrudan öldürme kastıdır.",
                    "Savunma: Bıçağın elverişliliği tek başına kastı belirlemez. Önemli olan failin darbeyi vururken niyetidir; müvekkilim sadece kendini savunmuştur.",
                    15
                )
            )
        ));

        // Step 3
        steps1.add(new Case.TrialStep(
            "Kapanış Beyanı",
            "Son hukuki değerlendirmelerinizi yapın ve talebinizi belirtin.",
            Arrays.asList(
                new Case.Choice(
                    "Yargıtay CGK kararları uyarınca; darbe sayısı, engel sebep olmaksızın eyleme son verilmesi yaralama suçuna işaret eder. Kasten yaralamadan ceza verilmesini talep ederiz.",
                    "Savcılık: Eylemin kasten öldürmeye teşebbüs (TCK m.81, 35) olarak cezalandırılması, adaletin tecellisi için şarttır.",
                    25
                ),
                new Case.Choice(
                    "Şüpheden sanık yararlanır ilkesi gereğince, öldürme kastı şüpheli kaldığından eylemin yaralama olarak kabulü gerekir.",
                    "Savcılık: Ortada şüphe yoktur, adli tıp raporu ve silahın elverişliliği öldürme kastını net bir şekilde ortaya koymuştur.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Sanığın 'seni öldüreceğim' tehdidi, silahın elverişliliği ve darbenin hayati bölgeye vurulması sebebiyle TCK m.81 ve 35 uyarınca cezalandırılmasını talep ederiz.",
                    "Savunma: Müvekkilin eylemi sadece kasten yaralama (TCK m.86) sınırları içinde değerlendirilmeli, haksız tahrik hükümleri uygulanmalıdır.",
                    25
                ),
                new Case.Choice(
                    "Sanığın hiçbir hafifletici neden uygulanmaksızın kasten adam öldürmeye teşebbüs suçundan en üst sınırdan cezalandırılmasını istiyoruz.",
                    "Savunma: En üst sınırdan ceza talebi hukuki dayanaktan yoksundur. Müvekkilin sabıkasız geçmişi ve haksız tahrik unsurları göz ardı edilemez.",
                    15
                )
            )
        ));

        CASES.add(new Case(
            1,
            "ceza",
            "Ceza Hukuku",
            "Kasten Adam Öldürmeye Teşebbüs",
            "Sanık, tartışma sonrasında katılanı bıçakla yaralamıştır. Kasten adam öldürmeye teşebbüs mü, kasten yaralama mı?",
            "Orta",
            "~5 dk",
            "TCK m.81, 86",
            "Sanık A.K., komşusu B.T. ile çıkan arazi anlaşmazlığı nedeniyle tartışmış; tartışmanın büyümesi üzerine mutfak bıçağıyla B.T.'nin karın bölgesini tek kez yaralamıştır. B.T. hastanede ameliyata alınmış ve hayatı kurtarılmıştır. Sanık yaralama kastıyla hareket ettiğini savunmakta, savcılık ise öldürme kastını ileri sürmektedir.",
            Arrays.asList(
                "Adli tıp raporu: tek bıçak darbesi, karın bölgesi",
                "Tanık ifadesi: sanığın \"seni öldürürüm\" dediği iddia ediliyor",
                "Sanık: sinirlendiğini, öldürmek istemediğini beyan ediyor",
                "Silah: ev tipi mutfak bıçağı, 18 cm"
            ),
            steps1,
            "MAHKEME KARARI: Sanık A.K.'nin eylemi kasten yaralama (TCK m.86) kapsamında değerlendirilmiştir. Yargıtay içtihatları doğrultusunda, failin darbeden sonra eylemine engel bir durum olmaksızın kendiliğinden son vermesi ve darbe sayısının tek olması öldürme kastının bulunmadığına işaret eder. Sanığın lehe kanun hükümleriyle cezalandırılmasına karar verilmiştir.",
            "MAHKEME KARARI: Sanık A.K.'nin eylemi kasten öldürmeye teşebbüs (TCK m.81, 35) kabul edilmiştir. Darbenin karın bölgesi gibi hayati bölgeye vurulmuş olması, kullanılan bıçağın 18 cm elverişli yapısı ve olay öncesi husumet göz önüne alınarak sanığın mahkumiyetine karar verilmiştir.",
            "TR"
        ));

        // CASE 2: Meşru Müdafaa Sınırları
        List<Case.TrialStep> steps2 = new ArrayList<>();
        steps2.add(new Case.TrialStep(
            "Açılış Beyanı",
            "Meşru müdafaa savunması veya orantısız güç iddialarınızı sunun.",
            Arrays.asList(
                new Case.Choice(
                    "Müvekkilime yönelen doğrudan ve yakın bir silahlı saldırı mevcuttur. Müvekkil hayatta kalmak için meşru müdafaa kapsamında ateş etmiştir.",
                    "Savcılık: Saldırganın silah çekmesi meşru müdafaayı başlatsa da, sanığın 3 el ateş ederek kişiyi ağır yaralaması savunma sınırının aşılmasıdır.",
                    25
                ),
                new Case.Choice(
                    "Olay anında müvekkilimin korku ve panik içinde hareket ettiği sabittir. Sınır aşılmış olsa bile ceza verilmemelidir.",
                    "Savcılık: Sanık profesyonel ruhsatlı silah sahibidir. Korku ve panik savunması orantısız güç kullanımını gizleyemez.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Sanık, saldırganı durdurabilecekken doğrudan hayati bölgelerine 3 el ateş ederek orantısız güç kullanmış ve meşru müdafaa sınırlarını aşmıştır.",
                    "Savunma: Silahlı bir saldırı altında failden matematiksel bir orantı hesabı yapması beklenemez. Eylem meşru müdafaadır.",
                    25
                ),
                new Case.Choice(
                    "Sanık duruma hakim olmasına rağmen intikam duygusuyla karşı tarafa ateş açmıştır.",
                    "Savunma: İntikam iddiası asılsızdır, müvekkil hayatta kalma güdüsüyle ani tepki vermiştir.",
                    15
                )
            )
        ));

        steps2.add(new Case.TrialStep(
            "Delil Değerlendirmesi",
            "Güvenlik kamerası ve balistik raporlarını yorumlayın.",
            Arrays.asList(
                new Case.Choice(
                    "Güvenlik kamerası kayıtlarında karşı tarafın ilk silah çeken kişi olduğu net görülmektedir. Müvekkilim can tehlikesi nedeniyle ateş etmiştir.",
                    "Savcılık: Balistik raporu sanığın 3 kez ateş ettiğini göstermektedir. Saldırganı etkisiz hale getirdikten sonra da ateş edilmeye devam edilmiştir.",
                    25
                ),
                new Case.Choice(
                    "Müvekkilin 'dur, bırak silahı' uyarısı tanıklarca duyulmuştur. Bu durum sanığın iyi niyetini gösterir.",
                    "Savcılık: Uyarı yapılmış olsa dahi, peş peşe 3 el ateş edilmesi saldırıyla savunma arasındaki dengeyi bozmuştur.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Balistik rapora göre sanık 3 el ateş etmiştir. Karşı taraf tek el dahi ateş etmemişken 3 el ateş edilmesi meşru müdafaa sınırının kasten aşıldığını gösterir.",
                    "Savunma: Müvekkil karşısındakinin ateş etmesini bekleseydi şu an hayatta olmayabilirdi. Tehlikenin varlığı savunma için yeterlidir.",
                    25
                ),
                new Case.Choice(
                    "Güvenlik kamerası sanığın soğukkanlılıkla silahını çekip ateşlediğini göstermektedir. Korku ve panik hali mevcut değildir.",
                    "Savunma: Kamera kayıtları saniyeler içinde gelişen olayı göstermektedir. Soğukkanlılık iddiası hayatın gerçekleriyle uyuşmaz.",
                    15
                )
            )
        ));

        steps2.add(new Case.TrialStep(
            "Kapanış Beyanı",
            "Mahkemeden son talebinizi isteyin.",
            Arrays.asList(
                new Case.Choice(
                    "Müvekkilin eylemi TCK m.25 kapsamında meşru müdafaadır. Beraat kararı verilmesini talep ederiz.",
                    "Savcılık: Eylemin sınırın aşılması suretiyle yaralama kapsamında cezalandırılması hukuka uygun olacaktır.",
                    25
                ),
                new Case.Choice(
                    "Mahkeme sınırın aşıldığı kanaatindeyse, TCK m.27/2 uyarınca korku ve telaş nedeniyle ceza verilmemesini talep ederiz.",
                    "Savcılık: Sınırın mazur görülebilecek bir korkuyla aşılmadığı açık olup, sanık cezalandırılmalıdır.",
                    25
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Sanığın meşru müdafaa sınırlarını bilerek aştığı sabit olduğundan TCK m.86 uyarınca cezalandırılmasını talep ederiz.",
                    "Savunma: Müvekkilimin eylemi tamamen meşru savunma sınırları içerisindedir, beraat kararı verilmelidir.",
                    25
                ),
                new Case.Choice(
                    "Sanığın meşru müdafaa iddiası reddedilerek doğrudan kasten yaralama suçundan cezalandırılması gerekir.",
                    "Savunma: Karşı tarafın silahlı saldırısı ortadayken meşru müdafaanın tamamen reddedilmesi hukuka aykırıdır.",
                    15
                )
            )
        ));

        CASES.add(new Case(
            2,
            "ceza",
            "Ceza Hukuku",
            "Meşru Müdafaa Sınırları",
            "Sanık, kendisine yönelen silahlı saldırıyı bertaraf etmek için ateş etmiştir. Meşru müdafaa mı, orantısız güç mü?",
            "Zor",
            "~5 dk",
            "TCK m.25",
            "Sanık M.D., eski iş ortağından alacaklı olduğunu belirtmektedir. Söz konusu kişinin üzerine silah doğrultması üzerine sanık ruhsatlı tabancayla karşı ateş açmış ve kişiyi yaralamıştır. Meşru müdafaa savunması ileri sürülmüştür. Savcılık, orantısız güç kullanıldığını öne sürmektedir.",
            Arrays.asList(
                "Güvenlik kamerası: saldırganın silah çektiğini gösteriyor",
                "Balistik rapor: sanığın 3 el ateş ettiği, 1 isabet",
                "Tanık: sanığın \"dur, bırak silahı\" dediğini duymuş",
                "Sanık: canının tehlikede olduğunu beyan ediyor"
            ),
            steps2,
            "MAHKEME KARARI: Sanık M.D.'nin eyleminin TCK m.25 kapsamında meşru müdafaa olduğuna hükmedilerek beraatine karar verilmiştir. Saldırı ile savunma arasındaki orantının saniyelerle ölçülen olay anında tam olarak hesaplanamayacağı, sanığın can güvenliğini koruma amacı taşıdığı kabul edilmiştir.",
            "MAHKEME KARARI: Sanık M.D.'nin meşru müdafaa sınırını aştığına karar verilmiştir. Ancak sınırın aşılması olay anındaki mazur görülebilecek heyecan, korku veya telaştan ileri geldiğinden TCK m.27/2 maddesi gereğince sanığa ceza verilmesine yer olmadığına hükmedilmiştir.",
            "TR"
        ));

        // CASE 3: Trafik Kazası - Bilinçli Taksir mi Olası Kast mı?
        List<Case.TrialStep> steps3 = new ArrayList<>();
        steps3.add(new Case.TrialStep(
            "Açılış Beyanı",
            "Kazadaki kusur durumu ve kastın türü hakkındaki argümanınızı sunun.",
            Arrays.asList(
                new Case.Choice(
                    "Müvekkilimin hız sınırını aşması basit bir kural ihlalidir, ölüm neticesini istememiştir. Olay basit taksirle ölüme neden olmadır.",
                    "Savcılık: Sanık kırmızı ışıkta geçerek ve aşırı hız yaparak neticeyi öngörmüştür. Eylem bilinçli taksir boyutunu aşarak olası kasta varmıştır.",
                    20
                ),
                new Case.Choice(
                    "Müvekkilim şoförlüğüne güvenerek hareket etmiş, kaza istemeden gerçekleşmiştir. Bilinçli taksir bile tartışılamaz.",
                    "Savcılık: Şehir içinde 120 km/s hızla ve alkollü araç kullanmak 'şoförlüğe güvenmek' ile açıklanamaz, ağır kusurdur.",
                    10
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Sanık 120 km hızla kırmızı ışıkta geçip yaya geçidindeki maktule çarpmıştır. 'Olursa olsun' mantığıyla hareket ettiğinden olası kastla cezalandırılmalıdır.",
                    "Savunma: Olası kast iddiası hukuki dayanaktan yoksundur. Müvekkil frene basmış ve neticeyi engellemeye çalışmıştır, sadece bilinçli taksir vardır.",
                    25
                ),
                new Case.Choice(
                    "Sanığın trafik kurallarını hiçe saydığı ortadadır. En ağır şekilde bilinçli taksirden cezalandırılmasını istiyoruz.",
                    "Savunma: Müvekkilimin alkolsüz olması ve olay sonrası kaçmaması iyi niyetini gösterir.",
                    15
                )
            )
        ));

        steps3.add(new Case.TrialStep(
            "Delil Sunumu",
            "Trafik bilirkişi raporu ve fren izlerini değerlendirin.",
            Arrays.asList(
                new Case.Choice(
                    "Bilirkişi raporunda olay yerinde 15 metrelik fren izi tespit edilmiştir. Bu, müvekkilimin kazayı engellemeye çalıştığının ve neticeyi istemediğinin kesin kanıtıdır.",
                    "Savcılık: Fren yapılması olası kastı tamamen ortadan kaldırmaz, zira o hıza çıkıp kırmızı ışıkta geçmek başlı başına ağır kusurdur.",
                    30
                ),
                new Case.Choice(
                    "Yaya da yola aniden fırlamıştır. Raporda yayanın da tali kusurlu olduğu belirtilmektedir.",
                    "Savcılık: Yaya geçidinde geçiş üstünlüğü yayadadır. Sanığın asli kusuru tartışmasızdır.",
                    15
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Rapordaki 15 metrelik fren izi, sanığın hızının yasal sınırın çok üzerinde olduğunu kanıtlamaktadır. Olası kast unsurları mevcuttur.",
                    "Savunma: Fren izinin varlığı, neticenin istenmediğini (kast olmadığını) gösterir. Taksir sınırları içinde kalınmalıdır.",
                    20
                ),
                new Case.Choice(
                    "Sanığın kırmızı ışık ihlali kamera kayıtlarıyla sabittir.",
                    "Savunma: Işık ihlali vardır ancak bu, taksirli bir ihlaldir.",
                    15
                )
            )
        ));

        steps3.add(new Case.TrialStep(
            "Kapanış Beyanı",
            "Son ceza talebinizi mahkemeye iletin.",
            Arrays.asList(
                new Case.Choice(
                    "Müvekkilimin eyleminin TCK m.85 uyarınca basit taksirle ölüme neden olma suçu kapsamında değerlendirilerek alt sınırdan ceza verilmesini talep ederiz.",
                    "Savcılık: Basit taksir talebi kabul edilemez. En azından bilinçli taksir veya olası kast hükümleri uygulanmalıdır.",
                    15
                ),
                new Case.Choice(
                    "Fren izleri neticenin istenmediğini göstermektedir. TCK m.22/3 uyarınca bilinçli taksir kabul edilse dahi olası kast cezası verilemez.",
                    "Savcılık: Sanığın eyleminin olası kastla insan öldürme olarak değerlendirilmesi adaletin gereğidir.",
                    25
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Sanığın yaya geçidinde kırmızı ışıkta yüksek hızla seyretmesi olası kasttır. TCK m.81 ve 21/2 uyarınca cezalandırılmalıdır.",
                    "Savunma: Yargıtay içtihatları fren yapılan durumlarda olası kast değil bilinçli taksir uygulanması gerektiğini belirtmektedir.",
                    20
                ),
                new Case.Choice(
                    "Sanığın eylemi TCK m.85 ve 22/3 uyarınca bilinçli taksirle ölüme neden olmadır. Üst sınırdan ceza talep ediyoruz.",
                    "Savunma: Bilinçli taksir uygulansa dahi failin geçmişi dikkate alınarak lehe indirimler yapılmalıdır.",
                    25
                )
            )
        ));

        CASES.add(new Case(
            3,
            "ceza",
            "Ceza Hukuku",
            "Trafik Kazası - Bilinçli Taksir",
            "Sanık, aşırı hız ve kırmızı ışık ihlali sonucu ölümlü bir kazaya karışmıştır. Kast mı, taksir mi?",
            "Orta",
            "~6 dk",
            "TCK m.85, 22",
            "Sanık C.Y., şehir içi yolda 120 km/s hızla seyrederken kırmızı ışıkta durmamış ve yaya geçidinden geçen maktule çarparak ölümüne sebep olmuştur. Olay yerinde 15 metrelik fren izi tespit edilmiştir. Savcılık olası kast ile, savunma ise taksir ile yargılama talep etmektedir.",
            Arrays.asList(
                "Kamera kaydı: Kırmızı ışık ihlali",
                "Bilirkişi raporu: Hız 120 km/s, 15m fren izi",
                "Sanık: \"Göstergelere bakıyordum, ışığı görmedim, son anda fark edip frene bastım\"",
                "Tanık: Aracın hiç yavaşlamadan kavşağa girdiğini söylüyor"
            ),
            steps3,
            "MAHKEME KARARI: Olay yerindeki 15 metrelik fren izi, sanığın neticeyi öngörmesine rağmen istemediğini (engellemeye çalıştığını) gösterdiğinden eylem TCK m.85 ve m.22/3 uyarınca BİLİNÇLİ TAKSİR kapsamında değerlendirilmiştir.",
            "MAHKEME KARARI: Sanığın şehir içinde 120 km/s hızla kırmızı ışıkta geçmesi, 'olursa olsun' düşüncesiyle hareket ettiğini gösterdiğinden eylem OLASI KAST ile adam öldürme (TCK m.81, 21/2) kapsamında cezalandırılmıştır.",
            "TR"
        ));

        // CASE 4: Hırsızlık mı Yağma mı?
        List<Case.TrialStep> steps4 = new ArrayList<>();
        steps4.add(new Case.TrialStep(
            "Açılış Beyanı",
            "Suçun nitelendirmesine ilişkin iddianızı sunun.",
            Arrays.asList(
                new Case.Choice(
                    "Müvekkil çantasını aldığı mağdura karşı herhangi bir cebir veya şiddet kullanmamıştır. Eylem sadece kapkaç suretiyle hırsızlıktır.",
                    "Savcılık: Mağdurun direnmesi üzerine sanığın onu iterek yere düşürmesi, eylemi basit hırsızlıktan çıkarıp yağma suçuna dönüştürmüştür.",
                    25
                ),
                new Case.Choice(
                    "Müvekkil mağdurun çantasını yerdeyken almıştır, zor kullanmamıştır.",
                    "Savcılık: Mağdurun ve tanıkların ifadeleri zorlama ve düşürmeyi doğrulamaktadır.",
                    10
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Sanık, mağdurun çantasını alırken direnmesi üzerine cebir uygulamış ve onu yaralamıştır. Eylem açıkça yağma (gasp) suçunu oluşturur.",
                    "Savunma: Yaşanan arbede malın alınmasına yönelik bir cebir değil, anlık bir tepkidir. Yağma kastı yoktur.",
                    25
                ),
                new Case.Choice(
                    "Sanığın eylemi sokak ortasında zorbalıktır, en ağır yağma hükümlerinden yargılanmalıdır.",
                    "Savunma: Müvekkil silah veya tehdit kullanmamıştır, eylem hırsızlık sınırları içerisindedir.",
                    15
                )
            )
        ));

        steps4.add(new Case.TrialStep(
            "Delil Sunumu",
            "Tanık ifadeleri ve tıbbi raporları değerlendirin.",
            Arrays.asList(
                new Case.Choice(
                    "Mağdurun dizindeki sıyrık, çantayı çekerken dengesini kaybetmesi sonucu oluşmuştur. Müvekkilimin doğrudan bir darbesi yoktur.",
                    "Savcılık: TCK 148 kapsamında malı alabilmek için uygulanan her türlü fiziki zorlama (itme, çekiştirme) yağmanın cebir unsurunu oluşturur.",
                    20
                ),
                new Case.Choice(
                    "Tanıklar olayın çok hızlı geliştiğini söylemiştir. Müvekkilim cebir kullanmamış, sadece kaçmaya çalışmıştır.",
                    "Savcılık: Kaçarken malı elinde tutmak için gösterilen direnç de yağma suçunu oluşturabilir.",
                    15
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Adli tıp raporu mağdurun bileğinde ve dizinde zorlamaya bağlı doku zedelenmesi olduğunu teyit etmiştir. Bu cebirin kesin kanıtıdır.",
                    "Savunma: Bu tür basit tıbbi müdahaleyle giderilebilecek yaralanmalar kapkaç suçunun (hırsızlık) olağan sonucudur, yağma için yeterli ağırlıkta değildir.",
                    20
                ),
                new Case.Choice(
                    "Olay yerindeki esnaf, sanığın mağduru bilerek ittiğini görmüştür.",
                    "Savunma: Görgü tanığı ifadeleri çelişkilidir, şüpheden sanık yararlanır.",
                    15
                )
            )
        ));

        steps4.add(new Case.TrialStep(
            "Kapanış Beyanı",
            "Mahkemeden son talebinizi isteyin.",
            Arrays.asList(
                new Case.Choice(
                    "Eylemin TCK m.142/2-b kapsamında 'elde veya üstte taşınan eşyayı çekip almak suretiyle hırsızlık' olarak nitelendirilmesini talep ederiz.",
                    "Savcılık: Cebir unsuru gerçekleştiğinden eylem TCK m.148 (Yağma) kapsamındadır.",
                    30
                ),
                new Case.Choice(
                    "Eğer mahkeme cebir unsurunu kabul edecekse bile, bunun malın değerinin azlığı sebebiyle cezada indirim yapılmasını talep ederiz.",
                    "Savcılık: Çantanın içinde cüzdan ve telefon olduğu için değer azlığı uygulanmaz.",
                    15
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Sanığın, malı teslime mecbur kılmak maksadıyla cebir kullandığı sabit olduğundan TCK m.148 uyarınca yağma suçundan cezalandırılmasını talep ederiz.",
                    "Savunma: Yağma suçu çok ağır bir cezadır. Eylem hırsızlık olarak değerlendirilmelidir.",
                    25
                ),
                new Case.Choice(
                    "Sanık nitelikli yağma suçundan cezalandırılmalıdır.",
                    "Savunma: Suçun silahla veya birden fazla kişiyle işlenmediği sabittir, nitelikli haller oluşmamıştır.",
                    15
                )
            )
        ));

        CASES.add(new Case(
            4,
            "ceza",
            "Ceza Hukuku",
            "Hırsızlık mı Yağma mı?",
            "Sanık sokakta yürüyen mağdurun çantasını çalarken mağdur direnmiş ve yere düşmüştür. Suç yağma (gasp) mıdır, hırsızlık mıdır?",
            "Kolay",
            "~4 dk",
            "TCK m.142, 148",
            "Sanık E.K., sokakta yürüyen mağdur L.M.'nin omzundaki çantayı asılarak almak istemiş. Mağdur direnmiş, yaşanan arbede sırasında mağdur yere düşmüş ve dizinden yaralanmıştır. Sanık çantayı alıp kaçmıştır. Savunma, eylemin 'kapkaç (hırsızlık)' olduğunu savunurken, savcılık 'yağma (gasp)' olduğunu iddia etmektedir.",
            Arrays.asList(
                "Tıbbi rapor: Mağdurun dizinde sıyrık, bilekte zedelenme",
                "Sanık ifadesi: \"Sadece çektim, kendi düştü, vurmadım.\"",
                "Tanık esnaf: \"Çantayı çekiştirdiler, çocuk kadını ittirip kaçtı.\""
            ),
            steps4,
            "MAHKEME KARARI: Eylemde, mağdura malı vermesi için mağdurun iradesini kıracak boyutta bir şiddet uygulanmadığı, mağdurun çantayı bırakmaması sebebiyle dengesini kaybedip düştüğü anlaşıldığından sanık TCK m.142/2-b (Kapkaç suretiyle hırsızlık) suçundan cezalandırılmıştır.",
            "MAHKEME KARARI: Sanığın, çantayı direnen mağdurdan alabilmek için fiziki güç uyguladığı ve onu yere düşürdüğü (cebir unsuru) anlaşıldığından, sanık TCK m.148 uyarınca YAĞMA suçundan mahkum edilmiştir.",
            "TR"
        ));

        // CASE 5 (US): Fourth Amendment - The Traffic Stop Search
        List<Case.TrialStep> steps5 = new ArrayList<>();
        steps5.add(new Case.TrialStep(
            "Opening Statement",
            "Present your opening argument on whether the search was constitutional.",
            Arrays.asList(
                new Case.Choice(
                    "The officer had no reasonable suspicion beyond the broken taillight. Extending the stop to search the trunk turned a routine traffic stop into an unconstitutional fishing expedition.",
                    "The People submit the officer smelled marijuana through the window before the search began, which independently establishes probable cause under the automobile exception.",
                    25
                ),
                new Case.Choice(
                    "My client never consented to any search. Silence is not consent, and the officer's request to 'take a quick look' does not meet the voluntariness standard.",
                    "The People note the defendant stepped out of the vehicle and gestured toward the trunk without objection, which courts have treated as implied consent.",
                    20
                ),
                new Case.Choice(
                    "The stop itself was pretextual — the officer admitted on the dashcam he was 'looking for an excuse' to search the vehicle.",
                    "The People argue subjective intent is irrelevant under Whren v. United States; an objectively valid traffic violation justifies the stop regardless of the officer's motive.",
                    15
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "The officer detected the odor of marijuana emanating from the vehicle upon approach, which under the automobile exception provides probable cause to search without a warrant.",
                    "The Defense responds that the bodycam audio never records the officer mentioning any odor until after the search began — an after-the-fact justification.",
                    25
                ),
                new Case.Choice(
                    "The defendant voluntarily stepped out and opened the trunk himself when asked, which is clear, unambiguous consent under the totality of the circumstances.",
                    "The Defense argues a solitary driver facing an armed officer at night cannot meaningfully 'volunteer' consent — this is textbook coercion, not free choice.",
                    20
                ),
                new Case.Choice(
                    "Even if the stop was pretextual, Whren v. Ohio makes clear that an officer's subjective motivation is irrelevant so long as an objective traffic violation occurred.",
                    "The Defense counters that Whren governs the stop, not the subsequent search — the search itself still required independent justification, which the People have not shown.",
                    15
                )
            )
        ));
        steps5.add(new Case.TrialStep(
            "Evidence & Precedent Analysis",
            "Argue how the dashcam footage and prior case law should be weighed.",
            Arrays.asList(
                new Case.Choice(
                    "The dashcam timestamp proves the trunk was opened ninety seconds before any mention of an odor is heard on the audio — the smell was invented after the fact to justify an already-completed search.",
                    "The People argue audio pickup delay on patrol dashcams is well documented and does not disprove that the odor was detected in real time.",
                    25
                ),
                new Case.Choice(
                    "Under Rodriguez v. United States, prolonging a stop beyond the time reasonably required to address the traffic violation, absent independent reasonable suspicion, itself violates the Fourth Amendment.",
                    "The People argue the extension here was minimal — under two minutes — and courts have found comparable delays de minimis when probable cause develops during the stop.",
                    25
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "The officer's twelve years of narcotics-interdiction experience and specialized K-9 training make his identification of the odor credible and sufficient to establish probable cause on its own.",
                    "The Defense responds that experience alone cannot substitute for contemporaneous documentation — the absence of any radio call or bodycam mention until after the search is fatal to that claim.",
                    20
                ),
                new Case.Choice(
                    "Under Rodriguez, an officer may extend a stop briefly once specific, articulable facts arise during the encounter — here, the defendant's inconsistent answers about his travel plans supplied exactly that.",
                    "The Defense argues nervousness and inconsistent small talk are not 'specific, articulable facts' under Terry v. Ohio; that standard requires far more than a driver being nervous around police.",
                    15
                )
            )
        ));
        steps5.add(new Case.TrialStep(
            "Closing Statement",
            "Deliver your final argument to the court.",
            Arrays.asList(
                new Case.Choice(
                    "The exclusionary rule exists precisely for cases like this: without a contemporaneous, credible basis for probable cause, the evidence recovered from an unlawful search must be suppressed.",
                    "The People maintain the totality of the circumstances — the odor, the inconsistent statements, and the defendant's own gesture toward the trunk — together supply probable cause even if no single fact is dispositive alone.",
                    25
                ),
                new Case.Choice(
                    "Even under the People's own version of events, the search exceeded the scope of any consent given and extended the stop well past its lawful purpose under Rodriguez.",
                    "The People argue scope objections were never raised at the time of the search and are being raised now only as an afterthought.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "The totality of the circumstances — odor, inconsistent statements, and voluntary cooperation — meets the probable cause standard this Court has applied in countless similar automobile searches.",
                    "The Defense argues 'totality of the circumstances' cannot rescue a search built on facts documented only after the fact — that inverts the burden the Fourth Amendment places on the government.",
                    25
                ),
                new Case.Choice(
                    "Regardless of any procedural questions, the substantial quantity of narcotics recovered demonstrates this was never an innocent traffic stop, and the evidence should stand.",
                    "The Defense responds that the seriousness of what was found cannot retroactively justify how it was found — that is precisely what the exclusionary rule forbids.",
                    15
                )
            )
        ));

        CASES.add(new Case(
            5,
            "criminal",
            "Criminal Law",
            "Fourth Amendment: The Traffic Stop Search",
            "Police searched the defendant's car after a routine traffic stop and found narcotics in the trunk. Was the search constitutional, or must the evidence be suppressed?",
            "Medium",
            "~5 min",
            "U.S. Const. amend. IV",
            "Defendant was pulled over for a broken taillight. During the stop, the officer asked the defendant to step out of the vehicle and subsequently searched the trunk, recovering a significant quantity of narcotics. The officer claims he smelled marijuana and that the defendant gestured toward the trunk without objection. The defense claims no odor was mentioned until after the search began, based on the dashcam recording, and that the stop was unlawfully prolonged.",
            Arrays.asList(
                "Dashcam footage: no mention of odor until 90 seconds after the trunk was opened",
                "Officer's testimony: twelve years of narcotics-interdiction experience, claims to have smelled marijuana on approach",
                "Defendant's statement: denies consenting to any search, says he was told to 'step out and open the trunk'",
                "Stop duration: traffic stop extended by approximately ninety seconds before the search began"
            ),
            steps5,
            "COURT'S RULING: The Court finds the People failed to establish contemporaneous probable cause independent of the search itself. The dashcam timeline undermines the officer's claim, and the stop was unlawfully prolonged under Rodriguez v. United States. The evidence recovered from the trunk is SUPPRESSED under the exclusionary rule, and the case against the defendant is DISMISSED.",
            "COURT'S RULING: The Court finds the totality of the circumstances — the officer's training and experience, the odor of marijuana, and the defendant's own cooperative gestures toward the trunk — sufficient to establish probable cause under the automobile exception. The brief extension of the stop was justified by facts developing in real time. The evidence is ADMITTED, and the defendant is found guilty on the narcotics charge.",
            "US"
        ));

        // CASE 6 (UK): R v. Defendant - The Limits of Self-Defence
        List<Case.TrialStep> steps6 = new ArrayList<>();
        steps6.add(new Case.TrialStep(
            "Opening Statement",
            "Set out your case on whether the force used was reasonable self-defence.",
            Arrays.asList(
                new Case.Choice(
                    "My client was woken by an intruder in his home at 2am. Under the 'householder case' provisions, even disproportionate force is not automatically unlawful provided it was not grossly disproportionate.",
                    "The Crown submits that striking a fleeing, unarmed intruder twice with a cricket bat after he was already retreating goes well beyond what any householder provision could excuse.",
                    25
                ),
                new Case.Choice(
                    "My client genuinely believed, however mistaken, that he was in imminent danger, and under R v Williams (Gladstone) he is entitled to be judged on the facts as he honestly believed them to be.",
                    "The Crown responds that even accepting his honest belief, the degree of force used must still be reasonable in relation to the threat he believed he faced — and two blows to a retreating man was not.",
                    20
                ),
                new Case.Choice(
                    "This was a spontaneous reaction in the darkness and confusion of the moment; the law does not expect a person under attack to 'weigh to a nicety the exact measure of necessary defensive action', as established in Palmer v The Queen.",
                    "The Crown notes there was a clear pause between the first and second blow, during which the intruder was already turning to leave — this was not one continuous reflexive act.",
                    15
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "The intruder was found to be unarmed, already retreating toward the door, when the second and fatal blow was struck — this is not defence, it is retribution.",
                    "The Defence responds that in the dark, at 2am, with adrenaline surging, the defendant had no reliable way to know the intruder was unarmed or genuinely retreating rather than repositioning to attack.",
                    25
                ),
                new Case.Choice(
                    "Section 76 of the Criminal Justice and Immigration Act 2008 is clear that the degree of force must be reasonable in the circumstances as the defendant believed them — and no reasonable person could believe a second blow to a fleeing man was necessary.",
                    "The Defence argues the 'householder case' standard under s.76(5A) permits force that is not merely reasonable but only excludes force that is grossly disproportionate — a materially higher bar the Crown has not met.",
                    20
                )
            )
        ));
        steps6.add(new Case.TrialStep(
            "Evidence & Precedent Analysis",
            "Weigh the forensic evidence and how the leading authorities apply.",
            Arrays.asList(
                new Case.Choice(
                    "Forensic evidence places the second blow to the back of the intruder's head, consistent with him already turning away — directly undermining any claim of a continuing threat at the moment force was used.",
                    "The Crown's own forensic expert conceded on cross-examination that a struggling, disoriented person can turn or stumble in any direction — the wound location alone cannot prove the intruder was retreating rather than repositioning.",
                    25
                ),
                new Case.Choice(
                    "In R v Martin (Anthony), the Court of Appeal held that a householder using force against a burglar is judged more leniently, but still requires the force to correspond to a genuinely perceived ongoing threat — which the second blow does not satisfy here.",
                    "The Defence argues Martin actually supports the householder, and here, unlike Martin, my client had no history of excessive prior reactions and acted purely in the moment against an unknown intruder.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "The householder provisions were designed for split-second decisions against an ongoing threat, not for a second blow delivered after the threat had objectively already passed, as the forensic evidence shows.",
                    "The Defence responds that 'objectively passed' is a courtroom luxury; in the actual seconds of a home invasion, no householder can be expected to instantly recalibrate once an intruder turns.",
                    20
                ),
                new Case.Choice(
                    "R v Martin (Anthony) shows that even the s.76(5A) householder standard has limits, and courts have not hesitated to convict where the force used clearly outpaced any continuing threat.",
                    "The Defence argues each case turns on its own facts, and unlike Martin, there is no evidence here of premeditation, prior grudge, or excessive force beyond the two blows in immediate succession.",
                    15
                )
            )
        ));
        steps6.add(new Case.TrialStep(
            "Closing Statement",
            "Present your final submission to the jury.",
            Arrays.asList(
                new Case.Choice(
                    "Under s.76(5A), only grossly disproportionate force removes the defence for a householder — and two blows against an intruder in a dark, chaotic home invasion falls well short of that high bar.",
                    "The Crown maintains that a second blow to a retreating, unarmed man is precisely the kind of force Parliament intended to exclude, householder case or not.",
                    25
                ),
                new Case.Choice(
                    "My client's honest, even if mistaken, belief that he remained in danger is the standard this jury must apply, not hindsight forensic reconstruction from the safety of a courtroom.",
                    "The Crown responds that honest belief still has to translate into a reasonable response, and the forensic timeline gives the jury solid, non-speculative grounds to find it did not here.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "The forensic evidence, taken together with the pause between blows, shows this crossed from self-defence into retaliation, and the jury should convict of unlawful wounding.",
                    "The Defence urges the jury to remember that s.76(5A) exists exactly to avoid second-guessing householders with forensic hindsight — the law asks what he believed in the moment, not what a lab report shows afterward.",
                    25
                ),
                new Case.Choice(
                    "Even under the generous householder standard, Parliament did not give homeowners a licence to strike a retreating man a second time, and this jury should not extend one.",
                    "The Defence responds that 'retreating' is the Crown's characterisation, not an agreed fact, and where genuine doubt exists on that point, the benefit must go to the defendant.",
                    20
                )
            )
        ));

        CASES.add(new Case(
            6,
            "criminal",
            "Criminal Law",
            "R v. Defendant: The Limits of Self-Defence",
            "A homeowner struck a nighttime intruder twice with a cricket bat; the intruder later died of his injuries. Was this lawful self-defence, or did the force used cross into unlawful violence?",
            "Hard",
            "~5 min",
            "CJIA 2008, s.76",
            "The defendant awoke to find an intruder in his home at approximately 2am. He confronted the intruder with a cricket bat, striking him once. Forensic evidence shows a second blow was struck to the back of the intruder's head, who died of his injuries. The defence argues this was a continuous, honestly-believed act of self-defence under the 'householder case' provisions; the prosecution argues the second blow was struck after the intruder had already begun retreating, making it unlawful.",
            Arrays.asList(
                "Forensic report: second blow struck to the back of the head, consistent with the intruder turning away",
                "Defendant's statement: says he 'blacked out with fear' and does not clearly recall the second blow",
                "Timeline: an estimated 2-3 second gap between the first and second blow, based on blood spatter analysis",
                "Intruder was found to be unarmed"
            ),
            steps6,
            "COURT'S RULING: The jury finds that, applying the 'householder case' standard under s.76(5A), the prosecution has not proven the force used was grossly disproportionate. The defendant honestly believed he remained under threat in the chaos and darkness of a home invasion. The defendant is found NOT GUILTY of unlawful wounding.",
            "COURT'S RULING: The jury finds that the forensic evidence — the location of the second wound and the measurable pause between blows — establishes beyond reasonable doubt that the intruder had already begun retreating when the fatal blow was struck. This exceeds even the generous householder standard under s.76. The defendant is found GUILTY of unlawful wounding.",
            "UK"
        ));

        // CASE 7 (DE): Diebstahl oder Raub? - Theft or Robbery under German Law
        List<Case.TrialStep> steps7 = new ArrayList<>();
        steps7.add(new Case.TrialStep(
            "Opening Statement",
            "Present your opening argument on whether this was Diebstahl (§242 StGB) or Raub (§249 StGB).",
            Arrays.asList(
                new Case.Choice(
                    "My client snatched the bag and ran. There was no Gewalt (force) directed at the victim's person — she stumbled on her own when she instinctively tightened her grip. This is simple theft under §242 StGB, not robbery.",
                    "The Prosecution submits that pulling a bag with enough force to physically unbalance and injure the victim is itself Gewalt gegen eine Person within the meaning of §249 StGB, regardless of intent to injure.",
                    25
                ),
                new Case.Choice(
                    "Under German law, Raub requires that the Gewalt be the means used to overcome resistance and obtain the property — here, my client did not anticipate any resistance and used no force beyond the pull itself.",
                    "The Prosecution responds that the victim's grip on the bag was itself resistance, and the additional force used to wrench it free specifically to overcome that resistance satisfies §249 StGB.",
                    20
                ),
                new Case.Choice(
                    "This was a fleeting, opportunistic 'Trickdiebstahl'-style snatch, not the sustained physical confrontation the Bundesgerichtshof has required to establish Raub in its settled case law.",
                    "The Prosecution notes the BGH has repeatedly held that even brief force is sufficient for §249 StGB provided it is directed at overcoming the victim's resistance to the taking.",
                    15
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "The victim testified she held firmly onto her bag before the defendant pulled hard enough to throw her off balance — this pulling against her resistance is precisely the Gewalt element of Raub.",
                    "The Defence argues holding onto one's own bag is not 'resistance' in the legal sense contemplated by §249 StGB — it is simply not letting go, an entirely passive act.",
                    25
                ),
                new Case.Choice(
                    "The medical report confirms a wrist sprain consistent with forceful pulling against sustained resistance, which the BGH has repeatedly recognised as sufficient Gewalt for Raub even without a separate blow being struck.",
                    "The Defence responds that an incidental injury from a struggle over a bag does not retroactively convert an opportunistic snatch into the more serious offence of Raub.",
                    20
                )
            )
        ));
        steps7.add(new Case.TrialStep(
            "Evidence Assessment",
            "Assess the witness testimony and medical report.",
            Arrays.asList(
                new Case.Choice(
                    "The independent witness confirms the victim was already falling before any pulling motion is visible on the shop's security footage — undermining the claim that force was used to overcome resistance.",
                    "The Prosecution argues the footage's resolution and angle make it impossible to establish that precise sequence beyond reasonable doubt, and the victim's contemporaneous account should be preferred.",
                    25
                ),
                new Case.Choice(
                    "Even accepting the medical report, a wrist sprain is consistent with an involuntary reflex to grip tighter, not with active resistance the defendant had to specifically overcome, as §249 StGB requires.",
                    "The Prosecution responds that the Bundesgerichtshof does not require the resistance to be a deliberate strategy — an instinctive tightening grip that the defendant then had to forcibly break is legally sufficient resistance.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "The security footage timeline, cross-referenced with the medical report, shows the pulling force directly caused the fall and the injury — a textbook application of Gewalt to overcome resistance under §249 StGB.",
                    "The Defence argues footage of this quality cannot establish causation to the standard required in criminal proceedings, and any residual doubt must favour the defendant under the in dubio pro reo principle.",
                    20
                ),
                new Case.Choice(
                    "The independent witness's account corroborates the victim's version far more than the defence acknowledges — both describe the same forceful pulling motion immediately preceding the fall.",
                    "The Defence notes the independent witness only saw the aftermath, by his own admission, and cannot actually testify to what motion preceded the fall.",
                    25
                )
            )
        ));
        steps7.add(new Case.TrialStep(
            "Closing Statement",
            "Deliver your final submission to the court.",
            Arrays.asList(
                new Case.Choice(
                    "The evidence does not establish, to the exclusion of reasonable doubt, that force was used specifically to overcome resistance — this remains Diebstahl under §242 StGB, and the court should convict accordingly, not for the more serious Raub.",
                    "The Prosecution maintains the totality of the medical, testimonial, and video evidence meets the threshold for Raub under §249 StGB, and asks the court not to understate the violence actually used.",
                    25
                ),
                new Case.Choice(
                    "In dubio pro reo must govern here: where the footage and testimony leave genuine ambiguity about whether force overcame active resistance, that ambiguity must be resolved in my client's favour.",
                    "The Prosecution responds that the medical evidence alone removes any genuine ambiguity, and in dubio pro reo cannot be invoked simply because the defence dislikes the conclusion the evidence supports.",
                    20
                )
            ),
            Arrays.asList(
                new Case.Choice(
                    "Taken together, the witness account, footage, and medical report establish beyond reasonable doubt that force was used to overcome the victim's resistance — this court should convict for Raub under §249 StGB.",
                    "The Defence urges the court to resist reading certainty into evidence that is, on the Prosecution's own submissions, partial and disputed — §242 StGB is the more defensible verdict.",
                    25
                ),
                new Case.Choice(
                    "Even the defendant's own account does not dispute that the victim was physically thrown off balance by his pulling — that concession alone satisfies the Gewalt element required for Raub.",
                    "The Defence responds that conceding an accidental by-product of the snatch is not the same as conceding that force was deployed as a means to overcome resistance, which is what §249 StGB actually requires.",
                    20
                )
            )
        ));

        CASES.add(new Case(
            7,
            "criminal",
            "Criminal Law",
            "Diebstahl oder Raub? Theft or Robbery under German Law",
            "The defendant pulled a bag from a pedestrian's shoulder; she fell and was injured. Was this Diebstahl (theft, §242 StGB) or Raub (robbery, §249 StGB)?",
            "Medium",
            "~5 min",
            "StGB §242, §249",
            "The defendant pulled a shoulder bag from the victim as she walked down the street. She held on briefly before losing her grip and falling, spraining her wrist in the process. The defendant fled with the bag. The defence argues this was opportunistic theft with no force directed at the victim's person; the prosecution argues the forceful pulling against the victim's grip constitutes the Gewalt (force) element that elevates the offence to Raub.",
            Arrays.asList(
                "Medical report: wrist sprain consistent with forceful pulling",
                "Defendant's statement: \"I just pulled and ran, I didn't push her.\"",
                "Independent witness: saw the victim fall but only after the pulling motion had already begun",
                "Security footage: partial view, shows the bag being pulled and the victim losing balance"
            ),
            steps7,
            "COURT'S RULING: The court finds that the prosecution has not proven, to the exclusion of reasonable doubt, that the force used was specifically directed at overcoming the victim's resistance rather than being an incidental consequence of the snatch. In dubio pro reo, the defendant is convicted of Diebstahl under §242 StGB, not Raub.",
            "COURT'S RULING: The court finds that the medical evidence, witness testimony, and footage together establish that the defendant used force against the victim's person to overcome her resistance and complete the taking. This satisfies the Gewalt element under §249 StGB. The defendant is convicted of Raub.",
            "DE"
        ));
    }

    public static List<Case> getCases() {
        return CASES;
    }

    public static List<Case> getCasesForJurisdiction(String jurisdiction) {
        String code = jurisdiction != null ? jurisdiction.toUpperCase() : "TR";
        List<Case> filtered = new ArrayList<>();
        for (Case c : CASES) {
            if (code.equals(c.getJurisdiction())) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    public static Case getCaseById(int id) {
        for (Case c : CASES) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }
}
