package me.universi.group.DTO;

import me.universi.profile.entities.Profile;

import java.util.List;
import java.util.Map;
import java.util.UUID;



public record CompetenceInfoDTO(
        String competenceName,
        UUID competenceTypeId,
        Map<Integer, List<Profile>> levelInfo

){}