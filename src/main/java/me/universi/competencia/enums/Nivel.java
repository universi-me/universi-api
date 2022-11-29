package me.universi.competencia.enums;

public enum Nivel {
    NENHUMA_EXPERIENCIA("Nenhuma Experiência"),
    POUCA_EXPERIENCIA("Pouca Experiência"),
    EXPERIENTE("Experiente"),
    MUITO_EXPERIENTE("Muito Experiente"),
    MASTER("Master");

    public final String label;

    private Nivel(String label) {
        this.label = label;
    }
}
