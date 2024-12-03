package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.enums.ContentType;

public record CreateContentDTO(
    @NotNull( message = "O parâmetro 'url' não foi informado" )
    @NotBlank( message = "O parâmetro 'url' não pode estar vazio" )
    String url,

    @NotNull( message = "O parâmetro 'title' não foi informado" )
    @NotBlank( message = "O parâmetro 'title' não pode estar vazio" )
    String title,

    @NotNull( message = "O parâmetro 'type' não foi informado" )
    ContentType type,

    @Nullable
    String image,

    @Nullable
    String description,

    @NotNull( message = "O parâmetro 'rating' não foi informado" )
    @Min( 0 ) @Max( 5 )
    Integer rating,

    @Nullable
    List<UUID> categoriesIds,

    @Nullable
    List<UUID> foldersIds
) { }
