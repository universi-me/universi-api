package me.universi.group.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CompetenceFilterDTO(
        List<CompetenceFilterRequestDTO> competences,
        boolean matchEveryCompetence,
        @JsonAlias({ "groupId", "groupPath" })
        String group
) {
}
