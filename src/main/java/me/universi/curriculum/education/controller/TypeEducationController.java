package me.universi.curriculum.education.controller;


import me.universi.api.entities.Response;
import me.universi.competence.entities.Competence;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.education.servicies.TypeEducationService;
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

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<TypeEducation> getTypeEducation(@PathVariable UUID id){
        return typeEducationService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TypeEducation update(@RequestBody TypeEducation newTypeEducation, @PathVariable UUID id) throws Exception {
        return typeEducationService.update(newTypeEducation, id);
    }

    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response response = new Response();

        try {
            String name = (String) body.get("name");
            if(name.isBlank() || name.isEmpty()){
                throw new TypeEducationException("Paramentro name passado é nulo");
            }

            TypeEducation newTypeEducation = new TypeEducation(name);
            typeEducationService.save(newTypeEducation);

            response.message = "TypeEducation criada.";
            response.success = true;
            return response;

        }catch (Exception e){
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {
            String id = (String)body.get("typeEducationId");

            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro id é nulo.");
            }

            TypeEducation typeEducation = typeEducationService.findById(UUID.fromString(id)).get();

            response.body.put("typeEducation", typeEducation);

            response.success = true;
            return response;
        }catch (Exception e){
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            List<TypeEducation> typeEducations = typeEducationService.findAll();

            response.body.put("lista", typeEducations);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("tyEducationId");
            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro typeEducationId é nulo.");
            }

            typeEducationService.deleteLogic(UUID.fromString(id));

            response.message = "TypeEdcation removida logicamente";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }
}
