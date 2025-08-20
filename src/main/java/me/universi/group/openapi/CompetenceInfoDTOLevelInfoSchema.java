package me.universi.group.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import me.universi.competence.entities.Competence;

@Configuration
public class CompetenceInfoDTOLevelInfoSchema {
    public static final String SCHEMA_NAME = "CompetenceInfoDTOLevelInfo";
    public static final String REF_STRING = "#/components/schemas/" + SCHEMA_NAME;

    @Bean
    OpenApiCustomizer addCompetenceInfoDTOLevelInfoSchema() {
        return openApi -> {
            var schema = new Schema<>()
                .description( "Maps from a Competence level to all participants that have a Competence with this CompetenceType with this level" );

            for ( var i = Competence.MIN_LEVEL; i <= Competence.MAX_LEVEL; i++ ) {
                schema.addProperty(
                    String.valueOf( i ),
                    new ArraySchema().items( new Schema<>().$ref( "#/components/schemas/Profile" ) )
                );
            }

            openApi.schema(
                SCHEMA_NAME,
                schema
            );
        };
    }
}
