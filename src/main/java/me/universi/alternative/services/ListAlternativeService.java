package me.universi.alternative.services;



import me.universi.alternative.entities.Alternative;

import java.util.List;

@FunctionalInterface
public interface ListAlternativeService {
    List<Alternative> listAlternative(Long groupId, Long exerciseId, Long questionId);
}
