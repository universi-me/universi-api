package me.universi.group.DTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;

public record ChangeGroupParticipantsDTO(
    Optional<List<AddParticipantRecord>> add,
    Optional<List<String>> remove
) {
    public record AddParticipantRecord(
        @JsonAlias( { "profileId", "username" } )
        @NotBlank String profile,

        Optional<UUID> role
    ) {}
}
