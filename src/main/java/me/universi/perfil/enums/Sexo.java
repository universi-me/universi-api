package me.universi.perfil.enums;

public enum Sexo {
    M("Masculino"),
    F("Feminino");

    public final String label;

    private Sexo(String label) {
        this.label = label;
    }
}
