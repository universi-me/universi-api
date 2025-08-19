package me.universi.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public @interface SwaggerAnnotationUtils {

    public @interface ApiResponses {
        @Inherited
        @Target( { ElementType.METHOD, ElementType.ANNOTATION_TYPE } )
        @Retention( RetentionPolicy.RUNTIME )
        @ApiResponse(
            responseCode = "302",
            description = "The image was found and its URL is at the Location header",
            headers = {
                @Header(
                    name = "Location",
                    description = "The URL for the image",
                    example = "/api/img/minio/5c2d12db-bb94-4ff7-8900-1a2485d2258d"
                )
            },
            content = @Content()
        )
        @ApiResponse(
            responseCode = "404",
            description = "The image could not be found",
            content = @Content()
        )
        public @interface ImageRedirect {}
    }
}
