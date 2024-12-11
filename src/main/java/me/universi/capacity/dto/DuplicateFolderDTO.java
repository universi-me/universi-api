package me.universi.capacity.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record DuplicateFolderDTO(
    @NotNull
    List<String> groups

    // TODO: Add edit folder params to edit duplicate on creation
) { }
