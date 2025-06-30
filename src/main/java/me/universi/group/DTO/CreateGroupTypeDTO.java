package me.universi.group.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupTypeDTO(
    @JsonAlias( { "name" } )
    @NotBlank String label
) {}
