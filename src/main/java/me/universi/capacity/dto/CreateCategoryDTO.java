package me.universi.capacity.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for creating new Categories" )
public record CreateCategoryDTO(
    @NotBlank
    @Schema(
        description = "The name of the new Category. Must not be in use by any existing Category",
        examples = { "Python", "Arquitetura de Software", "BPMN", }
    )
    String name,

    @Nullable
    @Schema( description = "ImageMetadata ID used for the Category" )
    UUID image
) { }
