package me.universi.simulated.services;

import me.universi.simulated.dto.AnswerDTO;
import me.universi.simulated.dto.SimulatedAnswersDTO;
import me.universi.usuario.entities.User;
import me.universi.usuario.exceptions.UserNotFoundException;
import me.universi.usuario.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ValuerSimulatedServiceImpl implements ValuerSimulatedService{

  /*  private final IndicatorsRepository indicatorsRepository;
    private final UsuarioRepository userRepository;

    @Autowired
    public ValuerSimulatedServiceImpl(IndicatorsRepository indicatorsRepository, UsuarioRepository userRepository) {
        this.indicatorsRepository = indicatorsRepository;
        this.userRepository = userRepository;
    }

    //Responsável por receber as respostas e verificar se estão corretas
    @Override
    public SimulatedAnswersDTO simulatedAnswers(Long userId, List<AnswerDTO> answers) {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Indicators indicators = this.indicatorsRepository.findByUserId(userId);

        long score = 0;

        List<Long> ids = new ArrayList<>();
        SimulatedAnswersDTO simulatedAnswersDTO = new SimulatedAnswersDTO();
        simulatedAnswersDTO.setAnswers(answers);
        for (AnswerDTO answerDTO : answers){
            ids.add(answerDTO.getQuestion().getId());

            if (answerDTO.getAlternativeSelected().getCorrect()){
                score+=10;
            }
        }
        indicators.setScore(indicators.getScore() + score);
        indicatorsRepository.save(indicators);
        simulatedAnswersDTO.setScore(score);

        return simulatedAnswersDTO;
    }*/



}
