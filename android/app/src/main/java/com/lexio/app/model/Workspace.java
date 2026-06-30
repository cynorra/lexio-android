package com.lexio.app.model;

public class Workspace {
    private int id;
    private String name;
    private String description;
    private int memberCount;
    private String iconEmoji;

    public Workspace(int id, String name, String description, int memberCount, String iconEmoji) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.memberCount = memberCount;
        this.iconEmoji = iconEmoji;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMemberCount() { return memberCount; }
    public String getIconEmoji() { return iconEmoji; }
}
