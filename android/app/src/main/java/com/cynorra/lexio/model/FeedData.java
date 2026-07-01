package com.cynorra.lexio.model;

import java.util.ArrayList;
import java.util.List;

public class FeedData {
    private static final List<FeedPost> POSTS = new ArrayList<>();

    static {
        POSTS.add(new FeedPost(1, "Av. Burak Yılmaz", "Avukat", "2 saat önce", "Yargıtay'ın son kararına göre, kira tespit davalarında yeni bilirkişi raporu kriterleri değişti. Dosyalarını kontrol etmenizi tavsiye ederim.", 45, 12));
        POSTS.add(new FeedPost(2, "Zeynep Kaya", "Stajyer Avukat", "5 saat önce", "İcra takibinde tebligat süreciyle ilgili pratik bir soru sormak istiyorum. Muhatap adreste bulunmazsa Mernis adresine doğrudan tebligat çıkarmak mantıklı mıdır?", 15, 34));
        POSTS.add(new FeedPost(3, "Av. Aslı Demir", "Avukat", "1 gün önce", "Ceza hukuku alanında uzmanlaşmak isteyen genç meslektaşlarıma tavsiyem: Yargıtay Ceza Genel Kurulu kararlarını düzenli olarak okuyun. Lexio'daki vakalar da fena değil!", 120, 8));
        POSTS.add(new FeedPost(4, "Mehmet Taş", "Hukuk Öğrencisi", "2 gün önce", "Finaller yaklaşıyor. Borçlar Hukuku Genel Hükümler için pratik çalışma notu olan var mı?", 30, 5));
    }

    public static List<FeedPost> getPosts() {
        return POSTS;
    }
}
