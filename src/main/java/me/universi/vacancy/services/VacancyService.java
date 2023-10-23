package me.universi.vacancy.services;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.enums.Level;
import me.universi.competence.services.CompetenceService;
import me.universi.curriculum.education.entities.Education;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.vacancy.entities.Vacancy;
import me.universi.vacancy.repositories.VacancyRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VacancyService {

    private VacancyRepository vacancyRepository;

    public ProfileService profileService;
    public UserService userService;
    private CompetenceService competenceService;

    public VacancyService(VacancyRepository vacancyRepository, ProfileService profileService, UserService userService, CompetenceService competenceService){
        this.vacancyRepository = vacancyRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.competenceService = competenceService;
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

    public Vacancy findFirstById(UUID id) {
        return vacancyRepository.findFirstById(id);
    }

    public Vacancy addCompetenceInVacancy(UUID id, Competence newCompetence){
        try {
            Vacancy vacancy = vacancyRepository.findFirstById(id);
            /*Implementar execetion para quando nao existir vaga com o id passado*/
            competenceService.save(newCompetence);
            vacancy.getCompetenceRequired().add(newCompetence);
            return vacancyRepository.saveAndFlush(vacancy);
        }catch (Exception e){
            return null;
        }
    }

    public Vacancy findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
    }

    public void deleteLogic(UUID id) throws Exception {
        Vacancy vacancy = findFirstById(id);
        vacancy.setDeleted(true);
        save(vacancy);
    }

    public List<Vacancy> findAllActive(){
        List<Vacancy> vacanciesActive = new ArrayList<>();
        for(Vacancy vacancy: findAll()){
            refreshActive(vacancy);
            if(vacancy.getActive()){
                vacanciesActive.add(vacancy);
            }
        }
        return vacanciesActive;
    }

    /*Colocar essa funcao em um timer futuramente 21/10/23*/
    private void refreshActive(Vacancy vacancy){
        Date dataAtual = new Date(); // Isso pega a data e hora atual.
        if (dataAtual.after(vacancy.getEndRegistrationDate())){
            vacancy.setActive(false);
        }
    }

    public List<Vacancy> findByCompetenceTypeAndLevel(CompetenceType competenceType, Level level){
        List<Vacancy> vacancies = new ArrayList<>();
        for (Vacancy vacancy: findAllActive()) {
            for (Competence competence : vacancy.getCompetenceRequired()) {
                if (competence.getCompetenceType().equals(competenceType) && competence.getLevel().equals(level)){
                    vacancies.add(vacancy);
                }
            }
        }
        return vacancies;
    }

}
