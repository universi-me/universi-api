package me.universi.capacity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema( description = "Request body for changing a Content's position in a Folder" )
public record ChangeContentPositionDTO(
    @NotNull
    @Min( 0 )
    @Schema( description = "The new position (starting at zero) of the Content" )
    Integer moveTo
) { }
