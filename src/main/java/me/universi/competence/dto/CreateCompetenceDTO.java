package me.universi.competence.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.competence.entities.Competence;

public record CreateCompetenceDTO(
    @NotNull( message = "O parâmetro 'competenceTypeId' não foi informado" )
    UUID competenceTypeId,
    
    @NotNull( message = "O parâmetro 'description' não foi informado" )
    @NotBlank( message = "O parâmetro 'description' não pode estar vazio" )
    String description,

    @NotNull( message = "O parâmetro 'level' não foi informado" )
    @Min( Competence.MIN_LEVEL ) @Max( Competence.MAX_LEVEL )
    Integer level
) { }
