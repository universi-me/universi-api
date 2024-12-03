package me.universi.capacity.dto;

import jakarta.validation.constraints.NotNull;
import me.universi.capacity.enums.ContentStatusType;

public record UpdateContentStatusDTO(
    @NotNull( message = "O parâmetro 'contentStatusType' não foi informado" )
    ContentStatusType contentStatusType
) { }
