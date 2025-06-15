package me.universi.education.dto;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.annotation.Nullable;

@JsonDeserialize( using = UpdateEducationDTOJsonDeserializer.class )
public record UpdateEducationDTO(
    @Nullable
    String educationType,

    @Nullable
    UUID institution,

    @Nullable
    Date startDate,

    @Nullable
    Date endDate,

    boolean removeEndDate
) {}

class UpdateEducationDTOJsonDeserializer extends JsonDeserializer<UpdateEducationDTO> {
    @Override
    public UpdateEducationDTO deserialize( JsonParser parser, DeserializationContext context ) throws IOException {
        var tree = parser.readValueAs( JsonNode.class );

        var educationType = context.readTreeAsValue( tree.get( "educationType" ), String.class );
        var institution = context.readTreeAsValue( tree.get( "institution" ) , UUID.class );
        var startDate = context.readTreeAsValue( tree.get( "startDate" ) , Date.class );
        var endDate = context.readTreeAsValue( tree.get( "endDate" ) , Date.class );
        var removeEndDate = tree.has( "endDate" ) && endDate == null;

        return new UpdateEducationDTO(
            educationType,
            institution,
            startDate,
            endDate,
            removeEndDate
        );
    }
}
