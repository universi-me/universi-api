package me.universi.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for creating new Roles" )
public record CreateRoleDTO(
    @NotBlank
    @Schema( description = "Name of the created Role", examples = { "Leader", } )
    String name,

    @Nullable
    @Schema( description = "Short plain text describing this Role", deprecated = true )
    String description,

    @NotBlank
    @Schema( description = "Group ID or path in which the Role will be created" )
    String group
) {}
