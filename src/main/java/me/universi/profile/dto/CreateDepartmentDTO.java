package me.universi.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body used for creating new Departments" )
public record CreateDepartmentDTO(
    @NotBlank
    String name,

    @NotBlank
    String acronym
) {}
