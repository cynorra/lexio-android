package com.cynorra.lexio.model;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceData {
    private static final List<Workspace> WORKSPACES = new ArrayList<>();

    static {
        WORKSPACES.add(new Workspace(1, "Ceza Hukuku Tartışmaları", "Yargıtay kararları ve ceza hukuku üzerine akademik/pratik tartışmalar.", 1245, "⚖️"));
        WORKSPACES.add(new Workspace(2, "Stajyer Avukatlar Yardımlaşma", "Adliye tecrübeleri, dilekçe örnekleri ve mesleki dayanışma.", 850, "📚"));
        WORKSPACES.add(new Workspace(3, "İş ve Sosyal Güvenlik Hukuku", "İşçi-işveren uyuşmazlıkları ve SGK davaları hakkında güncel gelişmeler.", 620, "💼"));
        WORKSPACES.add(new Workspace(4, "Hukuk Fakültesi Öğrencileri", "Vize/Final not paylaşımı ve genel sohbet.", 3400, "🎓"));
        WORKSPACES.add(new Workspace(5, "Bilişim Hukuku & KVKK", "Kişisel verilerin korunması, siber suçlar ve teknoloji hukuku.", 410, "💻"));
    }

    public static List<Workspace> getWorkspaces() {
        return WORKSPACES;
    }
}
