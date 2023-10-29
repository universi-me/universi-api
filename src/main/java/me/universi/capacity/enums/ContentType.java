package me.universi.capacity.enums;

public enum ContentType {
    LINK("Link"),
    FILE("Arquivo"),
    VIDEO("Vídeo"),
    FOLDER("Pasta");

    public final String label;

    private ContentType(String label) {
        this.label = label;
    }
}
