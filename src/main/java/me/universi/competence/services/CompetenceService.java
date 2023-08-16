package me.universi.competence.services;

import me.universi.competence.entities.Competence;
import me.universi.competence.repositories.CompetenceRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CompetenceService {

    private CompetenceRepository competenceRepository;
    public ProfileService profileService;
    public UserService userService;

    public CompetenceService(CompetenceRepository competenceRepository, ProfileService profileService, UserService userService){
        this.competenceRepository = competenceRepository;
        this.profileService = profileService;
        this.userService = userService;
    }
    public Competence findFirstById(Long id) {
        Optional<Competence> optionalCompetence = competenceRepository.findFirstById(id);
        if(optionalCompetence.isPresent()){
            return optionalCompetence.get();
        }else{
            return null;
        }
    }

    public Competence save(Competence competence) throws Exception{
        try {
            User user = userService.getUserInSession();
            competence.setProfile(user.getProfile());
            return competenceRepository.saveAndFlush(competence);

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public void delete(Competence competence) {
        competenceRepository.delete(competence);
    }

    public List<Competence> findAll() {
        return competenceRepository.findAll();
    }

    public void update(Competence competence){ competenceRepository.saveAndFlush(competence); }

    public boolean profileHasCompetence(Profile profile, Competence competence) {
        try {
            if(profile.getCompetences() != null) {
                for(Competence compNow : profile.getCompetences()) {
                    if(competence.getId() == compNow.getId()) {
                        return true;
                    }
                }
            }
        }catch (Exception e) {
            return false;
        }
        return false;
    }
    public void deleteAll(Collection<Competence> competences){
        competenceRepository.deleteAll(competences);
    }

    public Optional<Competence> findById(Long id){
        return competenceRepository.findById(id);
    }

    public Competence update(Competence newCompetence, Long id) throws Exception{
        return competenceRepository.findById(id).map(competence -> {
            competence.setTitle(newCompetence.getTitle());
            competence.setDescription(newCompetence.getDescription());
            competence.setLevel(newCompetence.getLevel());
            competence.setStartDate(newCompetence.getStartDate());
            competence.setEndDate(newCompetence.getEndDate());
            competence.setCurrentDate(newCompetence.getCurrentDate());
            return competenceRepository.saveAndFlush(competence);
        }).orElseGet(()->{
            try {
                return competenceRepository.saveAndFlush(newCompetence);
            }catch (Exception e){
                return null;
            }
        });
    }

    public void delete(Long id) {
        competenceRepository.deleteById(id);
    }

}
