package me.universi.profile.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentDTO(
    @NotBlank
    String name,

    @NotBlank
    String acronym
) {}
