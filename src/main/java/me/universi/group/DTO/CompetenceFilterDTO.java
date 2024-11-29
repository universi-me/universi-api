package me.universi.group.DTO;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;

import java.util.List;
import java.util.UUID;

public record CompetenceFilterDTO(
        List<CompetenceFilterRequestDTO> competences,
        boolean matchEveryCompetence,
        UUID groupId,
        String groupPath
) {
}
