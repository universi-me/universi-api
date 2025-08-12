package me.universi.capacity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.enums.ContentStatusType;

@Schema( description = "Request body for updating a Content's status" )
public record UpdateContentStatusDTO(
    @NotNull
    @Schema( description = "The new status of this content" )
    ContentStatusType contentStatusType
) { }
