package me.universi.roles.dto;

import java.util.Map;

import jakarta.annotation.Nullable;
import me.universi.roles.enums.FeaturesTypes;

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
