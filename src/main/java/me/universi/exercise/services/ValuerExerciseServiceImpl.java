package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.exception.ExerciseNotFoundException;
import me.universi.indicators.IndicatorsRepository;
import me.universi.indicators.entities.Indicators;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ValuerExerciseServiceImpl implements ValuerExerciseService {

    private final IndicatorsRepository indicatorsRepository;
    private final UserService userService;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ValuerExerciseServiceImpl(IndicatorsRepository indicatorsRepository, UserService userService, ExerciseRepository exerciseRepository) {
        this.indicatorsRepository = indicatorsRepository;
        this.userService = userService;
        this.exerciseRepository = exerciseRepository;
    }

    //Responsável por receber as respostas e verificar se estão corretas
    @Override
    public ExerciseAnswersDTO exercisesAnswers(UUID groupId, UUID exerciseid, List<AnswerDTO> answers) {
        User user = this.userService.getUserInSession();
        Exercise exercise = this.exerciseRepository.findFirstByIdAndGroupId(exerciseid, groupId).orElseThrow(ExerciseNotFoundException::new);
        Indicators indicators = this.indicatorsRepository.findByProfileId(user.getId());

        long score = 0;

        List<UUID> ids = new ArrayList<>();
        ExerciseAnswersDTO simulatedAnswersDTO = new ExerciseAnswersDTO();
        simulatedAnswersDTO.setAnswers(answers);
        for (AnswerDTO answerDTO : answers){
            ids.add(answerDTO.getQuestion().getId());

        }
        indicators.setScore(indicators.getScore() + score);
        indicatorsRepository.save(indicators);
        simulatedAnswersDTO.setScore(score);

        return simulatedAnswersDTO;
    }



}
