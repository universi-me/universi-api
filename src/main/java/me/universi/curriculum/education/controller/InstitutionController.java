package me.universi.curriculum.education.controller;

import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.servicies.InstitutionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Institution> getInstitution(@PathVariable UUID id){
        return institutionService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Institution update(@RequestBody Institution newTypeEducation, @PathVariable UUID id) throws Exception {
        return institutionService.update(newTypeEducation, id);
    }

}
