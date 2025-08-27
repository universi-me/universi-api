package me.universi.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Schema( description = "Default error response" )
public record ApiError(

        @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp,

        @JsonSerialize(using = StatusCodeToValueParse.class)
        @Schema( implementation = StatusCodeSchema.class )
        HttpStatus status,

        @Schema(
            example = "[\"Human-readable message describing the error\"]",
            description = "Contains human-readable message of which error(s) occurred, *most* times with only 1 item"
        )
        List<String> errors

) {
        // created Builder class
        public static class Builder {
                private LocalDateTime timestamp;
                private HttpStatus status;
                private List<String> errors;

                public Builder timestamp(LocalDateTime timestamp) {
                        this.timestamp = timestamp;
                        return this;
                }

                public Builder status(HttpStatus status) {
                        this.status = status;
                        return this;
                }

                public Builder errors(List<String> errors) {
                        this.errors = errors;
                        return this;
                }

                public ApiError build() {
                        return new ApiError(timestamp, status, errors);
                }
        }

        // Static method to create a builder instance
        public static Builder builder() {
                return new Builder();
        }

        public ResponseEntity<ApiError> toResponseEntity() {
            return new ResponseEntity<>( this, this.status );
        }

        public static class StatusCodeToValueParse extends JsonSerializer<HttpStatus> {
                @Override
                public void serialize(HttpStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        gen.writeStartObject();
                        gen.writeNumberField("code", value.value());
                        gen.writeStringField("description", value.name());
                        gen.writeEndObject();
                }
        }

        private record StatusCodeSchema(
            @Schema(
                example = "404",
                description = "Will always be the same as the HTTP response code"
            )
            int code,

            @Schema(
                example = "NOT_FOUND",
                description = "A descriptive name for the HTTP status code error. Will always be one of the one the names for the Spring HttpStatus enum"
            )
            String description
        ) { }
}
