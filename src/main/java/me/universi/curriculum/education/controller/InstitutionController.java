package me.universi.curriculum.education.controller;

import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.servicies.InstitutionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/curriculum/institution")
public class InstitutionController {

    private InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService){
        this.institutionService = institutionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Institution save(@RequestBody Institution institution) throws Exception{
        return  institutionService.save(institution);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Institution> getAll() throws Exception{
        return institutionService.findAll();
    }

}
