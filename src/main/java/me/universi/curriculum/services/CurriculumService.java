package me.universi.curriculum.services;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.servicies.EducationService;
import me.universi.curriculum.profileExperience.servicies.ProfileExperienceService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CurriculumService {

    private ProfileService profileService;
    private UserService userService;
    private CompetenceService competenceService;
    private CompetenceTypeService competenceTypeService;
    private EducationService educationService;

    private ProfileExperienceService profileExperienceService;


    public CurriculumService(ProfileService profileService, UserService userService, CompetenceService competenceService,
                             CompetenceTypeService competenceTypeService, EducationService educationService, ProfileExperienceService profileExperienceService){
        this.profileService = profileService;
        this.userService = userService;
        this.competenceService = competenceService;
        this.competenceTypeService = competenceTypeService;
        this.educationService = educationService;
        this.profileExperienceService = profileExperienceService;
    }


    public Collection<Competence> findByProfile(Profile profile){
        return profile.getCompetences();
    }

    public List<List> mountCurriculum(){
        User user = userService.getUserInSession();
        List<List> curriculum = new ArrayList<List>();

        curriculum.add(educationService.findByProfile(user.getProfile()));
        curriculum.add(profileExperienceService.findByProfile(user.getProfile()));

        return curriculum;
    }

    public List<Profile> getProfileByEducation(UUID idEducation){
        return educationService.findByTypeEducation(idEducation);
    }

}
