package me.universi.capacity.dto;

import jakarta.validation.constraints.NotNull;
import me.universi.capacity.enums.ContentStatusType;

public record UpdateContentStatusDTO(
    @NotNull
    ContentStatusType contentStatusType
) { }
