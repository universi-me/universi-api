package me.universi.institution.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateInstitutionDTO(
    @NotBlank( message = "O parâmetro 'name' não pode estar vazio" )
    String name
) { }
