package me.universi.experience.dto;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.annotation.Nullable;

@JsonDeserialize( using = UpdateExperienceDTOJsonDeserializer.class )
public record UpdateExperienceDTO(
    @Nullable
    String experienceType,

    @Nullable
    UUID institution,

    @Nullable
    String description,

    @Nullable
    Date startDate,

    @Nullable
    Date endDate,

    boolean removeEndDate
) {}

class UpdateExperienceDTOJsonDeserializer extends JsonDeserializer<UpdateExperienceDTO> {
    @Override
    public UpdateExperienceDTO deserialize( JsonParser parser, DeserializationContext context ) throws IOException {
        var tree = parser.readValueAs( JsonNode.class );

        var typeExperience = context.readTreeAsValue( tree.get( "typeExperience" ), String.class );
        var institution = context.readTreeAsValue( tree.get( "institution" ) , UUID.class );
        var description = context.readTreeAsValue( tree.get( "description" ), String.class );
        var startDate = context.readTreeAsValue( tree.get( "startDate" ) , Date.class );
        var endDate = context.readTreeAsValue( tree.get( "endDate" ) , Date.class );
        var removeEndDate = tree.has( "endDate" ) && endDate == null;

        return new UpdateExperienceDTO(
            typeExperience,
            institution,
            description,
            startDate,
            endDate,
            removeEndDate
        );
    }
}
