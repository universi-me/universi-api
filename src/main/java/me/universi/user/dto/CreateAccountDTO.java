package me.universi.user.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for creating a new User on the platform" )
public record CreateAccountDTO(
        @Nullable
        @Schema( description = "ReCaptcha token, required for some organizations" )
        String recaptchaToken,

        @NotBlank
        @Schema( description = "Your unique username on the platform", example = "example.username" )
        String username,

        @NotBlank
        @Schema( description = "Your email address, used to receive updates and update your password. Some organizations might need to validate your address before allowing a signing in", example = "example@email.com" )
        String email,
        @NotBlank
        @Schema( description = "Your password, used to signing in on the platform" )
        String password,

        @NotBlank
        @Schema( description = "Your firstname", example = "John" )
        String firstname,

        @NotBlank
        @Schema( description = "Your lastname", example = "Smith" )
        String lastname,

        @JsonAlias( { "department_id", "departmentId" } )
        @Schema( description = "The department you are a part of, by ID or name", example = "EDN" )
        Optional<String> department
) {
}
