package me.universi.subject;

import jakarta.validation.Valid;
import me.universi.subject.entities.Subject;
import me.universi.subject.services.CreateSubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user/{userId}/subject")
public class SubjectController {

    public CreateSubjectService createSubjectService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Subject createSubject(@Valid @RequestBody Subject subject, @PathVariable Long userId){
       return this.createSubjectService.createSubject(subject);
    }

}
