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
@RequestMapping(value = "/api/group/{groupId}/exercise/{exerciseId}/question/{questionId}/alternative")
public class AlternativeController {
    private final CreateAlternativeService createAlternativeService;
    private final GetAlternativeService getAlternativeService;
    private final ListAlternativeService listAlternativeService;

    public AlternativeController(CreateAlternativeService createAlternativeService, GetAlternativeService getAlternativeService, ListAlternativeService listAlternativeService) {
        this.createAlternativeService = createAlternativeService;
        this.getAlternativeService = getAlternativeService;
        this.listAlternativeService = listAlternativeService;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Alternative createAlternative(@Valid @RequestBody AlternativeCreateDTO alternativeCreateDTO, @PathVariable Long groupId, @PathVariable Long exerciseId, @PathVariable Long questionId){
        return createAlternativeService.createAlternative(groupId, exerciseId, questionId, alternativeCreateDTO);
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
