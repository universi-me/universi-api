package me.universi.capacity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for duplicating moving a Folder to another Group" )
public record MoveFolderDTO(
    @NotBlank
    @Schema( description = "The Group the Folder will be removed from" )
    String originalGroupId,

    @NotBlank
    @Schema( description = "The Group the Folder will be added to" )
    String newGroupId
) { }
