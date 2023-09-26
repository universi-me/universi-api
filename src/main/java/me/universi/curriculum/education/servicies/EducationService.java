package me.universi.curriculum.education.servicies;


import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.repositories.EducationRepository;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
