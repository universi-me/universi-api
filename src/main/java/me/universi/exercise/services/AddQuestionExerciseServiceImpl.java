package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.exception.ExerciseNotFoundException;
import me.universi.question.QuestionRepository;
import me.universi.question.entities.Question;
import me.universi.question.exceptions.QuestionNotfoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddQuestionExerciseServiceImpl implements AddQuestionExerciseService{

    private final ExerciseRepository exerciseRepository;

    private final QuestionRepository questionRepository;

    @Autowired
    public AddQuestionExerciseServiceImpl(ExerciseRepository exerciseRepository, QuestionRepository questionRepository) {
        this.exerciseRepository = exerciseRepository;
        this.questionRepository = questionRepository;
    }

    public void addQuestion(Long exerciseId, Question question){
        Exercise exercise = this.exerciseRepository.findById(exerciseId).orElseThrow(ExerciseNotFoundException::new);

        exercise.getQuestions().add(this.questionRepository.findById(question.getId()).orElseThrow(QuestionNotfoundException::new));
    }
}
