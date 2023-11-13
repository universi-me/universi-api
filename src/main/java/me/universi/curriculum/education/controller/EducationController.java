package me.universi.curriculum.education.controller;


import me.universi.api.entities.Response;
import me.universi.curriculum.education.servicies.EducationService;
import me.universi.curriculum.education.servicies.InstitutionService;
import me.universi.curriculum.education.servicies.TypeEducationService;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/curriculum/education")
public class EducationController {

    private EducationService educationService;
    private TypeEducationService typeEducationService;
    private InstitutionService institutionService;
    private UserService userService;

    public EducationController(EducationService educationService, TypeEducationService typeEducationService, InstitutionService institutionService, UserService userService){
        this.educationService = educationService;
        this.institutionService = institutionService;
        this.typeEducationService = typeEducationService;
        this.userService = userService;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        return educationService.update(body);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        return educationService.create(body);
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        return educationService.get(body);
    }

    @GetMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        return educationService.findAll(body);
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        return educationService.remove(body);
    }
}
