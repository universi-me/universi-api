package me.universi.roles.enums;

// ! When adding a new FeaturesTypes add a new column on Roles

public enum FeaturesTypes {
    FEED("Publicações"),
    CONTENT("Conteúdo"),
    GROUP("Grupo"),
    PEOPLE("Pessoas"),
    COMPETENCE("Competência"),
    JOBS("Vagas");

    public final String label;

    private FeaturesTypes(String label) {
        this.label = label;
    }
}
