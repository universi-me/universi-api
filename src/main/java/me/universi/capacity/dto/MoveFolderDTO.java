package me.universi.capacity.dto;

import jakarta.validation.constraints.NotBlank;

public record MoveFolderDTO(
    @NotBlank
    String originalGroupId,

    @NotBlank
    String newGroupId
) { }
