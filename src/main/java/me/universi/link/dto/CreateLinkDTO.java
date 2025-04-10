package me.universi.link.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.universi.link.enums.TypeLink;

public record CreateLinkDTO(
    @NotNull
    String url,

    @NotNull
    TypeLink type,

    @Nullable
    String name
) { }
