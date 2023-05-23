package me.universi.question;

import jakarta.validation.Valid;
import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.dto.QuestionUpdateDTO;
import me.universi.question.entities.Question;
import me.universi.question.services.DeleteQuestionService;
import me.universi.question.services.GetQuestionService;
import me.universi.question.services.ListQuestionService;
import me.universi.question.services.QuestionCreateService;
import me.universi.question.services.UpdateQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/group/{groupId}/exercise/{exerciseId}/question")
public class QuestionController {
    private final QuestionCreateService questionCreateService;
    private final UpdateQuestionService updateQuestionService;
    private final GetQuestionService getQuestionService;
    private final DeleteQuestionService deleteQuestionService;
    private final ListQuestionService listQuestionService;

    public QuestionController(QuestionCreateService questionCreateService, UpdateQuestionService updateQuestionService, GetQuestionService getQuestionService, DeleteQuestionService deleteQuestionService, ListQuestionService listQuestionService) {
        this.questionCreateService = questionCreateService;
        this.updateQuestionService = updateQuestionService;
        this.getQuestionService = getQuestionService;
        this.deleteQuestionService = deleteQuestionService;
        this.listQuestionService = listQuestionService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Question createQuestion(@PathVariable Long groupId, @PathVariable Long exerciseId,@Valid @RequestBody QuestionCreateDTO questionCreateDTO){
        return this.questionCreateService.createQuestion(groupId,exerciseId,questionCreateDTO);
    }

    @PutMapping(value = "/{idQuestion}")
    @ResponseStatus(value = HttpStatus.OK)
    public Question updateQuestion(@PathVariable Long groupId,
                                   @PathVariable Long exerciseId,
                                   @PathVariable Long idQuestion,
                                   @RequestBody @Valid QuestionUpdateDTO questionUpdateDTO){

        return this.updateQuestionService.updateQuestion(exerciseId, groupId, idQuestion, questionUpdateDTO);

    }

    @GetMapping(value = "/{questionId}")
    @ResponseStatus(code = HttpStatus.FOUND)
    public Question getQuestion(@PathVariable Long groupId,
                                @PathVariable Long exerciseId,
                                @PathVariable Long questionId){
        return this.getQuestionService.getQuestion(groupId, exerciseId, questionId);
    }

    @DeleteMapping(value = "/{questionId}")
    @ResponseStatus(code = HttpStatus.GONE)
    public void deleteQuestion(@PathVariable Long groupId, @PathVariable Long exerciseId, @PathVariable Long questionId){
        this.deleteQuestionService.deleteQuestion(groupId, exerciseId, questionId);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.FOUND)
    public List<Question> listQuestion(@PathVariable Long groupId, @PathVariable Long exerciseId){
        return this.listQuestionService.listQuestion(groupId, exerciseId);
    }
}
