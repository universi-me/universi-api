package me.universi.curriculum.services;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.curriculum.entities.Curriculum;
import me.universi.curriculum.repositories.CurriculumRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CurriculumService {

    private CurriculumRepository curriculumRepository;
    public ProfileService profileService;
    public UserService userService;
    public CompetenceService competenceService;
    private CompetenceTypeService competenceTypeService;

    public CurriculumService(CurriculumRepository curriculumRepository, ProfileService profileService, UserService userService, CompetenceService competenceService, CompetenceTypeService competenceTypeService){
        this.curriculumRepository = curriculumRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.competenceService = competenceService;
        this.competenceTypeService = competenceTypeService;
    }


    public Curriculum save(Curriculum curriculum) throws Exception{
        try {
            User user = userService.getUserInSession();
            curriculum.setProfile(user.getProfile());
            return curriculumRepository.saveAndFlush(curriculum);

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public void delete(UUID id) {
        curriculumRepository.deleteById(id);
    }

    public List<Curriculum> findAll() {
        return curriculumRepository.findAll();
    }

    public Optional<Curriculum> findFirstById(UUID id){
        return curriculumRepository.findFirstById(id);
    }

    public Optional<Curriculum> findFirstById(String id){
        return curriculumRepository.findFirstById(UUID.fromString(id));
    }

    public Curriculum update(Curriculum newCurriculum, UUID id) throws Exception{
        return curriculumRepository.findById(id).map(curriculum -> {
            curriculum.setDescription(newCurriculum.getDescription());
            return curriculumRepository.saveAndFlush(curriculum);
        }).orElseGet(()->{
            try {
                User user = userService.getUserInSession();
                newCurriculum.setProfile(user.getProfile());
                return curriculumRepository.saveAndFlush(newCurriculum);
            }catch (Exception e){
                return null;
            }
        });
    }

    public Collection<Competence> findCompetenceUserByType(String competenceTypeName){
        CompetenceType competenceType = competenceTypeService.findFirstByName(competenceTypeName);
        Collection<Competence> competences = findByProfile(userService.getUserInSession().getProfile());
        for (Competence comp : competences) {
            if(!comp.getCompetenceType().equals(competenceType)){
                competences.remove(comp);
            }
        }
        return competences;
    }

    public Collection<Competence> findByProfile(Profile profile){
        return profile.getCompetences();
    }
}
