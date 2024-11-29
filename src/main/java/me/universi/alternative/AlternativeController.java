package me.universi.alternative;

import jakarta.validation.Valid;
import me.universi.alternative.dto.AlternativeCreateDTO;
import me.universi.alternative.dto.AlternativeUpdateDTO;
import me.universi.alternative.entities.Alternative;
import me.universi.alternative.services.CreateAlternativeService;
import me.universi.alternative.services.DeleteAlternativeService;
import me.universi.alternative.services.GetAlternativeService;
import me.universi.alternative.services.ListAlternativeService;
import me.universi.alternative.services.UpdateAlternativeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/group/{groupId}/exercise/{exerciseId}/question/{questionId}/alternative")
public class AlternativeController {
    private final CreateAlternativeService createAlternativeService;
    private final GetAlternativeService getAlternativeService;
    private final ListAlternativeService listAlternativeService;
    private final UpdateAlternativeService updateAlternativeService;
    private final DeleteAlternativeService deleteAlternativeService;

    public AlternativeController(CreateAlternativeService createAlternativeService, GetAlternativeService getAlternativeService, ListAlternativeService listAlternativeService, UpdateAlternativeService updateAlternativeService, DeleteAlternativeService deleteAlternativeService) {
        this.createAlternativeService = createAlternativeService;
        this.getAlternativeService = getAlternativeService;
        this.listAlternativeService = listAlternativeService;
        this.updateAlternativeService = updateAlternativeService;
        this.deleteAlternativeService = deleteAlternativeService;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Alternative createAlternative(@Valid @RequestBody AlternativeCreateDTO alternativeCreateDTO, @PathVariable UUID groupId, @PathVariable UUID exerciseId, @PathVariable UUID questionId){
        return createAlternativeService.createAlternative(groupId, exerciseId, questionId, alternativeCreateDTO);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping(value = "{alternativeId}")
    public Alternative getAlternative(
            @PathVariable UUID groupId,
            @PathVariable UUID exerciseId,
            @PathVariable UUID questionId,
            @PathVariable UUID alternativeId){

        return getAlternativeService.getAlternative(groupId, questionId, alternativeId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Alternative> listAlternative (
            @PathVariable UUID groupId,
            @PathVariable UUID exerciseId,
            @PathVariable UUID questionId){

        return listAlternativeService.listAlternative(groupId, exerciseId, questionId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(value = "/{alternativeId}")
    public Alternative updateAlternative(
            @PathVariable UUID groupId,
            @PathVariable UUID exerciseId,
            @PathVariable UUID questionId,
            @PathVariable UUID alternativeId,
            @RequestBody @Valid AlternativeUpdateDTO alternativeUpdateDTO
            ){
        return this.updateAlternativeService.updateAlternative(groupId, exerciseId, questionId, alternativeId, alternativeUpdateDTO);
    }

    @DeleteMapping(value = "/{alternativeId}")
    @ResponseStatus(code = HttpStatus.GONE)
    private void deleteAlternative(@PathVariable UUID groupId,
                                   @PathVariable UUID exerciseId,
                                   @PathVariable UUID questionId,
                                   @PathVariable UUID alternativeId){

        this.deleteAlternativeService.deleteAlternative(groupId, exerciseId, questionId, alternativeId);
    }
}
