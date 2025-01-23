package me.universi.profile.dto;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.annotation.Nonnull;
import me.universi.profile.enums.Gender;

public record CreateProfileDTO(
    @Nonnull
    String username,

    @Nonnull
    @JsonAlias( { "firstname", "name" } )
    String firstname,

    @Nonnull
    String lastname,

    Optional<UUID> image,

    @JsonAlias( { "bio", "biography", "description" } )
    Optional<String> biography,

    Optional<Gender> gender
) {}
