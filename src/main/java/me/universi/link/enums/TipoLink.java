package me.universi.link.enums;

public enum TipoLink {
    GITHUB("GitHub", "github"),
    GIT("Git", "git"),
    TWITTER("Twitter", "twitter"),
    WORDPRESS("Wordpress", "wordpress"),
    TELEGRAM("Telegram", "telegram"),
    LINKEDIN("LinkedIn", "linkedin");

    public final String label;
    public final String bootstrapIconName;

    private TipoLink(String label, String bootstrapIconName) {
        this.label = label;
        this.bootstrapIconName = bootstrapIconName;
    }
}
