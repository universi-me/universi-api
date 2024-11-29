package me.universi.alternative.services;



import me.universi.alternative.entities.Alternative;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface ListAlternativeService {
    List<Alternative> listAlternative(UUID groupId, UUID exerciseId, UUID questionId);
}
