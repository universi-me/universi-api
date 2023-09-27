package me.universi.curriculum.profileExperience.servicies;

import me.universi.curriculum.profileExperience.entities.ProfileExperience;
import me.universi.curriculum.profileExperience.repositories.ProfileExperienceRepository;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileExperienceService {

    private ProfileExperienceRepository profileExperienceRepository;
    private UserService userService;

    public ProfileExperienceService(ProfileExperienceRepository profileExperienceRepository,UserService userService){
        this.profileExperienceRepository = profileExperienceRepository;
        this.userService = userService;
    }

    public ProfileExperience save(ProfileExperience profileExperience){
        try {
            User user = userService.getUserInSession();
            profileExperience.setProfile(user.getProfile());
            return profileExperienceRepository.saveAndFlush(profileExperience);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public List<ProfileExperience> findAll(){
        return profileExperienceRepository.findAll();
    }

}
