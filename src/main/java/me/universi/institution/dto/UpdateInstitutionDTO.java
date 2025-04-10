package me.universi.institution.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateInstitutionDTO(
    @NotBlank
    String name
) { }
