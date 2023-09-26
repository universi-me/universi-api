package me.universi.curriculum.profileExperience.controller;

import me.universi.curriculum.profileExperience.entities.TypeExperience;
import me.universi.curriculum.profileExperience.repositories.TypeExperienceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/curriculum/typeExperience")
public class TypeExperienceController {

    private TypeExperienceRepository typeExperienceRepository;

    public TypeExperienceController(TypeExperienceRepository typeExperienceRepository){
        this.typeExperienceRepository = typeExperienceRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TypeExperience save(@RequestBody TypeExperience typeExperience) throws Exception{
        return  typeExperienceRepository.save(typeExperience);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TypeExperience> getAllCurriculum() throws Exception{
        return typeExperienceRepository.findAll();
    }
}
