package me.universi.profile.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "A user's self-declared gender" )
public enum Gender {
    M("Masculine"),
    F("Feminine"),
    O("Other");

    public final String label;

    Gender(String label) {
        this.label = label;
    }
}
