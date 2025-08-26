package me.universi.user.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Request body for updating another User data" )
public record EditAccountDTO(
        @NotNull
        @Schema( description = "ID of the updated User" )
        UUID userId,
        @Schema( description = "New username" )
        String username,
        @Schema( description = "New email" )
        String email,
        @Schema( description = "New password" )
        String password,
        @Schema( description = "New platform-wide authority level" )
        String authorityLevel,
        @Schema( description = "If true, will skip user email verification. If false, will require another email verification" )
        Boolean emailVerified,
        @Schema( description = "Blocks or unblocks user access to the platform" )
        Boolean blockedAccount,
        @Schema( description = "Marks this account as inactive" )
        Boolean inactiveAccount,
        @Schema( description = "Marks this User's credentials as expired" )
        Boolean credentialsExpired,
        @Schema( description = "Marks this User as expired" )
        Boolean expiredUser,
        @Schema( description = "If true, will require the user to update their password on their next login" )
        Boolean temporarilyPassword
) {
}
