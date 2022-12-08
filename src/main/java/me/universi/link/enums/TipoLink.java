package me.universi.link.enums;

public enum TipoLink {
    LINK("Link", "link-45deg"),
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
    SPOTIFY("Spotify", "spotify"),
    YOUTUBE("Youtube", "youtube"),
    SKYPE("Skype", "skype"),
    STACK("Stack overflow", "stack-overflow"),
    FACEBOOK("Facebook", "facebook");

    public final String label;

    // bootstrap icon nome, do Bootstrap Icons
    public final String bootstrapIconName;

    private TipoLink(String label, String bootstrapIconName) {
        this.label = label;
        this.bootstrapIconName = bootstrapIconName;
    }
}
