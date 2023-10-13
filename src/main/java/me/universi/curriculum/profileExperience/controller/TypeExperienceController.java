package me.universi.curriculum.profileExperience.controller;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.curriculum.education.exceptions.InstitutionException;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.profileExperience.entities.TypeExperience;
import me.universi.curriculum.profileExperience.repositories.TypeExperienceRepository;
import me.universi.curriculum.profileExperience.servicies.TypeExperienceService;
import me.universi.user.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum/typeExperience")
public class TypeExperienceController {

    private TypeExperienceService typeExperienceService;

    public TypeExperienceController(TypeExperienceService typeExperienceService){
        this.typeExperienceService = typeExperienceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TypeExperience save(@RequestBody TypeExperience typeExperience) throws Exception{
        return  typeExperienceService.save(typeExperience);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TypeExperience> getAllCurriculum() throws Exception{
        return typeExperienceService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<TypeExperience> getTypeExperience(@PathVariable UUID id){
        return typeExperienceService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TypeExperience update(@RequestBody TypeExperience newTypeExperience, @PathVariable UUID id) throws Exception {
        return typeExperienceService.update(newTypeExperience, id);
    }
}
