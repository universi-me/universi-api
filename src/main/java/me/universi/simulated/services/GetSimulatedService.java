package me.universi.simulated.services;


import me.universi.simulated.dto.QuestionWithAlternativesDTO;

import java.util.List;

@FunctionalInterface
public interface GetSimulatedService {
    List<QuestionWithAlternativesDTO> getQuestionsWithAlternatives(int amount);
}
