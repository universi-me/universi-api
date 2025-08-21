package me.universi.profile.dto;

import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Request body for updating existing Departments" )
public record UpdateDepartmentDTO(
    Optional<String> name,

    Optional<String> acronym
) {}
