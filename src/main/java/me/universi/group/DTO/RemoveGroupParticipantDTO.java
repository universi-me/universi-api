package me.universi.group.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;

public record RemoveGroupParticipantDTO(
        @NotBlank
        @JsonAlias( { "groupId", "groupPath" } )
        String group,

        @NotBlank
        String participant
) { }
