package me.universi.curriculum.education.controller;


import me.universi.api.entities.Response;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.curriculum.education.exceptions.InstitutionException;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.education.servicies.EducationService;
import me.universi.curriculum.education.servicies.InstitutionService;
import me.universi.curriculum.education.servicies.TypeEducationService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Education createEducation(@RequestBody Education education){
        return educationService.save(education);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Education> getAllEducation(){
        return educationService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Education> getEducation(@PathVariable UUID id){
        return educationService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Education update(@RequestBody Education newEducation, @PathVariable UUID id) throws Exception {
        return educationService.update(newEducation, id);
    }

    @PostMapping(value = "/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat  simpleDateFormat = new SimpleDateFormat(dateFormat);

            String id = (String)body.get("educationId");
            if(id == null) {
                throw new EducationException("Parametro educationId é nulo.");
            }

            String typeEducationId = (String)body.get("typeEducationId");
            String institutionId = (String)body.get("institutionId");
            Date startDate = simpleDateFormat.parse((String) body.get("startDate"));
            Date endDate = simpleDateFormat.parse((String) body.get("endDate"));
            Boolean presentDate = (Boolean) body.get("presentDate");



            Education education = educationService.findById(UUID.fromString(id)).get();
            if (education == null) {
                throw new EducationException("Formação não encontrada.");
            }

            if(typeEducationId != null) {
                TypeEducation typeEducation = typeEducationService.findById(UUID.fromString(typeEducationId)).get();
                if(typeEducation == null) {
                    throw new EducationException("Tipo de Education não encontrado.");
                }
                education.setTypeEducation(typeEducation);
            }

            if(institutionId != null) {
                Institution institution = institutionService.findById(UUID.fromString(institutionId)).get();
                if(institution == null) {
                    throw new EducationException("Institution não encontrada.");
                }
                education.setInstitution(institution);
            }

            if (startDate != null) {
                education.setStartDate(startDate);
            }
            if (endDate != null) {
                education.setEndDate(endDate);
            }
            if (presentDate != null){
                education.setPresentDate(presentDate);
            }

            educationService.update(education, education.getId());

            response.message = "Formação atualizada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            e.printStackTrace();
            return response;
        }
    }

    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response response = new Response();

        try {

            User user = userService.getUserInSession();
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat  simpleDateFormat = new SimpleDateFormat(dateFormat);

            String typeEducationId = (String) body.get("typeEducationId");
            if(typeEducationId.isBlank() || typeEducationId.isEmpty()){
                throw new TypeEducationException("Paramentro typeEducationId passado é nulo");
            }

            String institutionId = (String) body.get("institutionId");
            if(institutionId.isBlank() || institutionId.isEmpty()){
                throw new TypeEducationException("Paramentro institutionId passado é nulo");
            }

            Date startDate = simpleDateFormat.parse((String) body.get("starDate"));
            if(startDate == null){
                throw new EducationException("Paramentro starDate passado é nulo");
            }

            Boolean presentDate = (Boolean) body.get("presentDate");
            Date endDate = simpleDateFormat.parse((String) body.get("endDate"));
            if(endDate == null && presentDate == null){
                throw new EducationException("Paramentro endDate e presentDate passado é nulo");
            }

            TypeEducation typeEducation = typeEducationService.findById(UUID.fromString(typeEducationId)).get();
            if(typeEducation == null) {
                throw new TypeEducationException("TypeEducation não encontrado.");
            }

            Institution institution = institutionService.findById(UUID.fromString(institutionId)).get();
            if(institution == null) {
                throw new InstitutionException("Institution não encontrado.");
            }

            Education newEducation = new Education();
            newEducation.setTypeEducation(typeEducation);
            newEducation.setInstitution(institution);
            newEducation.setStartDate(startDate);
            newEducation.setEndDate(endDate);
            if(presentDate != null){
                newEducation.setPresentDate(presentDate);
            }
            educationService.save(newEducation);

            educationService.addEducationInProfile(user, newEducation);

            response.message = "Formação criada.";
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
            String id = (String)body.get("educationId");

            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro id é nulo.");
            }

            Education education = educationService.findById(UUID.fromString(id)).get();

            response.body.put("education", education);

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

            List<Education> educations = educationService.findAll();

            response.body.put("lista", educations);

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

            String id = (String)body.get("educationId");
            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro educationId é nulo.");
            }

            educationService.deleteLogic(UUID.fromString(id));

            response.message = "Formação removida logicamente";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }
}
