package me.universi.curriculum.experience.servicies;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.experience.entities.Experience;
import me.universi.curriculum.experience.entities.TypeExperience;
import me.universi.curriculum.experience.exceptions.TypeExperienceException;
import me.universi.curriculum.experience.repositories.ExperienceRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExperienceService {

    private ExperienceRepository experienceRepository;
    private UserService userService;
    private ProfileService profileService;
    private TypeExperienceService typeExperienceService;


    public ExperienceService(ExperienceRepository experienceRepository, UserService userService, ProfileService profileService
            , TypeExperienceService typeExperienceService){
        this.experienceRepository = experienceRepository;
        this.userService = userService;
        this.profileService =profileService;
        this.typeExperienceService = typeExperienceService;
    }

    public Experience save(Experience experience){
        try {
            return experienceRepository.saveAndFlush(experience);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public List<Experience> findAll(){
        return experienceRepository.findAll();
    }

    public Optional<Experience> findById(UUID id){
        return experienceRepository.findById(id);
    }

    public Experience update(Experience newExperience, UUID id) throws Exception{
        return experienceRepository.findById(id).map(experience -> {
            experience.setTypeExperience(newExperience.getTypeExperience());
            experience.setLocal(newExperience.getLocal());
            experience.setDescription(newExperience.getDescription());
            experience.setStartDate(newExperience.getStartDate());
            experience.setEndDate(newExperience.getEndDate());
            experience.setPresentDate(newExperience.getPresentDate());
            return experienceRepository.saveAndFlush(experience);
        }).orElseGet(()->{
            try {
                return experienceRepository.saveAndFlush(newExperience);
            }catch (Exception e){
                return null;
            }
        });
    }

    public void deleteLogic(UUID id) throws Exception {
        Experience experience = findById(id).get();
        experience.setDeleted(true);
        update(experience, id);
    }

    public void addExperienceInProfile(User user, Experience newExperience) throws ProfileException {
        Profile profile = profileService.getProfileByUserIdOrUsername(user.getProfile().getId(), user.getUsername());
        profile.getExperiences().add(newExperience);
        profileService.save(profile);
    }

    public Response create(Map<String, Object> body) {
        return Response.buildResponse(response -> {

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
            save(experience);
            addExperienceInProfile(user, experience);

            response.message = "Experiencia criada.";
            response.success = true;

        });
    }


    public Response update(Map<String, Object> body) {
        return Response.buildResponse(response -> {

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



            Experience experience = experienceRepository.findById(UUID.fromString(id)).get();
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

            update(experience, experience.getId());

            response.message = "Experience atualizada";
            response.success = true;

        });
    }


    public Response remove(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("profileExperienceId");
            if(id == null) {
                throw new TypeEducationException("Parametro experienceId é nulo.");
            }

            Experience experience = experienceRepository.findById(UUID.fromString(id)).get();
            if (experience == null) {
                throw new ProfileException("profileExperience não encontrada.");
            }

            deleteLogic(experience.getId());

            response.message = "profileExperience removida logicamente";
            response.success = true;

        });
    }

    public Response get(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("profileExperienceId");
            if(id == null) {
                throw new TypeEducationException("Parametro profileExperienceId é nulo.");
            }

            Experience experience = experienceRepository.findById(UUID.fromString(id)).get();
            if (experience == null) {
                throw new TypeEducationException("profileExperience não encontrada.");
            }

            response.body.put("profileExperience", experience);
            response.success = true;

        });
    }

    public Response findAll(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<Experience> experiences = findAll();

            response.body.put("lista", experiences);
            response.success = true;

        });
    }
}
