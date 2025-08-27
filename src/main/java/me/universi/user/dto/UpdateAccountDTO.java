package me.universi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for updating your User data" )
public record UpdateAccountDTO(
        @NotBlank
        @Schema( description = "Your current password, to authorize the update" )
        String password,
        @NotBlank
        @Schema( description = "Your new password" )
        String newPassword
) {
}
