package me.universi.profile.enums;

public enum Gender {
    M("Masculine"),
    F("Feminine"),
    O("Other");

    public final String label;

    Gender(String label) {
        this.label = label;
    }
}
