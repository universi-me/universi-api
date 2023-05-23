package me.universi.exercise;

import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.dto.ExerciseUpdateDTO;
import me.universi.exercise.dto.QuestionWithAlternativesDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.services.CreateExerciseService;
import me.universi.exercise.services.DeleteExerciseService;
import me.universi.exercise.services.GetExerciseService;
import me.universi.exercise.services.ListExerciseService;
import me.universi.exercise.services.ListQuestionsWithAlternativesService;
import me.universi.exercise.services.UpdateExerciseService;
import me.universi.exercise.services.ValuerExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final GetExerciseService getExerciseService;
    private final UpdateExerciseService updateExerciseService;
    private final DeleteExerciseService deleteExerciseService;
    private final ListExerciseService listExerciseService;

    public ExerciseController(ListQuestionsWithAlternativesService listQuestionsWithAlternativesService, ValuerExerciseService valuerExerciseService, CreateExerciseService createExerciseService, GetExerciseService getExerciseService, UpdateExerciseService updateExerciseService, DeleteExerciseService deleteExerciseService, ListExerciseService listExerciseService) {
        this.listQuestionsWithAlternativesService = listQuestionsWithAlternativesService;
        this.valuerExerciseService = valuerExerciseService;
        this.createExerciseService = createExerciseService;
        this.getExerciseService = getExerciseService;
        this.updateExerciseService = updateExerciseService;
        this.deleteExerciseService = deleteExerciseService;
        this.listExerciseService = listExerciseService;
    }

    @GetMapping(value = "/{exerciseId}/questions")
    @ResponseStatus(HttpStatus.FOUND)
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

    @GetMapping(value = "/{exerciseId}")
    @ResponseStatus(value = HttpStatus.FOUND)
    public Exercise getExercise(@PathVariable Long groupId,
                                @PathVariable Long exerciseId){
        return this.getExerciseService.getExercise(groupId, exerciseId);
    }

    @PutMapping(value = "/{exerciseId}")
    @ResponseStatus(value = HttpStatus.OK)
    public Exercise updateExercise(@PathVariable Long groupId,
                                   @PathVariable Long exerciseId, @RequestBody @Valid ExerciseUpdateDTO exerciseUpdateDTO){
        return this.updateExerciseService.updateExercise(groupId, exerciseId, exerciseUpdateDTO);
    }

    @DeleteMapping(value = "/{exerciseId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteExercise(@PathVariable Long groupId,
                               @PathVariable Long exerciseId){
        this.deleteExerciseService.deleteExercise(groupId, exerciseId);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.FOUND)
    public List<Exercise> listExercises(@PathVariable Long groupId){
        return this.listExerciseService.listExercise(groupId);
    }
}
