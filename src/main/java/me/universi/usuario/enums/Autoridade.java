package me.universi.usuario.enums;

public enum Autoridade {
    ROLE_USER("Usuário"),
    ROLE_ADMIN("Administrador");

    public final String label;

    private Autoridade(String label) {
        this.label = label;
    }
}
