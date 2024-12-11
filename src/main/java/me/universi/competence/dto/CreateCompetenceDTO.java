package me.universi.competence.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.competence.entities.Competence;

public record CreateCompetenceDTO(
    @NotNull
    UUID competenceTypeId,

    @NotBlank
    String description,

    @NotNull
    @Min( Competence.MIN_LEVEL ) @Max( Competence.MAX_LEVEL )
    Integer level
) { }
