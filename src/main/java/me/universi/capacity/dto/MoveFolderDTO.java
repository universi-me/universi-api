package me.universi.capacity.dto;

import jakarta.validation.constraints.NotNull;

public record MoveFolderDTO(
    @NotNull
    String originalGroupId,

    @NotNull
    String newGroupId
) { }
