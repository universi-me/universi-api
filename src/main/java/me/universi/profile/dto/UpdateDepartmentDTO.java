package me.universi.profile.dto;

import java.util.Optional;

public record UpdateDepartmentDTO(
    Optional<String> name,

    Optional<String> acronym
) {}
