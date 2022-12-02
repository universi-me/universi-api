package me.universi.usuario.enums;

/*
    Ao editar, atualizar roleHierarchy em spring-security.xml
*/

public enum Autoridade {
    ROLE_USER("Usuário"),
    ROLE_DEV("Desenvolvedor"),
    ROLE_ADMIN("Administrador");

    public final String label;

    private Autoridade(String label) {
        this.label = label;
    }
}
