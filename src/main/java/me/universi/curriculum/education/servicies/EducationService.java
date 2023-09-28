package me.universi.curriculum.education.servicies;


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


}
