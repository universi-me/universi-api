package me.universi.role.openapi;

import java.math.BigDecimal;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;

@Configuration
public class FeatureToLevelSchema {
    public static final String SCHEMA_NAME = "FeatureToLevel";
    public static final String REF_STRING = "#/components/schemas/" + SCHEMA_NAME;

    @Bean
    OpenApiCustomizer addFeatureToLevelSchema() {
        return openApi -> {
            var schema = new Schema<>()
                .description( "Maps a Feature into it's corresponding Level on a Role" );

            for ( var feature : FeaturesTypes.values() ) {
                schema.addProperty(
                    feature.name(),
                    new IntegerSchema()
                        .minimum( BigDecimal.valueOf( Permission.NONE ) )
                        .maximum( BigDecimal.valueOf( Permission.READ_WRITE_DELETE ) )
                );
            }

            openApi.schema( SCHEMA_NAME, schema );
        };
    }
}
