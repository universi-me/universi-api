package me.universi.competence.services;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.repositories.CompetenceRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Objects;

@Service
public class CompetenceService {

    private CompetenceRepository competenceRepository;
    private ProfileService profileService;
    private UserService userService;
    private CompetenceTypeService competenceTypeService;

    public CompetenceService(CompetenceRepository competenceRepository, ProfileService profileService, UserService userService, CompetenceTypeService competenceTypeService){
        this.competenceRepository = competenceRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.competenceTypeService = competenceTypeService;
    }

    public Competence findFirstById(UUID id) {
        Optional<Competence> optionalCompetence = competenceRepository.findFirstById(id);
        if(optionalCompetence.isPresent()){
            return optionalCompetence.get();
        }else{
            return null;
        }
    }

    public Competence save(Competence competence) throws Exception {
        try {
            return competenceRepository.saveAndFlush(competence);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Competence findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
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
                    if(Objects.equals(competence.getId(), compNow.getId())) {
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

    public Competence update(Competence newCompetence, UUID id) throws Exception{
        return competenceRepository.findFirstById(id).map(competence -> {
            competence.setTitle(newCompetence.getTitle());
            competence.setDescription(newCompetence.getDescription());
            competence.setLevel(newCompetence.getLevel());
            competence.setStartDate(newCompetence.getStartDate());
            competence.setEndDate(newCompetence.getEndDate());
            competence.setPresentDate(newCompetence.getPresentDate());
            return competenceRepository.saveAndFlush(competence);
        }).orElseGet(()->{
            try {
                return competenceRepository.saveAndFlush(newCompetence);
            }catch (Exception e){
                return null;
            }
        });
    }

    public void addCompetenceInProfile(User user,Competence newCompetence) throws ProfileException {
        Profile profile = profileService.getProfileByUserIdOrUsername(user.getProfile().getId(), user.getUsername());
        profile.getCompetences().add(newCompetence);
        profileService.save(profile);
    }

    public void delete(UUID id) {
        competenceRepository.deleteById(id);
    }

}
