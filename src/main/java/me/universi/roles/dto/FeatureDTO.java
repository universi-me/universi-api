package me.universi.roles.dto;

import java.util.UUID;
import me.universi.roles.enums.FeaturesTypes;

public class FeatureDTO {
    public UUID id;
    public FeaturesTypes featureType;
    public int permission = 0;
}
