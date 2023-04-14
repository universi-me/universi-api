package me.universi.alternative;

import jakarta.validation.Valid;
import me.universi.alternative.dto.AlternativeCreateDTO;
import me.universi.alternative.entities.Alternative;
import me.universi.alternative.services.CreateAlternativeService;
import me.universi.alternative.services.GetAlternativeService;
import me.universi.alternative.services.ListAlternativeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/user/{userId}/question/{questionId}/alternative/")
public class AlternativeController {
    private CreateAlternativeService createAlternativeService;
    private GetAlternativeService getAlternativeService;
    private ListAlternativeService listAlternativeService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Alternative createAlternative(@Valid @RequestBody AlternativeCreateDTO alternativeCreateDTO, @PathVariable Long userId, @PathVariable Long questionId){
        return createAlternativeService.createAlternative(userId, questionId, alternativeCreateDTO);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping(value = "{alternativeId}")
    public Alternative getAlternative(@PathVariable Long userId, @PathVariable Long questionId, @PathVariable Long alternativeId){

        return getAlternativeService.getAlternative(userId, questionId, alternativeId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Alternative> listAlternative (@PathVariable Long userId, @PathVariable Long questionId){

        return listAlternativeService.listAlternative(userId, questionId);
    }
}