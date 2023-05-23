package me.universi.alternative.services;


@FunctionalInterface
public interface DeleteAlternativeService {
    void deleteAlternative(Long groupId, Long exerciseId, Long questionId, Long alternativeId);
}
