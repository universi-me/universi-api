package me.universi.simulated;

import me.universi.simulated.dto.AnswerDTO;
import me.universi.simulated.dto.QuestionWithAlternativesDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    public final GetSimulatedService getSimulatedService;
    public final ValuerSimulatedService valuerSimulatedService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionWithAlternativesDTO> listQuestionsWithAlternatives(@PathVariable Long userId, @RequestParam int amount){
        return getSimulatedService.getQuestionsWithAlternatives(amount);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SimulatedAnswersDTO calculateSimulated(@PathVariable Long userId,@Valid @RequestBody List<AnswerDTO> answers){
        return valuerSimulatedService.simulatedAnswers(userId, answers);
    }
}
