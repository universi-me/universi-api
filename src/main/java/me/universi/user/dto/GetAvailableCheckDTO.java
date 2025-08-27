package me.universi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Response body for checking username or email availability" )
public record GetAvailableCheckDTO(
    boolean available,

    @NotBlank
    @Schema( description = "Human-readable text explaining why the username or email is not available, or reinforcing if it is" )
    String reason
) {
}
