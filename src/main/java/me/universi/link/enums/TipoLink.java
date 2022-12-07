package me.universi.link.enums;

public enum TipoLink {
    GITHUB("GitHub", "github"),
    GIT("Git", "git"),
    TWITTER("Twitter", "twitter"),
    WORDPRESS("Wordpress", "wordpress"),
    TELEGRAM("Telegram", "telegram"),
    REDDIT("Reddit", "reddit"),
    LINKEDIN("LinkedIn", "linkedin"),
    DISCORD("Discord", "discord"),
    PAYPAL("Paypal", "paypal"),
    WHATSAPP("Whatsapp", "whatsapp"),
    TRELLO("Trello", "trello"),
    SLACK("Slack", "slack"),
    SKYPE("Skype", "skype"),
    FACEBOOK("Facebook", "facebook"),
    LINK("Link", "link-45deg");

    public final String label;
    public final String bootstrapIconName;

    private TipoLink(String label, String bootstrapIconName) {
        this.label = label;
        this.bootstrapIconName = bootstrapIconName;
    }
}
