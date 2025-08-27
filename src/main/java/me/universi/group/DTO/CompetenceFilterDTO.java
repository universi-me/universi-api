package me.universi.group.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema( description = "Request body for filtering from a Group's participants" )
public record CompetenceFilterDTO(
        @Schema( description = "List of Competence filters a Profile must match" )
        List<CompetenceFilterRequestDTO> competences,
        @Schema( description = "If true, a Profile must match all filters on the `competence` parameter. Otherwise, they must match at least one instead" )
        boolean matchEveryCompetence,
        @Schema( description = "Group's ID or path" )
        @JsonAlias({ "groupId", "groupPath" })
        String group
) {
}
