package me.universi.capacity.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema( description = "Request body for duplicating a Folder" )
public record DuplicateFolderDTO(
    @NotNull
    @Schema( description = "The list of Group's path or IDs the duplicate will be added to" )
    List<String> groups

    // TODO: Add edit folder params to edit duplicate on creation
) { }
