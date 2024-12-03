package me.universi.capacity.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryDTO(
    @NotNull( message = "O parâmetro 'name' não foi informado" )
    @NotBlank( message = "O parâmetro 'name' não pode estar vazio" )
    String name,

    @Nullable
    String image
) { }
