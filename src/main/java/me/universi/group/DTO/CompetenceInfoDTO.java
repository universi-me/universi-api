package me.universi.group.DTO;

import me.universi.group.openapi.CompetenceInfoDTOLevelInfoSchema;
import me.universi.profile.entities.Profile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AdditionalPropertiesValue;



@Schema( description = "Response body for a Group's participants Competence statistics" )
public record CompetenceInfoDTO(
        @Schema( description = "The name of the CompetenceType", example = "Python" )
        String competenceName,
        @Schema( description = "The ID of the CompetenceType" )
        UUID competenceTypeId,
        @Schema( ref = CompetenceInfoDTOLevelInfoSchema.REF_STRING, additionalProperties = AdditionalPropertiesValue.FALSE )
        Map<Integer, List<Profile>> levelInfo

){}