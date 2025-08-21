package me.universi.user.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/*
    Role, níveis de autoridades utilizadas para o SpringSecurity

    Ao editar, atualizar roleHierarchy em spring-security.xml
*/

@Schema( description = "Visible only to yourself or system administrators, indicates an User's access level to the platform.\n\n`ROLE_USER` being the default value for most users;\n\n`ROLE_ADMIN` being reserved for system administrators;\n\nand `ROLE_DEV` being reserved for platform developers" )
public enum Authority {
    ROLE_USER("User"),
    ROLE_DEV("Developer"),
    ROLE_ADMIN("Admin");

    public final String label;

    Authority(String label) {
        this.label = label;
    }
}
