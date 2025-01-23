package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;

public record ChangeFolderContentsDTO(
    @Nullable
    List<UUID> addContentsIds,

    @Nullable
    List<UUID> removeContentsIds
) { }
