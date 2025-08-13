package me.universi.competence.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import me.universi.competence.entities.Competence;

@Schema( description = "Request body for updating existing Competences" )
public record UpdateCompetenceDTO(
    @JsonAlias( { "competenceTypeId" } )
    @Schema( description = "The ID or name of the new CompetenceType to be use" )
    Optional<String> competenceType,

    @Nullable
    @Schema( description = "A short plain text description of this Competence" )
    String description,

    @Nullable
    @Min( Competence.MIN_LEVEL ) @Max( Competence.MAX_LEVEL )
    @Schema( description = "The level of the Profile's proficiency in the CompetenceType" )
    Integer level
) { }
