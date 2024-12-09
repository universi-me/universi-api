package me.universi.competence.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.constraints.NotNull;
import me.universi.competence.entities.Competence;
import me.universi.competence.services.CompetenceProfileService;
import me.universi.profile.entities.Profile;

public class CompetenceProfileDTO {
    @JsonUnwrapped private @NotNull Competence competence;
    @JsonIgnore private @NotNull Profile profile;

    public CompetenceProfileDTO(@NotNull Competence competence, @NotNull Profile profile) {
        this.competence = competence;
        this.profile = profile;
    }

    public static List<CompetenceProfileDTO> allFromProfile(@NotNull Profile profile) {
        return CompetenceProfileService.getInstance().findCompetenceByProfileId( profile.getId() )
            .stream()
            .map(c -> new CompetenceProfileDTO(c, profile))
            .toList();
    }

    public boolean isHasBadge() {
        return this.profile.getCompetenceBadges()
            .stream()
            .anyMatch(ct -> ct.getId().equals(this.competence.getCompetenceType().getId()));
    }
}
