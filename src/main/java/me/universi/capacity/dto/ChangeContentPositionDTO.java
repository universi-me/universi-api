package me.universi.capacity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChangeContentPositionDTO(
    @NotNull
    @Min( 0 )
    Integer moveTo
) { }
