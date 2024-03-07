package me.universi.roles.enums;

public enum FeaturesTypes {
    FEED("Publicações"),
    CONTENT("Conteúdo"),
    GROUP("Grupo"),
    PEOPLE("Pessoas"),
    COMPETENCE("Competência");

    public final String label;

    private FeaturesTypes(String label) {
        this.label = label;
    }
}
