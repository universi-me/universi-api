package me.universi.roles.dto;

import java.util.UUID;

import me.universi.roles.entities.RolesFeature;
import me.universi.roles.enums.FeaturesTypes;

public class FeatureDTO {
    public UUID id;
    public FeaturesTypes featureType;
    public int permission = 0;

    public static FeatureDTO fromRolesFeature(RolesFeature feature) {
        var dto = new FeatureDTO();

        dto.id = feature.id;
        dto.featureType = feature.featureType;
        dto.permission = feature.permission;

        return dto;
    }
}
