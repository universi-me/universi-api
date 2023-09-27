package me.universi.curriculum.education.controller;


import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.servicies.TypeEducationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/curriculum/TypeEducation")
public class TypeEducationController {

    private TypeEducationService typeEducationService;

    public TypeEducationController(TypeEducationService typeEducationService){
        this.typeEducationService = typeEducationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TypeEducation save(@RequestBody TypeEducation typeEducation) throws Exception{
        return typeEducationService.save(typeEducation);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TypeEducation> getAll() throws Exception{
        return typeEducationService.findAll();
    }

}
