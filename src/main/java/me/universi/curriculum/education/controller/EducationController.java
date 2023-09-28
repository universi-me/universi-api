package me.universi.curriculum.education.controller;


import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.servicies.EducationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum/education")
public class EducationController {

    private EducationService educationService;

    public EducationController(EducationService educationService){
        this.educationService = educationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Education createEducation(@RequestBody Education education){
        return educationService.save(education);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Education> getAllEducation(){
        return educationService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Education> getEducation(@PathVariable UUID id){
        return educationService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Education update(@RequestBody Education newEducation, @PathVariable UUID id) throws Exception {
        return educationService.update(newEducation, id);
    }
}
