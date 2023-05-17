package me.universi.curriculum.controller;

import me.universi.curriculum.entities.Curriculum;
import me.universi.curriculum.services.CurriculumService;
import me.universi.profile.services.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

@Controller
public class CurriculumController {

    @Autowired
    public CurriculumService curriculumService;

    @Autowired
    public PerfilService perfilService;

    @PostMapping(value = "/curriculum", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Curriculum createCurriculum(@RequestBody Curriculum curriculum){
        return  curriculumService.save(curriculum);
    }

    @GetMapping(value = "/curriculum")
    @ResponseStatus(HttpStatus.OK)
    public List<Curriculum> getAllCurriculum(){
        return curriculumService.findAll();
    }

    @GetMapping(value = "/curriculum/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Curriculum> getCurriculum(@PathVariable Long id){
        return curriculumService.findById(id);
    }

}
