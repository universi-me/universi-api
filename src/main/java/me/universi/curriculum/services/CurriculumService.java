package me.universi.curriculum.services;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CurriculumService {

    public ProfileService profileService;
    public UserService userService;
    public CompetenceService competenceService;
    private CompetenceTypeService competenceTypeService;

    public CurriculumService(ProfileService profileService, UserService userService, CompetenceService competenceService, CompetenceTypeService competenceTypeService){
        this.profileService = profileService;
        this.userService = userService;
        this.competenceService = competenceService;
        this.competenceTypeService = competenceTypeService;
    }


    public Collection<Competence> findByProfile(Profile profile){
        return profile.getCompetences();
    }

}
