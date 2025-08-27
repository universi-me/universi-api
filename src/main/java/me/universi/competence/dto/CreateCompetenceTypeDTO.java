package me.universi.competence.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for a new CompetenceType" )
public record CreateCompetenceTypeDTO(
    @NotBlank
    @Schema(
        description = "The name of the new CompetenceType. Must not be in use by any existing CompetenceType",
        examples = { "Python", "BPMN", "Engenharia de Software" }
    )
    String name
) {}
