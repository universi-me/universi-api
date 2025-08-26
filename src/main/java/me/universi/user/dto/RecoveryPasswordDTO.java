package me.universi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for requesting a password recovery" )
public record RecoveryPasswordDTO(
        @Nullable
        @Schema( description = "ReCaptcha token, required for some organizations" )
        String recaptchaToken,

        @NotBlank
        @Schema( description = "Account's username or email" )
        String username
) {
}
