package me.universi.group.enums;

public enum GroupEmailFilterType {
    END_WITH("Terminando em"),
    START_WITH("Começando em"),
    CONTAINS("Contendo"),
    EQUALS("Igual a"),
    MASK("Máscara ( * )"),
    REGEX("Padrão RegEx");
	
    public final String label;
	
    private GroupEmailFilterType(String label) {
        this.label = label;
    }
}
