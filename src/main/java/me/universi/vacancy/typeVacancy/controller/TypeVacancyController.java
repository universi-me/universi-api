package me.universi.vacancy.typeVacancy.controller;

import me.universi.vacancy.typeVacancy.entities.TypeVacancy;
import me.universi.vacancy.typeVacancy.service.TypeVacancyService;
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
@RequestMapping(value = "/api/typeVacancy")
public class TypeVacancyController {

    private TypeVacancyService typeVacancyService;

    public TypeVacancyController(TypeVacancyService typeVacancyService){
        this.typeVacancyService = typeVacancyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TypeVacancy save(@RequestBody TypeVacancy typeVacancy) throws Exception{
        return  typeVacancyService.save(typeVacancy);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TypeVacancy> getAll() throws Exception{
        return typeVacancyService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<TypeVacancy> getTypeVacancy(@PathVariable UUID id){
        return typeVacancyService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TypeVacancy update(@RequestBody TypeVacancy newTypeVacancy, @PathVariable UUID id) throws Exception {
        return typeVacancyService.update(newTypeVacancy, id);
    }

}
