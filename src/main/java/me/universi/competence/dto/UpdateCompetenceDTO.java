package me.universi.competence.dto;

import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import me.universi.competence.entities.Competence;

public record UpdateCompetenceDTO(
    @Nullable
    UUID competenceTypeId,
    
    @Nullable
    String description,

    @Nullable
    @Min( Competence.MIN_LEVEL ) @Max( Competence.MAX_LEVEL )
    Integer level
) { }
