package me.universi.vacancy.controller;

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
    public Optional<Vacancy> getVacancy(@PathVariable Long id){
        return vacancyService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Vacancy update(@RequestBody Vacancy newCurriculum, @PathVariable Long id) throws Exception {
        return vacancyService.update(newCurriculum, id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id){
        vacancyService.delete(id);
    }

}
