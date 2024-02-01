package me.universi.group.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record CompetenceFilterRequestDTO(
        String id,
        @Max(3)
        @Min(0)
        int level
) {
}
