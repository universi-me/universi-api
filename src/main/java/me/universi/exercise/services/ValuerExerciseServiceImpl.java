package me.universi.exercise.services;

import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;
import me.universi.indicators.IndicatorsRepository;
import me.universi.indicators.entities.Indicators;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserNotFoundException;
import me.universi.user.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ValuerExerciseServiceImpl implements ValuerExerciseService {

    private final IndicatorsRepository indicatorsRepository;
    private final UsuarioRepository userRepository;

    @Autowired
    public ValuerExerciseServiceImpl(IndicatorsRepository indicatorsRepository, UsuarioRepository userRepository) {
        this.indicatorsRepository = indicatorsRepository;
        this.userRepository = userRepository;
    }

    //Responsável por receber as respostas e verificar se estão corretas
    @Override
    public ExerciseAnswersDTO simulatedAnswers(Long userId, List<AnswerDTO> answers) {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Indicators indicators = this.indicatorsRepository.findByUserId(userId);

        long score = 0;

        List<Long> ids = new ArrayList<>();
        ExerciseAnswersDTO simulatedAnswersDTO = new ExerciseAnswersDTO();
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
    }



}
