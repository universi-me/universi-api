package me.universi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for Google or Keycloak login" )
public record LoginCodeDTO(
        @NotBlank
        @Schema( description = "Login token" )
        String code
) {
}
