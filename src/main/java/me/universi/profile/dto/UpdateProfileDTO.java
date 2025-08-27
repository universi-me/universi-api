package me.universi.profile.dto;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import me.universi.profile.enums.Gender;

@Schema( description = "Request body for updating your Profile" )
public record UpdateProfileDTO(
    @JsonAlias( { "firstname", "name" } )
    @Schema( description = "Your new firstname", example = "John" )
    Optional<String> firstname,

    @Schema( description = "Your new lastname", example = "Smith" )
    Optional<String> lastname,

    @Schema( description = "ImageMetadata ID used for your Profile image" )
    Optional<UUID> image,

    @JsonAlias( { "bio", "biography", "description" } )
    @Schema( description = "Short plain text with a description of yourself", example = "Working at Team Name on Department Name as a Function Name" )
    Optional<String> biography,

    @Schema( description = "Your gender, amongst available options" )
    Optional<Gender> gender,

    @JsonAlias( { "department_id", "departmentId" } )
    @Schema( description = "The Department you are a part of, by ID or acronym" )
    Optional<String> department,

    @Nonnull
    @JsonAlias( { "password", "rawPassword", "raw_password" } )
    @Schema( description = "Your password, to authorize the update" )
    String password
) {}
