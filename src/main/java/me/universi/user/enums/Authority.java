package me.universi.user.enums;

/*
    Role, níveis de autoridades utilizadas para o SpringSecurity

    Ao editar, atualizar roleHierarchy em spring-security.xml
*/

public enum Authority {
    ROLE_USER("User"),
    ROLE_DEV("Developer"),
    ROLE_ADMIN("Admin");

    public final String label;

    Authority(String label) {
        this.label = label;
    }
}
