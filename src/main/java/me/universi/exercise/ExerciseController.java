package me.universi.exercise;

import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.dto.QuestionWithAlternativesDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.services.CreateExerciseService;
import me.universi.exercise.services.ListQuestionsWithAlternativesService;
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
@RequestMapping(value = "/api/group/{groupId}/exercise")
public class ExerciseController {

    public final ListQuestionsWithAlternativesService listQuestionsWithAlternativesService;
    public final ValuerExerciseService valuerExerciseService;

    private final CreateExerciseService createExerciseService;

    public ExerciseController(ListQuestionsWithAlternativesService listQuestionsWithAlternativesService, ValuerExerciseService valuerExerciseService, CreateExerciseService createExerciseService) {
        this.listQuestionsWithAlternativesService = listQuestionsWithAlternativesService;
        this.valuerExerciseService = valuerExerciseService;
        this.createExerciseService = createExerciseService;
    }

    @GetMapping(value = "/{exerciseId}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionWithAlternativesDTO> listQuestionsWithAlternatives(@PathVariable Long groupId, @PathVariable Long exerciseId,  @RequestParam int amount){
        return listQuestionsWithAlternativesService.getQuestionsWithAlternatives(groupId, exerciseId, amount);
    }

    @PostMapping(value = "/{exerciseId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExerciseAnswersDTO calculateExercise(@PathVariable Long groupId, @PathVariable Long exerciseId, @Valid @RequestBody List<AnswerDTO> answers){
        return valuerExerciseService.exercisesAnswers(groupId, exerciseId, answers);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Exercise createExercise(@PathVariable Long groupId, @RequestBody  @Valid ExerciseCreateDTO exerciseCreateDTO){

        return  this.createExerciseService.createExercise(groupId, exerciseCreateDTO);
    }
}