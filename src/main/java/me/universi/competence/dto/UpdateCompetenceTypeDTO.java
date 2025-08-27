package me.universi.competence.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@Schema( description = "Request body for updating CompetenceTypes" )
public record UpdateCompetenceTypeDTO(
    @Nullable
    @Schema(
        description = "The new name of the CompetenceType. Must not be in use by any other CompetenceType",
        examples = { "Python", "BPMN", "Engenharia de Software" }
    )
    String name,

    @Nullable
    @Schema( description = "Sets this CompetenceType to be reviewed and public" )
    Boolean reviewed
) {}
