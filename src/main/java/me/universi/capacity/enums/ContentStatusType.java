package me.universi.capacity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "All possible Status for a Content.\n\n`NOT_VIEWED` means the user never opened the Content;\n\n`VIEW` means it was opened, but not completed;\n\nand `DONE` means the Content was completed by the user" )
public enum ContentStatusType {
    NOT_VIEWED("Não Visualizado"),
    VIEW("Visualizado"),
    DONE("Concluído");

    public final String label;

    private ContentStatusType(String label) {
        this.label = label;
    }
}
