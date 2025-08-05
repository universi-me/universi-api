package me.universi.user.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EditAccountDTO(
        @NotNull
        UUID userId,
        String username,
        String email,
        String password,
        String authorityLevel,
        Boolean emailVerified,
        Boolean blockedAccount,
        Boolean inactiveAccount,
        Boolean credentialsExpired,
        Boolean expiredUser,
        Boolean temporarilyPassword
) {
}
