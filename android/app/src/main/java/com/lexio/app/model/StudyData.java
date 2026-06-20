package com.lexio.app.model;

import java.util.ArrayList;
import java.util.List;

public class StudyData {
    private static final List<StudyTopic> TOPICS = new ArrayList<>();

    static {
        TOPICS.add(new StudyTopic(
            "1",
            "Suçun Unsurları ve Kast / Taksir",
            "Ceza Hukuku Genel",
            "Kast ve taksir kavramları, aralarındaki farklar ve Yargıtay'ın kast nitelendirmesindeki kriterleri.",
            "<h3>Kast (TCK m.21)</h3>" +
            "<p>Kast, suçun kanuni tanımındaki unsurların bilerek ve istenerek gerçekleştirilmesidir. Failin neticeyi öngörmesi ve bu neticenin gerçekleşmesini istemesi durumunda doğrudan kast mevcuttur.</p>" +
            "<h4>Olası Kast</h4>" +
            "<p>Failin suçun kanuni tanımındaki unsurların gerçekleşebileceğini öngörmesine rağmen, fiili işlemesi halinde olası kast vardır. Burada fail, neticenin gerçekleşmesini doğrudan istememekte ancak olursa olsun mantığıyla göze almaktadır.</p>" +
            "<h3>Taksir (TCK m.22)</h3>" +
            "<p>Taksir, dikkat ve özen yükümlülüğüne aykırılık dolayısıyla, bir davranışın öngörülemeyen veya öngörülse de istenmeyen neticesinin gerçekleşmesidir.</p>" +
            "<h4>Bilinçli Taksir</h4>" +
            "<p>Failin öngördüğü neticenin gerçekleşmesini istememesine rağmen, hareketine devam ederek neticeyi meydana getirmesidir. Olası kast ile bilinçli taksir arasındaki en büyük fark: Olası kastta fail 'olursa olsun' diyerek neticeyi kabullenirken, bilinçli taksirde neticenin gerçekleşmeyeceğine güvenmektedir.</p>",
            6
        ));

        TOPICS.add(new StudyTopic(
            "2",
            "Meşru Müdafaa ve Sınırın Aşılması",
            "Ceza Hukuku Genel",
            "Meşru savunma şartları, saldırı ile savunma arasındaki orantılılık ve sınırın korkuyla aşılması hali.",
            "<h3>Meşru Savunma (TCK m.25/1)</h3>" +
            "<p>Gerek kendisine ve gerek başkasına ait bir hakka yönelmiş, gerçekleşen, gerçekleşmesi veya tekrarı muhakkak olan haksız bir saldırıyı o anda hal ve koşullara göre saldırı ile orantılı biçimde defetmek zorunluluğu ile işlenen fiillerden dolayı faile ceza verilmez.</p>" +
            "<h4>Saldırıya İlişkin Şartlar:</h4>" +
            "<p>1. Haksız bir saldırı olmalıdır.<br/>2. Saldırı bir hakka yönelmelidir (can, mal, iffet vb.).<br/>3. Saldırı mevcut olmalı veya gerçekleşmesi kesin olmalıdır.</p>" +
            "<h4>Savunmaya İlişkin Şartlar:</h4>" +
            "<p>1. Savunma zorunlu olmalıdır.<br/>2. Savunma saldırana karşı yapılmalıdır.<br/>3. Savunma ile saldırı arasında orantı bulunmalıdır.</p>" +
            "<h3>Sınırın Aşılması (TCK m.27)</h3>" +
            "<p>Meşru savunmada sınırın aşılması mazur görülebilecek bir heyecan, korku veya telaştan ileri gelmişse faile ceza verilmez (TCK m.27/2).</p>",
            8
        ));

        TOPICS.add(new StudyTopic(
            "3",
            "Suça Teşebbüs ve Gönüllü Vazgeçme",
            "Ceza Hukuku Genel",
            "Teşebbüsün unsurları, icra hareketlerinin başlangıcı ve failin eylemden kendi rızasıyla vazgeçmesi.",
            "<h3>Suça Teşebbüs (TCK m.35)</h3>" +
            "<p>Kişi, işlemeyi kastettiği bir suçu elverişli hareketlerle doğrudan doğruya icraya başlayıp da elinde olmayan nedenlerle tamamlayamazsa teşebbüsten dolayı sorumlu tutulur.</p>" +
            "<h4>Şartları:</h4>" +
            "<p>1. Kasıtlı bir suç işleme niyeti olmalıdır.<br/>2. Suçun icra hareketlerine başlanmış olmalıdır (hazırlık hareketleri cezalandırılmaz).<br/>3. Kullanılan araç suçun tamamlanması için elverişli olmalıdır.<br/>4. Netice elde olmayan nedenlerle tamamlanamamış olmalıdır.</p>" +
            "<h3>Gönüllü Vazgeçme (TCK m.36)</h3>" +
            "<p>Fail, suçun icra hareketlerinden gönüllü vazgeçerse veya kendi çabalarıyla suçun tamamlanmasını önlerse, teşebbüsten dolayı cezalandırılmaz; fakat o ana kadar yapılan kısım başka bir suçu oluşturuyorsa sadece o suçtan cezalandırılır.</p>",
            5
        ));
    }

    public static List<StudyTopic> getTopics() {
        return TOPICS;
    }

    public static StudyTopic getTopicById(String id) {
        for (StudyTopic t : TOPICS) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }
}
