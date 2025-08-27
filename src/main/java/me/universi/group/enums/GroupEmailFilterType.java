package me.universi.group.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "The kind of GroupEmailFilter applied" )
public enum GroupEmailFilterType {
    END_WITH("Terminando em"),
    START_WITH("Começando com"),
    CONTAINS("Contendo"),
    EQUALS("Igual a"),
    MASK("Máscara ( * )"),
    REGEX("Padrão RegEx");
	
    public final String label;
	
    private GroupEmailFilterType(String label) {
        this.label = label;
    }
}
