package me.universi.capacity.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@Schema( description = "Request body for updating existing Categories" )
public record UpdateCategoryDTO(
    @Nullable
    @Schema(
        description = "The new name of the Category. Must not be in use by any other Category",
        examples = { "Python", "Arquitetura de Software", "BPMN", }
    )
    String name,

    @Nullable
    @Schema( description = "New ImageMetadata ID used for the Category" )
    UUID image
) { }
