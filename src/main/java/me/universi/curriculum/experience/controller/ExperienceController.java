package me.universi.curriculum.experience.controller;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.experience.entities.Experience;
import me.universi.curriculum.experience.entities.TypeExperience;
import me.universi.curriculum.experience.exceptions.TypeExperienceException;
import me.universi.curriculum.experience.servicies.ExperienceService;
import me.universi.curriculum.experience.servicies.TypeExperienceService;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum/experience")
public class ExperienceController {

    private ExperienceService experienceService;
    private UserService userService;
    private TypeExperienceService typeExperienceService;
    private ProfileService profileService;

    public ExperienceController(ExperienceService experienceService, UserService userService, TypeExperienceService typeExperienceService, ProfileService profileService){
        this.experienceService = experienceService;
        this.userService = userService;
        this.typeExperienceService = typeExperienceService;
        this.profileService = profileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Experience save(@RequestBody Experience experience) throws Exception{
        return  experienceService.save(experience);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Experience> findAll() throws Exception{
        return experienceService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Experience> getProfileExperience(@PathVariable UUID id){
        return experienceService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Experience update(@RequestBody Experience newExperience, @PathVariable UUID id) throws Exception {
        return experienceService.update(newExperience, id);
    }

    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response response = new Response();

        try {

            User user = userService.getUserInSession();
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

            String typeExperienceId = (String) body.get("typeExperienceId");
            if(typeExperienceId.isBlank() || typeExperienceId.isEmpty()){
                throw new ProfileException("Paramentro typeExperienceId passado é nulo");
            }

            String local = (String) body.get("local");
            if(local.isBlank() || local.isEmpty()){
                throw new TypeEducationException("Paramentro local passado é nulo");
            }

            String description = (String) body.get("description");
            if(description.isBlank() || description.isEmpty()){
                throw new TypeEducationException("Paramentro description passado é nulo");
            }

            Date startDate = simpleDateFormat.parse((String) body.get("startDate"));
            if(startDate == null){
                throw new EducationException("Paramentro startDate passado é nulo");
            }

            Boolean presentDate = (Boolean) body.get("presentDate");
            Date endDate = simpleDateFormat.parse((String) body.get("endDate"));
            if(endDate == null && presentDate == null){
                throw new EducationException("Paramentro endDate e presentDate passado é nulo");
            }

            TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(typeExperienceId)).get();
            if(typeExperience == null) {
                throw new TypeEducationException("typeExperience não encontrado.");
            }

            Experience experience = new Experience();
            experience.setTypeExperience(typeExperience);
            experience.setLocal(local);
            experience.setDescription(description);
            experience.setStartDate(startDate);
            experience.setEndDate(endDate);
            if(presentDate != null){
                experience.setPresentDate(presentDate);
            }
            experienceService.save(experience);
            experienceService.addExperienceInProfile(user, experience);

            response.message = "Experiencia criada.";
            response.success = true;
            return response;

        }catch (Exception e){
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat  simpleDateFormat = new SimpleDateFormat(dateFormat);

            String id = (String)body.get("profileExperienceId");
            if(id == null) {
                throw new ProfileException("Parametro experienceId é nulo.");
            }

            String typeExperienceId = (String)body.get("typeExperienceId");
            String local = (String)body.get("local");
            String description = (String) body.get("description");
            Date startDate = simpleDateFormat.parse((String) body.get("startDate"));
            Date endDate = simpleDateFormat.parse((String) body.get("endDate"));
            Boolean presentDate = (Boolean) body.get("presentDate");



            Experience experience = experienceService.findById(UUID.fromString(id)).get();
            if (experience == null) {
                throw new ProfileException("Experiencia não encontrada.");
            }

            if(typeExperienceId != null) {
                TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(typeExperienceId)).get();
                if(typeExperience == null) {
                    throw new TypeExperienceException("Tipo de Experiencia não encontrado.");
                }
                experience.setTypeExperience(typeExperience);
            }

            if (local != null) {
                experience.setLocal(local);
            }
            if (description != null) {
                experience.setDescription(description);
            }
            if (startDate != null) {
                experience.setStartDate(startDate);
            }
            if (endDate != null) {
                experience.setEndDate(endDate);
            }
            if (presentDate != null){
                experience.setPresentDate(presentDate);
            }

            experienceService.update(experience, experience.getId());

            response.message = "Experience atualizada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            e.printStackTrace();
            return response;
        }
    }

    @PostMapping(value = "/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("profileExperienceId");
            if(id == null) {
                throw new TypeEducationException("Parametro experienceId é nulo.");
            }

            Experience experience = experienceService.findById(UUID.fromString(id)).get();
            if (experience == null) {
                throw new ProfileException("profileExperience não encontrada.");
            }

            experienceService.deleteLogic(experience.getId());

            response.message = "profileExperience removida logicamente";
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

            String id = (String)body.get("profileExperienceId");
            if(id == null) {
                throw new TypeEducationException("Parametro profileExperienceId é nulo.");
            }

            Experience experience = experienceService.findById(UUID.fromString(id)).get();
            if (experience == null) {
                throw new TypeEducationException("profileExperience não encontrada.");
            }

            response.body.put("profileExperience", experience);

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

            List<Experience> experiences = experienceService.findAll();

            response.body.put("lista", experiences);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

}
