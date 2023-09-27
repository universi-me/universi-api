package me.universi.curriculum.controller;

import me.universi.competence.entities.Competence;
import me.universi.curriculum.services.CurriculumService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/curriculum")
public class CurriculumController {

    public CurriculumService curriculumService;


    public CurriculumController(CurriculumService curriculumService){
        this.curriculumService = curriculumService;
    }


    @GetMapping(value = "/user")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Competence> getCurriculoUser(@RequestBody String competenceTypeName) throws Exception{
        return curriculumService.findCompetenceUserByType(competenceTypeName);
    }


}
