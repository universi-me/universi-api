package me.universi.profile.dto;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.annotation.Nonnull;
import me.universi.profile.enums.Gender;

public record UpdateProfileDTO(
    @JsonAlias( { "firstname", "name" } )
    Optional<String> firstname,

    Optional<String> lastname,

    Optional<UUID> image,

    @JsonAlias( { "bio", "biography", "description" } )
    Optional<String> biography,

    Optional<Gender> gender,

    @Nonnull
    @JsonAlias( { "password", "rawPassword", "raw_password" } )
    String password
) {}
