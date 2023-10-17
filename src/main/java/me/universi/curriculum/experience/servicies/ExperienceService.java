package me.universi.curriculum.experience.servicies;

import me.universi.curriculum.experience.entities.Experience;
import me.universi.curriculum.experience.repositories.ExperienceRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExperienceService {

    private ExperienceRepository experienceRepository;
    private UserService userService;
    private ProfileService profileService;

    public ExperienceService(ExperienceRepository experienceRepository, UserService userService, ProfileService profileService){
        this.experienceRepository = experienceRepository;
        this.userService = userService;
        this.profileService =profileService;
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
}
