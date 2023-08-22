package me.universi.alternative.services;


import java.util.UUID;

@FunctionalInterface
public interface DeleteAlternativeService {
    void deleteAlternative(UUID groupId, UUID exerciseId, UUID questionId, UUID alternativeId);
}
