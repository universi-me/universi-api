package me.universi.curriculum.education.servicies;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.repositories.EducationRepository;
import me.universi.profile.entities.Profile;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EducationService {
    @PersistenceContext
    private EntityManager entityManager;
    private EducationRepository educationRepository;
    private UserService userService;

    public EducationService(EducationRepository educationRepository, UserService userService){
        this.educationRepository = educationRepository;
        this.userService = userService;
    }

    public Education save(Education education) {
        try {
            User user = userService.getUserInSession();
            education.setProfile(user.getProfile());
            return educationRepository.saveAndFlush(education);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public List<Education> findAll(){
        return educationRepository.findAll();
    }

    public Optional<Education> findByProfileSession(){
        try{
            User user = userService.getUserInSession();
            Profile profile = user.getProfile();
           return educationRepository.findById(profile.getId());
        }catch (Exception e){
            /*Implementar tratamento de exeptions*/
            System.out.println(e);
        }
        return null;
    }

    public Optional<Education> findById(UUID id){
        return educationRepository.findById(id);
    }

    public List<Education> findByProfile(Profile profile){
        return educationRepository.findByProfile(profile);
    }

    public Education update(Education newEducation, UUID id) throws Exception{
        return educationRepository.findById(id).map(education -> {
            education.setTypeEducation(newEducation.getTypeEducation());
            education.setInstitution(newEducation.getInstitution());
            education.setStartDate(newEducation.getStartDate());
            education.setEndDate(newEducation.getEndDate());
            education.setPresentDate(newEducation.getPresentDate());
            return educationRepository.saveAndFlush(education);
        }).orElseGet(()->{
            try {
                User user = userService.getUserInSession();
                newEducation.setProfile(user.getProfile());
                return educationRepository.saveAndFlush(newEducation);
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


}
