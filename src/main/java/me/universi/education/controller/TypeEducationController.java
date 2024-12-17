package me.universi.education.controller;


import me.universi.api.entities.Response;
import me.universi.education.entities.TypeEducation;
import me.universi.education.exceptions.TypeEducationException;
import me.universi.education.servicies.TypeEducationService;

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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum/TypeEducation")
public class TypeEducationController {

    private TypeEducationService typeEducationService;

    public TypeEducationController(TypeEducationService typeEducationService){
        this.typeEducationService = typeEducationService;
    }

    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        return typeEducationService.create(body);
    }

    @PostMapping(value = "/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        return typeEducationService.get(body);
    }

    @PostMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        return typeEducationService.findAll(body);
    }

    @PostMapping(value = "/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        return typeEducationService.remove(body);
    }
}
