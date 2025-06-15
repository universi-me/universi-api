package me.universi.link.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.link.enums.TypeLink;

public record CreateLinkDTO(
    @NotBlank
    String url,

    @NotNull
    TypeLink type,

    @Nullable
    String name
) { }
