package me.universi.alternative.services;


import me.universi.alternative.entities.Alternative;

import java.util.UUID;

@FunctionalInterface
public interface GetAlternativeService {
    Alternative getAlternative(UUID groupId, UUID questionId, UUID alternativeId);
}
