package me.universi.role.enums;

// ! When adding a new FeaturesTypes add a new column on Role

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
