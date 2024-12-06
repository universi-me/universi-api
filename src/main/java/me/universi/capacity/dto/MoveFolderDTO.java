package me.universi.capacity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MoveFolderDTO(
    @NotNull( message = "O parâmetro 'originalGroupId' não foi informado" )
    @NotBlank( message = "O parâmetro 'originalGroupId' não pode estar vazio" )
    String originalGroupId,

    @NotNull( message = "O parâmetro 'newGroupId' não foi informado" )
    @NotBlank( message = "O parâmetro 'newGroupId' não pode estar vazio" )
    String newGroupId
) { }
