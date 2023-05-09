package me.universi.competence.enums;

public enum Level {
    NO_EXPERIENCE("Nenhuma Experiência"),
    LITTLE_EXPERIENCE("Pouca Experiência"),
    EXPERIENCED("Experiente"),
    VERY_EXPERIENCED("Muito Experiente"),
    MASTER("Master");

    public final String label;

    private Level(String label) {
        this.label = label;
    }
}
