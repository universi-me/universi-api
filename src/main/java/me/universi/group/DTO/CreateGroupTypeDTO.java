package me.universi.group.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for creating new GroupTypes" )
public record CreateGroupTypeDTO(
    @JsonAlias( { "name" } )
@Schema( description = "The name of the new GroupType. Must be unique amongst other GroupTypes" )
    @NotBlank String label
) {}
