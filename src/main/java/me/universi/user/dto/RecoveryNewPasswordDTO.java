package me.universi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for recovering an account's password" )
public record RecoveryNewPasswordDTO(
        @NotBlank
        @Schema( description = "Recovery token sent through email" )
        String token,
        @NotBlank
        @Schema( description = "New password" )
        String newPassword
) {
}
