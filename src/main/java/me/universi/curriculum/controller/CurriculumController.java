package me.universi.curriculum.controller;

import me.universi.curriculum.entities.Curriculum;
import me.universi.curriculum.services.CurriculumService;
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
@RequestMapping(value = "/api/curriculum")
public class CurriculumController {

    public CurriculumService curriculumService;


    public CurriculumController(CurriculumService curriculumService){
        this.curriculumService = curriculumService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Curriculum createCurriculum(@RequestBody Curriculum newCurriculum) throws Exception{
        return  curriculumService.save(newCurriculum);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Curriculum> getAllCurriculum() throws Exception{
        return curriculumService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Curriculum> getCurriculum(@PathVariable Long id){
        return curriculumService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Curriculum update(@RequestBody Curriculum newCurriculum, @PathVariable Long id) throws Exception {
        return curriculumService.update(newCurriculum, id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id){
        curriculumService.delete(id);
    }
}
