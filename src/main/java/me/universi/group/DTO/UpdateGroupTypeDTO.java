package me.universi.group.DTO;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Request body for updating an existing GroupTypes" )
public record UpdateGroupTypeDTO(
    @JsonAlias( { "name" } )
    @Schema( description = "The new name of this GroupType. Must be unique amongst other GroupTypes" )
    Optional<String> label
) {}
