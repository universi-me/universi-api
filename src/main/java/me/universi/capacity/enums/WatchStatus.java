package me.universi.capacity.enums;

public enum WatchStatus {
    NOT_VIEWED("Não Visto"),
    VIEW("Visto"),
    DONE("Concluído");

    public final String label;

    private WatchStatus(String label) {
        this.label = label;
    }
}
