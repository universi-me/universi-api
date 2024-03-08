package me.universi.roles.enums;

public class Permission {
    public static final int DEFAULT = 4;
    public static final int NONE = 0;
    public static final int DISABLED = 1;
    public static final int READ = 2;
    public static final int READ_WRITE = 3;
    public static final int READ_WRITE_DELETE = 4;

    public static String getPermissionName(int permission) {
        switch(permission) {
            case NONE:
                return "NONE";
            case DISABLED:
                return "Desabilitada";
            case READ:
                return "Ver";
            case READ_WRITE:
                return "Ver e Editar";
            case READ_WRITE_DELETE:
                return "Ver, Editar e Apagar";
            default:
                return "DEFAULT";
        }
    }

}
