package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ChangeFolderAssignmentsDTO(
    @NotNull
    List<UUID> addProfileIds,

    @NotNull
    List<UUID> removeProfileIds
) { }
