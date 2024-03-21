package me.universi.roles.dto;

import java.util.Collection;
import java.util.UUID;

public class RoleDTO {
    public UUID id;
    public UUID profile;
    public UUID group;
    public String name;
    public Collection<FeatureDTO> features;
}
