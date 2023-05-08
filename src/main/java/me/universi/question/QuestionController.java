package me.universi.question;

import jakarta.validation.Valid;
import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.entities.Question;
import me.universi.question.services.QuestionCreateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/group/{groupId}/exercise/{exerciseId}/question")
public class QuestionController {
    private final QuestionCreateService questionCreateService;

    public QuestionController(QuestionCreateService questionCreateService) {
        this.questionCreateService = questionCreateService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Question createQuestion(@PathVariable Long groupId, @PathVariable Long exerciseId,@Valid @RequestBody QuestionCreateDTO questionCreateDTO){
        return this.questionCreateService.createQuestion(groupId,exerciseId,questionCreateDTO);
    }
}
