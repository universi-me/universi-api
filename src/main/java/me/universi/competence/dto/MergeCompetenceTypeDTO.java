package me.universi.competence.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema( description = "Request body for merging CompetenceTypes" )
public record MergeCompetenceTypeDTO(
    @NotNull
    @Schema( description = "The ID of the deleted CompetenceType" )
    UUID removedCompetenceType,

    @NotNull
    @Schema( description = "The ID of the CompetenceType to replace the deleted one" )
    UUID remainingCompetenceType
) { }
