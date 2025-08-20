package me.universi.group.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record CompetenceFilterRequestDTO(
        @Schema( description = "CompetenceType ID or name" )
        String id,
        @Max(3)
        @Min(0)
        @Schema( description = "Required level for the Competence" )
        int level
) {
}
