package me.universi.activity.dto;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateActivityDTO(
    @NotBlank String name,
    @NotBlank String groupType,
    @NotBlank String description,
    @NotBlank String type,
    @NotBlank String location,
    @NotNull Integer workload,
    Optional<List<@NotBlank String>> badges,

    Optional<UUID> image,
    Optional<UUID> bannerImage,

    @JsonAlias( { "groupId", "groupPath" } )
    @NotBlank String group,

    @JsonAlias( { "start" } )
    @NotNull Date startDate,

    @JsonAlias( { "end" } )
    @NotNull Date endDate
) {}
