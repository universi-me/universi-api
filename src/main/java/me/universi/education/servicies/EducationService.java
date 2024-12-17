package me.universi.education.servicies;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import me.universi.api.entities.Response;
import me.universi.education.entities.Education;
import me.universi.education.entities.TypeEducation;
import me.universi.education.exceptions.EducationException;
import me.universi.education.exceptions.TypeEducationException;
import me.universi.education.repositories.EducationRepository;
import me.universi.institution.entities.Institution;
import me.universi.institution.services.InstitutionService;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class EducationService {
    @PersistenceContext
    private EntityManager entityManager;
    private final EducationRepository educationRepository;
    private final UserService userService;
    private final ProfileService profileService;
    private final InstitutionService institutionService;
    private final TypeEducationService typeEducationService;

    public EducationService(EducationRepository educationRepository, UserService userService, ProfileService profileService, TypeEducationService typeEducationService, InstitutionService institutionService){
        this.educationRepository = educationRepository;
        this.userService = userService;
        this.profileService = profileService;
        this.typeEducationService = typeEducationService;
        this.institutionService =institutionService;
    }

    public Education save(Education education) {
        try {
            return educationRepository.saveAndFlush(education);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public List<Education> findAll(){
        return educationRepository.findAll();
    }

    public Optional<Education> findById(UUID id){
        return educationRepository.findFirstById(id);
    }

    public Education update(Education newEducation, UUID id) {
        return findById(id).map(education -> {
            education.setTypeEducation(newEducation.getTypeEducation());
            education.setInstitution(newEducation.getInstitution());
            education.setStartDate(newEducation.getStartDate());
            education.setEndDate(newEducation.getEndDate());
            education.setPresentDate(newEducation.getPresentDate());
            return save(education);
        }).orElseGet(()->{
            try {
                return save(newEducation);
            }catch (Exception e){
                return null;
            }
        });
    }

    public List<Profile> findByTypeEducation(UUID idTypeEducation){
        // Crie a consulta nativa
        String sql = "SELECT p.* FROM profile p JOIN education e ON p.id = e.profile_id WHERE e.type_education_id = :idTypeEducation";
        Query query = entityManager.createNativeQuery(sql, Profile.class);

        // Defina os parâmetros da consulta
        query.setParameter("idTypeEducation", idTypeEducation);

        // Execute a consulta e obtenha os resultados
        List<Profile> resultados = query.getResultList();

        return resultados;
    }

    public void addEducationInProfile(User user, Education newEducation) throws ProfileException {
        Profile profile = profileService.getProfileByUserIdOrUsername(user.getProfile().getId(), user.getUsername());
        profile.getEducations().add(newEducation);
        profileService.save(profile);
    }

    public void delete(Education education) {
        education.setDeleted(true);
        update(education, education.getId());
    }

    public void deleteLogic(UUID id) {
        Education education = findById(id).get();
        delete(education);
    }

    public Response update(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

            String id = (String)body.get("educationId");
            if(id == null) {
                throw new EducationException("Parametro educationId é nulo.");
            }

            String typeEducationId = (String)body.get("typeEducationId");
            String institutionId = (String)body.get("institutionId");
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



            Education education = findById(UUID.fromString(id)).get();
            if (education == null) {
                throw new EducationException("Formação não encontrada.");
            }

            checkPermissionForEdit(education, false);

            if(typeEducationId != null) {
                TypeEducation typeEducation = typeEducationService.findOrThrow(UUID.fromString(typeEducationId));
                if(typeEducation == null) {
                    throw new EducationException("Tipo de Education não encontrado.");
                }
                education.setTypeEducation(typeEducation);
            }

            if(institutionId != null) {
                Institution institution = institutionService.findOrThrow(UUID.fromString(institutionId));
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

            update(education, education.getId());

            response.message = "Formação atualizada";
            response.success = true;

        });
    }

    private void checkPermissionForEdit(Education education, boolean forDelete) {
        User user = userService.getUserInSession();
        Profile profile = profileService.getProfileByUserIdOrUsername(user.getProfile().getId(), user.getUsername());
        if(!profile.getEducations().contains(education)) {
            if(forDelete) {
                if(userService.isUserAdminSession()) {
                    return;
                }
            }
        } else {
            return;
        }
        throw new EducationException("Você não tem permissão para editar essa formação.");
    }


    public Response create(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            User user = userService.getUserInSession();
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat  simpleDateFormat = new SimpleDateFormat(dateFormat);

            String typeEducationId = (String)body.get("typeEducationId");
            if(typeEducationId == null || typeEducationId.isBlank() || typeEducationId.isEmpty()){
                throw new TypeEducationException("Paramentro typeEducationId passado é nulo");
            }

            String institutionId = (String) body.get("institutionId");
            if(institutionId == null || institutionId.isBlank() || institutionId.isEmpty()){
                throw new TypeEducationException("Paramentro institutionId passado é nulo");
            }

            Date startDate = simpleDateFormat.parse((String) body.get("startDate"));
            if(startDate == null){
                throw new EducationException("Paramentro starDate passado é nulo");
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

            TypeEducation typeEducation = typeEducationService.findOrThrow(UUID.fromString(typeEducationId));
            if(typeEducation == null) {
                throw new TypeEducationException("TypeEducation não encontrado.");
            }

            Institution institution = institutionService.findOrThrow(UUID.fromString(institutionId));

            Education newEducation = new Education();
            newEducation.setTypeEducation(typeEducation);
            newEducation.setInstitution(institution);
            newEducation.setStartDate(startDate);
            newEducation.setEndDate(endDate);
            if(presentDate == true){
                newEducation.setPresentDate(presentDate);
                newEducation.setEndDate(null);
            }else{
                newEducation.setPresentDate(false);
            }

            save(newEducation);

            addEducationInProfile(user, newEducation);

            response.message = "Formação criada.";
            response.success = true;

        });
    }

    public Response get(Map<String, Object> body) {
        return Response.buildResponse(response -> {
            String id = (String)body.get("educationId");

            if(id == null || id.isEmpty()) {
                throw new TypeEducationException("Parametro id é nulo.");
            }

            Education education = findById(UUID.fromString(id)).get();

            response.body.put("education", education);
            response.success = true;

        });
    }

    public Response findAll(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<Education> educations = findAll();

            response.body.put("lista", educations);

            response.message = "Operação realizada com exito.";
            response.success = true;

        });
    }

    public Response remove(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("educationId");
            if(id == null || id.isEmpty()) {
                throw new TypeEducationException("Parametro educationId é nulo.");
            }

            Optional<Education> education = findById(UUID.fromString(id));
            if (education.isEmpty()) {
                throw new EducationException("Formação não encontrada.");
            }

            checkPermissionForEdit(education.get(), true);

            delete(education.get());

            response.message = "Formação removida com sucesso.";
            response.success = true;

        });
    }
}
