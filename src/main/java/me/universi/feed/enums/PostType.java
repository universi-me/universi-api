package me.universi.feed.enums;

public enum PostType {
    GROUP("Grupo"),
    GENERAL("Geral");
    public final String label;

    private PostType(String label) {
        this.label = label;
    }
}