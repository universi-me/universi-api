package me.universi.experience.services;

import me.universi.api.entities.Response;
import me.universi.education.exceptions.EducationException;
import me.universi.education.exceptions.TypeEducationException;
import me.universi.experience.entities.Experience;
import me.universi.experience.entities.TypeExperience;
import me.universi.experience.exceptions.ExperienceException;
import me.universi.experience.exceptions.TypeExperienceException;
import me.universi.experience.repositories.ExperienceRepository;
import me.universi.institution.services.InstitutionService;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final InstitutionService institutionService;
    private final UserService userService;
    private final ProfileService profileService;
    private final TypeExperienceService typeExperienceService;


    public ExperienceService(ExperienceRepository experienceRepository, UserService userService, ProfileService profileService, TypeExperienceService typeExperienceService, InstitutionService institutionService){
        this.experienceRepository = experienceRepository;
        this.userService = userService;
        this.profileService = profileService;
        this.typeExperienceService = typeExperienceService;
        this.institutionService = institutionService;
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
        return experienceRepository.findFirstById(id);
    }

    public Experience update(Experience newExperience, UUID id) throws Exception{
        return experienceRepository.findById(id).map(experience -> {
            experience.setTypeExperience(newExperience.getTypeExperience());
            experience.setInstitution(newExperience.getInstitution());
            experience.setDescription(newExperience.getDescription());
            experience.setStartDate(newExperience.getStartDate());
            experience.setEndDate(newExperience.getEndDate());
            experience.setPresentDate(newExperience.getPresentDate());
            return save(experience);
        }).orElseGet(()->{
            try {
                return save(newExperience);
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

            var institutionId = CastingUtil.getUUID(body.get("institutionId")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new TypeEducationException("Parâmetro 'institutionId' inválido ou não informado.");
            });

            var institution = institutionService.find(institutionId).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new TypeEducationException("Instituição com id '" + institutionId.toString() + "' não encontrado.");
            });

            String description = (String) body.get("description");
            if(description.isBlank() || description.isEmpty()){
                throw new TypeEducationException("Paramentro description passado é nulo");
            }

            Date startDate = simpleDateFormat.parse((String) body.get("startDate"));
            if(startDate == null){
                throw new EducationException("Paramentro startDate passado é nulo");
            }

            Boolean presentDate = (Boolean) body.get("presentDate");

            String endDateString = (String) body.get("endDate");
            Date endDate = null;
            if(endDateString == null && presentDate == null){
                throw new EducationException("Paramentro endDate e presentDate passado é nulo");
            }

            if(endDateString != null) {
                endDate = simpleDateFormat.parse(endDateString);
            }
            if(endDate != null && startDate != null){
                if(endDate.before(startDate)) {
                    throw new EducationException("Data de inicio não pode ser maior que a data de fim.");
                }
            }

            TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(typeExperienceId)).get();
            if(typeExperience == null) {
                throw new TypeEducationException("typeExperience não encontrado.");
            }

            Experience experience = new Experience();
            experience.setTypeExperience(typeExperience);
            experience.setInstitution(institution);
            experience.setDescription(description);
            experience.setStartDate(startDate);
            experience.setEndDate(endDate);
            if(presentDate != null){
                experience.setPresentDate(presentDate);
            }
            save(experience);
            addExperienceInProfile(user, experience);

            response.message = "Experiência criada com sucesso.";
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
            var institutionId = CastingUtil.getUUID(body.get("institutionId"));
            String description = (String) body.get("description");
            Date startDate = simpleDateFormat.parse((String) body.get("startDate"));
            Boolean presentDate = (Boolean) body.get("presentDate");

            String endDateString = (String) body.get("endDate");
            Date endDate = null;
            if(endDateString == null && presentDate == null){
                throw new EducationException("Paramentro endDate e presentDate passado é nulo");
            }

            if(endDateString != null) {
                endDate = simpleDateFormat.parse(endDateString);
            }
            if(endDate != null && startDate != null){
                if(endDate.before(startDate)) {
                    throw new EducationException("Data de inicio não pode ser maior que a data de fim.");
                }
            }



            Experience experience = experienceRepository.findById(UUID.fromString(id)).get();
            if (experience == null) {
                throw new ProfileException("Experiencia não encontrada.");
            }

            checkPermissionForEdit(experience, false);

            if(typeExperienceId != null) {
                TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(typeExperienceId)).get();
                if(typeExperience == null) {
                    throw new TypeExperienceException("Tipo de Experiencia não encontrado.");
                }
                experience.setTypeExperience(typeExperience);
            }

            if (institutionId.isPresent()) {
                var institution = institutionService.find(institutionId.get()).orElseThrow(() -> {
                    response.setStatus(HttpStatus.BAD_REQUEST);
                    return new TypeEducationException("Instituição com id '" + institutionId.toString() + "' não encontrado.");
                });

                experience.setInstitution(institution);
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

            response.message = "Experiencia atualizada com sucesso.";
            response.success = true;

        });
    }

    private void checkPermissionForEdit(Experience experience, boolean forDelete) {
        User user = userService.getUserInSession();
        Profile profile = profileService.getProfileByUserIdOrUsername(user.getProfile().getId(), user.getUsername());
        if(!profile.getExperiences().contains(experience)) {
            if(forDelete) {
                if(userService.isUserAdminSession()) {
                    return;
                }
            }
        } else {
            return;
        }
        throw new ExperienceException("Você não tem permissão para editar essa experiencia.");
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
            
            checkPermissionForEdit(experience, true);

            deleteLogic(experience.getId());

            response.message = "Experiencia removida com sucesso.";
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
