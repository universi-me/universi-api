package me.universi.link.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.link.enums.TypeLink;

public record CreateLinkDTO(
    @NotNull( message = "O parâmetro 'url' não foi informado" )
    @NotBlank( message = "O parâmetro 'url' não pode estar vazio" )
    String url,

    @NotNull( message = "O parâmetro 'type' não foi informado" )
    TypeLink type,

    @Nullable
    String name
) { }
