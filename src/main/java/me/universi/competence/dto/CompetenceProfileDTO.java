package me.universi.competence.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.profile.entities.Profile;

public class CompetenceProfileDTO {
    @JsonIgnore private @NotNull Competence competence;
    @JsonIgnore private @NotNull Profile profile;

    public CompetenceProfileDTO(@NotNull Competence competence, @NotNull Profile profile) {
        this.competence = competence;
        this.profile = profile;
    }

    public static List<CompetenceProfileDTO> allFromProfile(@NotNull Profile profile) {
        return profile
            .getCompetences()
            .stream()
            .map(c -> new CompetenceProfileDTO(c, profile))
            .toList();
    }

    public UUID getId() {
        return this.competence.getId();
    }

    public CompetenceType getCompetenceType() {
        return this.competence.getCompetenceType();
    }

    public String getTitle() {
        return this.competence.getTitle();
    }

    public String getDescription() {
        return this.competence.getDescription();
    }

    public int getLevel() {
        return this.competence.getLevel();
    }

    public Date getStartDate() {
        return this.competence.getStartDate();
    }

    public Date getEndDate() {
        return this.competence.getEndDate();
    }

    public Boolean getPresentDate() {
        return this.competence.getPresentDate();
    }

    public Date getCreationDate() {
        return this.competence.getCreationDate();
    }

    public boolean isHasBadge() {
        return this.profile.getCompetenceBadges()
            .stream()
            .anyMatch(ct -> ct.getId().equals(this.competence.getCompetenceType().getId()));
    }
}
