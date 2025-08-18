package me.universi.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for creating new ActivityTypes" )
public record CreateActivityTypeDTO(
    @NotBlank String name
) { }
