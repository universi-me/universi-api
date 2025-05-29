package me.universi.activity.dto;

import java.util.List;
import java.util.Optional;

public record ChangeActivityParticipantsDTO(
    Optional<List<String>> add,
    Optional<List<String>> remove
) {}
