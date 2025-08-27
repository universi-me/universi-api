package me.universi.competence.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.competence.entities.Competence;

@Schema( description = "Request body for creating a new Competence" )
public record CreateCompetenceDTO(
    @JsonAlias( { "competenceTypeId" } )
    @Schema( description = "The ID or name of the CompetenceType this competence refers to" )
    @NotBlank String competenceType,

    @NotNull
    @Schema( description = "A short plain text description of this Competence" )
    String description,

    @NotNull
    @Min( Competence.MIN_LEVEL ) @Max( Competence.MAX_LEVEL )
    @Schema( description = "The level of the Profile's proficiency in the CompetenceType" )
    Integer level
) {
    public CreateCompetenceDTO {
        if ( description == null )
            description = "";
    }
}
