package me.universi.capacity.enums;

public enum ContentStatusType {
    NOT_VIEWED("Não Visualizado"),
    VIEW("Visualizado"),
    DONE("Concluído");

    public final String label;

    private ContentStatusType(String label) {
        this.label = label;
    }
}
