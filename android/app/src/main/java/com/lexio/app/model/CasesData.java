package com.lexio.app.model;

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
            "MAHKEME KARARI: Sanık A.K.'nin eylemi kasten öldürmeye teşebbüs (TCK m.81, 35) kabul edilmiştir. Darbenin karın bölgesi gibi hayati bölgeye vurulmuş olması, kullanılan bıçağın 18 cm elverişli yapısı ve olay öncesi husumet göz önüne alınarak sanığın mahkumiyetine karar verilmiştir."
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
            "MAHKEME KARARI: Sanık M.D.'nin meşru müdafaa sınırını aştığına karar verilmiştir. Ancak sınırın aşılması olay anındaki mazur görülebilecek heyecan, korku veya telaştan ileri geldiğinden TCK m.27/2 maddesi gereğince sanığa ceza verilmesine yer olmadığına hükmedilmiştir."
        ));
    }

    public static List<Case> getCases() {
        return CASES;
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
