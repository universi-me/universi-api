package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ChangeFolderAssignmentsDTO(
    @NotNull( message = "O parâmetro 'addProfileIds' não foi informado" )
    List<UUID> addProfileIds,

    @NotNull( message = "O parâmetro 'removeProfileIds' não foi informado" )
    List<UUID> removeProfileIds
) { }
