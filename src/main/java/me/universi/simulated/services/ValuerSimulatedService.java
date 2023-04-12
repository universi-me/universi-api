package me.universi.simulated.services;


import me.universi.simulated.dto.AnswerDTO;
import me.universi.simulated.dto.SimulatedAnswersDTO;

import java.util.List;

@FunctionalInterface
public interface ValuerSimulatedService {
    SimulatedAnswersDTO simulatedAnswers(Long userId, List<AnswerDTO> answers);
}
