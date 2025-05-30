package me.universi.activity.dto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;

public record UpdateActivityDTO(
    Optional<String> name,
    Optional<String> description,
    Optional<String> type,
    Optional<String> location,
    Optional<Integer> workload,
    Optional<List<@NotBlank String>> badges,

    @JsonAlias( { "start" } )
    Optional<Date> startDate,

    @JsonAlias( { "end" } )
    Optional<Date> endDate
) {}
