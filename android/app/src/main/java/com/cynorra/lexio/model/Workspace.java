package com.cynorra.lexio.model;

public class Workspace {
    private int id;
    private String name;
    private String description;
    private int memberCount;
    private String iconEmoji;

    public Workspace() {
        // Required for Firestore
    }

    public Workspace(int id, String name, String description, int memberCount, String iconEmoji) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.memberCount = memberCount;
        this.iconEmoji = iconEmoji;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public String getIconEmoji() { return iconEmoji; }
    public void setIconEmoji(String iconEmoji) { this.iconEmoji = iconEmoji; }
}

