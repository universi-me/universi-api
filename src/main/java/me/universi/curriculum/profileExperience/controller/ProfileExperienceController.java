package me.universi.curriculum.profileExperience.controller;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.profileExperience.entities.ProfileExperience;
import me.universi.curriculum.profileExperience.entities.TypeExperience;
import me.universi.curriculum.profileExperience.exceptions.TypeExperienceException;
import me.universi.curriculum.profileExperience.servicies.ProfileExperienceService;
import me.universi.curriculum.profileExperience.servicies.TypeExperienceService;
import me.universi.profile.entities.Profile;
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
@RequestMapping(value = "/api/curriculum/profileExperience")
public class ProfileExperienceController {

    private ProfileExperienceService profileExperienceService;
    private UserService userService;
    private TypeExperienceService typeExperienceService;
    private ProfileService profileService;

    public ProfileExperienceController(ProfileExperienceService profileExperienceService, UserService userService, TypeExperienceService typeExperienceService, ProfileService profileService){
        this.profileExperienceService = profileExperienceService;
        this.userService = userService;
        this.typeExperienceService = typeExperienceService;
        this.profileService = profileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileExperience save(@RequestBody ProfileExperience profileExperience) throws Exception{
        return  profileExperienceService.save(profileExperience);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProfileExperience> findAll() throws Exception{
        return profileExperienceService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ProfileExperience> getProfileExperience(@PathVariable UUID id){
        return profileExperienceService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProfileExperience update(@RequestBody ProfileExperience newProfileExperience, @PathVariable UUID id) throws Exception {
        return profileExperienceService.update(newProfileExperience, id);
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

            ProfileExperience profileExperience = new ProfileExperience();
            profileExperience.setTypeExperience(typeExperience);
            profileExperience.setLocal(local);
            profileExperience.setDescription(description);
            profileExperience.setStartDate(startDate);
            profileExperience.setEndDate(endDate);
            if(presentDate != null){
                profileExperience.setPresentDate(presentDate);
            }
            profileExperienceService.save(profileExperience);

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



            ProfileExperience profileExperience = profileExperienceService.findById(UUID.fromString(id)).get();
            if (profileExperience == null) {
                throw new ProfileException("Experiencia não encontrada.");
            }

            if(typeExperienceId != null) {
                TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(typeExperienceId)).get();
                if(typeExperience == null) {
                    throw new TypeExperienceException("Tipo de Experiencia não encontrado.");
                }
                profileExperience.setTypeExperience(typeExperience);
            }

            if (local != null) {
                profileExperience.setLocal(local);
            }
            if (description != null) {
                profileExperience.setDescription(description);
            }
            if (startDate != null) {
                profileExperience.setStartDate(startDate);
            }
            if (endDate != null) {
                profileExperience.setEndDate(endDate);
            }
            if (presentDate != null){
                profileExperience.setPresentDate(presentDate);
            }

            profileExperienceService.update(profileExperience, profileExperience.getId());

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

            ProfileExperience profileExperience = profileExperienceService.findById(UUID.fromString(id)).get();
            if (profileExperience == null) {
                throw new ProfileException("profileExperience não encontrada.");
            }

            profileExperienceService.deleteLogic(profileExperience.getId());

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

            ProfileExperience profileExperience = profileExperienceService.findById(UUID.fromString(id)).get();
            if (profileExperience == null) {
                throw new TypeEducationException("profileExperience não encontrada.");
            }

            response.body.put("profileExperience", profileExperience);

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

            List<ProfileExperience> profileExperiences = profileExperienceService.findAll();

            response.body.put("lista", profileExperiences);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }
    @PostMapping(value = "/obterExperienceActiveByProfile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getByProfileOnlyExperienceActive(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("profileId");
            if(id == null) {
                throw new ProfileException("Parametro profileId é nulo.");
            }

            Profile profile = profileService.findFirstById(UUID.fromString(id));
            if (profile == null) {
                throw new ProfileException("profile não encontrada.");
            }

            List<ProfileExperience> profileExperiences = profileExperienceService.findByProfileAndExperienceActive(profile);
            if (profileExperiences == null){
                throw new ProfileException("valor de profileExperiences nulo.");
            }

            response.body.put("profileExperience", profileExperiences);

            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

}
