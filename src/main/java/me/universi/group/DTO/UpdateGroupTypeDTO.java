package me.universi.group.DTO;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAlias;

public record UpdateGroupTypeDTO(
    @JsonAlias( { "name" } )
    Optional<String> label
) {}
