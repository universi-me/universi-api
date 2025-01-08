package me.universi.role.dto;

import java.util.Map;

import jakarta.annotation.Nullable;
import me.universi.role.enums.FeaturesTypes;

public record UpdateRoleDTO(
    @Nullable
    String name,

    @Nullable
    String description,

    @Nullable
    String group,

    @Nullable
    Map<FeaturesTypes, Integer> features
) {}
