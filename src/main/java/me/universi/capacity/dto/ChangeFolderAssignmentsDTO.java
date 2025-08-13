package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema( description = "Request body for changing the assignments of a Folder" )
public record ChangeFolderAssignmentsDTO(
    @NotNull
    @Schema( description = "A list of Profile IDs to add an assignment" )
    List<UUID> addProfileIds,

    @NotNull
    @Schema( description = "A list of Profile IDs to remove an assignment" )
    List<UUID> removeProfileIds
) { }
