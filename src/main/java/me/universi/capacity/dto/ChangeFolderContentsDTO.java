package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@Schema( description = "Request body for changing the Contents present in a Folder" )
public record ChangeFolderContentsDTO(
    @Nullable
    @Schema( description = "A list of Contents IDs to add to this Folder" )
    List<UUID> addContentsIds,

    @Nullable
    @Schema( description = "A list of Contents IDs to remove from this Folder" )
    List<UUID> removeContentsIds
) { }
