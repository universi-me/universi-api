package me.universi.roles.dto;

import java.util.Collection;
import java.util.UUID;

import me.universi.group.entities.ProfileGroup;

public class RoleDTO {
    public UUID id;
    public UUID profile;
    public UUID group;
    public String name;
    public Collection<FeatureDTO> features;

    public static RoleDTO fromProfileGroup(ProfileGroup profileGroup) {
        RoleDTO dto = new RoleDTO();

        dto.id = profileGroup.role.id;
        dto.name = profileGroup.role.name;
        dto.profile = profileGroup.profile.getId();
        dto.group =  profileGroup.role.group.getId();

        dto.features = profileGroup.role.rolesFeatures
            .stream()
            .map(FeatureDTO::fromRolesFeature)
            .toList();

        return dto;
    }
}
