package me.universi.curriculum.services;

import me.universi.curriculum.entities.Curriculum;
import me.universi.curriculum.repositories.CurriculumRepository;
import me.universi.profile.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurriculumService {

    private CurriculumRepository curriculumRepository;
    public PerfilService perfilService;
    public UserService userService;

    public CurriculumService(CurriculumRepository curriculumRepository, PerfilService perfilService, UserService userService){
        this.curriculumRepository = curriculumRepository;
        this.perfilService = perfilService;
        this.userService = userService;
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
    public void delete(Long id) {
        curriculumRepository.deleteById(id);
    }

    public List<Curriculum> findAll() {
        return curriculumRepository.findAll();
    }

    public Optional<Curriculum> findById(Long id){
        return curriculumRepository.findById(id);
    }

    public Curriculum update(Curriculum newCurriculum, Long id) throws Exception{
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

}
