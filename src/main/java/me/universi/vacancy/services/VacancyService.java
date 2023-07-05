package me.universi.vacancy.services;

import me.universi.curriculum.entities.Curriculum;
import me.universi.profile.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.vacancy.entities.Vacancy;
import me.universi.vacancy.repositories.VacancyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {

    private VacancyRepository vacancyRepository;

    public PerfilService perfilService;
    public UserService userService;

    public VacancyService(VacancyRepository vacancyRepository, PerfilService perfilService, UserService userService){
        this.vacancyRepository = vacancyRepository;
        this.perfilService = perfilService;
        this.userService = userService;
    }

    public Vacancy save(Vacancy vacancy) throws Exception{
        try {
            User user = userService.getUserInSession();
            vacancy.setProfile(user.getProfile());
            return vacancyRepository.saveAndFlush(vacancy);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public void delete(Long id) {
        vacancyRepository.deleteById(id);
    }

    public List<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }

    public Optional<Vacancy> findById(Long id){
        return vacancyRepository.findById(id);
    }

    public Vacancy update(Vacancy newVacancy, Long id) throws Exception{
        return vacancyRepository.findById(id).map(vacancy -> {
            vacancy.setDescription(newVacancy.getDescription());
            return vacancyRepository.saveAndFlush(vacancy);
        }).orElseGet(()->{
            try {
                User user = userService.getUserInSession();
                newVacancy.setProfile(user.getProfile());
                return vacancyRepository.saveAndFlush(newVacancy);
            }catch (Exception e){
                return null;
            }
        });
    }
}
