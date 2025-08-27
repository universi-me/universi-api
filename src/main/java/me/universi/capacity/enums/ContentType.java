package me.universi.capacity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "The type of the Content material.\n\n"
    + "A `LINK` is a generic type for any kind of material;\n\n"
    + "A `FILE` is used for sharing any kind of file;\n\n"
    + "A `VIDEO` is used for sharing videos;\n\n"
    + "A `FOLDER` is used for sharing multiple files"
)
public enum ContentType {
    LINK("Link"),
    FILE("Arquivo"),
    VIDEO("Vídeo"),
    FOLDER("Pasta");

    public final String label;

    private ContentType(String label) {
        this.label = label;
    }
}
