package me.universi.link.enums;

public enum TipoLink {
    GITHUB("GitHub"),
    LINKEDIN("LinkedIn");

    public final String label;

    private TipoLink(String label) {
        this.label = label;
    }
}
