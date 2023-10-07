package me.universi.vacancy.services;

import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.vacancy.entities.Vacancy;
import me.universi.vacancy.repositories.VacancyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VacancyService {

    private VacancyRepository vacancyRepository;

    public ProfileService profileService;
    public UserService userService;

    public VacancyService(VacancyRepository vacancyRepository, ProfileService profileService, UserService userService){
        this.vacancyRepository = vacancyRepository;
        this.profileService = profileService;
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
    public void delete(UUID id) {
        vacancyRepository.deleteById(id);
    }

    public List<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }

    public Optional<Vacancy> findFirstById(UUID id) {
        return vacancyRepository.findFirstById(id);
    }

    public Optional<Vacancy> findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
    }

    /*Refatorar*/
    public Vacancy update(Vacancy newVacancy, UUID id) throws Exception{
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
