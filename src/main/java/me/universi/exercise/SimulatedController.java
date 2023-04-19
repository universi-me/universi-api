package me.universi.exercise;

import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.QuestionWithAlternativesDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;
import me.universi.exercise.services.GetExerciseService;
import me.universi.exercise.services.ValuerExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/user/{userId}/simulated")
public class SimulatedController {

    public GetExerciseService getExerciseService;
    public ValuerExerciseService valuerExerciseService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionWithAlternativesDTO> listQuestionsWithAlternatives(@PathVariable Long userId, @RequestParam int amount){
        return getExerciseService.getQuestionsWithAlternatives(amount);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExerciseAnswersDTO calculateSimulated(@PathVariable Long userId, @Valid @RequestBody List<AnswerDTO> answers){
        return valuerExerciseService.simulatedAnswers(userId, answers);
    }
}