package me.universi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.user.entities.User;

@Schema( description = "Response body for signing in on the platform" )
public record LoginResponseDTO(
        @NotNull
        @Schema( description = "Your user" )
        User user,
        @NotBlank
        @Schema( description = "Your JWT bearer token" )
        String token
) {
}
