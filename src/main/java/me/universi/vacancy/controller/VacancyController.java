package me.universi.vacancy.controller;

import me.universi.competence.entities.Competence;
import me.universi.vacancy.entities.Vacancy;
import me.universi.vacancy.services.VacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping(value = "/api/vacancy")
public class VacancyController {

    public VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService){
        this.vacancyService = vacancyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vacancy createVacancy(@RequestBody Vacancy newVacancy) throws Exception{
        return vacancyService.save(newVacancy);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Vacancy> getAllVacancy() throws Exception{
        return vacancyService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Vacancy getVacancy(@PathVariable UUID id){
        return vacancyService.findFirstById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Vacancy update(@RequestBody Vacancy newVacancy, @PathVariable UUID id) throws Exception {
        return vacancyService.update(newVacancy, id);
    }

    @PutMapping(value = "/addCompetencesInVacancy/{id}")
    public Vacancy addCompetencesInVacancy(@PathVariable UUID id,@RequestBody Competence newCompetences) throws Exception{
        return vacancyService.addCompetenceInVacancy(id, newCompetences);
    }

    /*Emplementar delete logico, nao o hard delete no banco*/
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable UUID id){
        vacancyService.delete(id);
    }

}
