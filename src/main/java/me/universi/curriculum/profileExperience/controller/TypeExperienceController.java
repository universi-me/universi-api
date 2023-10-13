package me.universi.curriculum.profileExperience.controller;

import me.universi.api.entities.Response;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.enums.Level;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.curriculum.education.exceptions.InstitutionException;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.profileExperience.entities.TypeExperience;
import me.universi.curriculum.profileExperience.repositories.TypeExperienceRepository;
import me.universi.curriculum.profileExperience.servicies.TypeExperienceService;
import me.universi.user.entities.User;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum/typeExperience")
public class TypeExperienceController {

    private TypeExperienceService typeExperienceService;

    public TypeExperienceController(TypeExperienceService typeExperienceService){
        this.typeExperienceService = typeExperienceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TypeExperience save(@RequestBody TypeExperience typeExperience) throws Exception{
        return  typeExperienceService.save(typeExperience);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TypeExperience> getAllCurriculum() throws Exception{
        return typeExperienceService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<TypeExperience> getTypeExperience(@PathVariable UUID id){
        return typeExperienceService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TypeExperience update(@RequestBody TypeExperience newTypeExperience, @PathVariable UUID id) throws Exception {
        return typeExperienceService.update(newTypeExperience, id);
    }

    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String name = (String)body.get("name");
            if(name == null) {
                throw new CompetenceException("Parametro name é nulo.");
            }

            TypeExperience typeExperience = new TypeExperience();
            typeExperience.setName(name);

            typeExperienceService.save(typeExperience);

            response.message = "TypeExperience Criada.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("typeExperienceId");
            if(id == null) {
                throw new TypeEducationException("Parametro typeExperienceId é nulo.");
            }

            String name = (String)body.get("name");

            TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(id)).get();
            if (typeExperience == null) {
                throw new TypeEducationException("typeExperience não encontrada.");
            }

            if (name != null) {
                throw new TypeEducationException("Parametro name nulo.");
            }

            typeExperience.setName(name);

            typeExperienceService.update(typeExperience, typeExperience.getId());

            response.message = "Competência atualizada";
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

            String id = (String)body.get("typeExperienceId");
            if(id == null) {
                throw new TypeEducationException("Parametro typeExperienceId é nulo.");
            }

            TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(id)).get();
            if (typeExperience == null) {
                throw new CompetenceException("TypeExperience não encontrada.");
            }

            typeExperienceService.deleteLogic(typeExperience.getId());

            response.message = "TypeExperience removida logicamente";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("typeExperienceId");
            if(id == null) {
                throw new TypeEducationException("Parametro typeExperienceId é nulo.");
            }

            TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(id)).get();
            if (typeExperience == null) {
                throw new TypeEducationException("TypeExperience não encontrada.");
            }

            response.body.put("typeExperience", typeExperience);

            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            List<TypeExperience> typeExperiences = typeExperienceService.findAll();

            response.body.put("lista", typeExperiences);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

}
