package me.universi.competence.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.competence.entities.Competence;

public record CreateCompetenceDTO(
    @JsonAlias( { "competenceTypeId" } )
    @NotBlank String competenceType,

    @NotNull
    String description,

    @NotNull
    @Min( Competence.MIN_LEVEL ) @Max( Competence.MAX_LEVEL )
    Integer level
) {
    public CreateCompetenceDTO {
        if ( description == null )
            description = "";
    }
}
