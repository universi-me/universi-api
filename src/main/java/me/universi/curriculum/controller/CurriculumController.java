package me.universi.curriculum.controller;

import me.universi.competence.entities.Competence;
import me.universi.curriculum.services.CurriculumService;
import me.universi.profile.entities.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum")
public class CurriculumController {

    public CurriculumService curriculumService;


    public CurriculumController(CurriculumService curriculumService){
        this.curriculumService = curriculumService;
    }

    @GetMapping(value = "/user")
    public List<List> curriculumProfile(){
        return curriculumService.mountCurriculum();
    }

    @GetMapping(value = "/search")
    public List<Profile> searchProfileByEducation(@RequestParam(name = "id", required = false)UUID id){
        return curriculumService.getProfileByEducation(id);
    }

}
