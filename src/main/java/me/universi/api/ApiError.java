package me.universi.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ApiError (

        @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp,

        @JsonSerialize(using = StatusCodeToValueParse.class)
        HttpStatus status,

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
}
