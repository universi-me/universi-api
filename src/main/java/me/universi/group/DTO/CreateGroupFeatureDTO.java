package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CreateGroupFeatureDTO(
        @NotBlank
        @JsonAlias( { "groupId", "groupPath" } )
        String group,

        @NotNull( message = "Nome da feature não pode ser nulo" )
        @NotBlank
        String name,

        String description,
        Boolean enabled
) { }
