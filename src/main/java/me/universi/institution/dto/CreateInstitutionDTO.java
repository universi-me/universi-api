package me.universi.institution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInstitutionDTO(
    @NotNull( message = "O parâmetro 'name' não foi informado" )
    @NotBlank( message = "O parâmetro 'name' não pode estar vazio" )
    String name
) { }
