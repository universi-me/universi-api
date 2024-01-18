package me.universi.curriculum.services;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.servicies.EducationService;
import me.universi.curriculum.experience.entities.Experience;
import me.universi.curriculum.experience.entities.TypeExperience;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class CurriculumService {
    private final ProfileService profileService;
    private final UserService userService;
    private final EducationService educationService;

    public CurriculumService(ProfileService profileService, UserService userService, EducationService educationService) {
        this.profileService = profileService;
        this.userService = userService;
        this.educationService = educationService;
    }


    public Collection<Competence> findByProfile(Profile profile){
        return profile.getCompetences();
    }

    public List<List> mountCurriculum(){
        User user = userService.getUserInSession();
        List<List> curriculum = new ArrayList<List>();

        return curriculum;
    }

    public List<Profile> getProfileByEducation(UUID idEducation){
        return educationService.findByTypeEducation(idEducation);
    }

    public Collection<Profile> filtrarCurriculum(CompetenceType competenceType, Integer level,
                                                 TypeExperience typeExperience, TypeEducation typeEducation){
        Collection<Profile> profiles = profileService.findAll();
        Collection<Profile> profilesTemp = new ArrayList<>();
        /*Otimizar essa busca para que so busque de acordo com o grupo*/
        if(competenceType!=null) {
            for (Profile profile : profiles) {
                for (Competence competence : profile.getCompetences()) {
                    if (competence.getCompetenceType().equals(competenceType)){
                        if(level!=null && competence.getLevel() == level){
                            profilesTemp.add(profile);
                        }
                    }
                }
            }
            profiles = profilesTemp;
            profilesTemp.clear();
        }

        if(typeExperience!=null){
            for (Profile profile: profiles) {
                for (Experience experience: profile.getExperiences()) {
                    if (experience.getTypeExperience().equals(typeExperience)){
                        profilesTemp.add(profile);
                    }
                }
            }
            profiles = profilesTemp;
            profilesTemp.clear();
        }

        if(typeEducation!=null){
            for (Profile profile: profiles) {
                for (Education education: profile.getEducations()) {
                    if (education.getTypeEducation().equals(typeEducation)){
                        profilesTemp.add(profile);
                    }
                }
            }
            profiles = profilesTemp;
            profilesTemp.clear();
        }
        return profiles;
    }

}
