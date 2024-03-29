package me.universi.vacancy.services;

import me.universi.api.entities.Response;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.vacancy.entities.Vacancy;
import me.universi.vacancy.exceptions.VacancyException;
import me.universi.vacancy.repositories.VacancyRepository;
import me.universi.vacancy.typeVacancy.entities.TypeVacancy;
import me.universi.vacancy.typeVacancy.service.TypeVacancyService;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProfileService profileService;
    private final UserService userService;
    private final CompetenceService competenceService;
    private final TypeVacancyService typeVacancyService;
    private final CompetenceTypeService competenceTypeService;

    public VacancyService(VacancyRepository vacancyRepository, ProfileService profileService, UserService userService, CompetenceService competenceService, TypeVacancyService typeVacancyService, CompetenceTypeService competenceTypeService){
        this.vacancyRepository = vacancyRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.competenceService = competenceService;
        this.typeVacancyService = typeVacancyService;
        this.competenceTypeService = competenceTypeService;
    }

    public Vacancy save(Vacancy vacancy) {
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
        deleteLogic(id);
    }

    public List<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }

    public Vacancy findFirstById(UUID id) {
        return vacancyRepository.findFirstById(id);
    }

    public Vacancy addCompetenceInVacancy(UUID id, Competence newCompetence){
        try {
            Vacancy vacancy = findFirstById(id);
            /*Implementar execetion para quando nao existir vaga com o id passado*/
            competenceService.save(newCompetence);
            vacancy.getCompetenceRequired().add(newCompetence);
            return save(vacancy);
        }catch (Exception e){
            return null;
        }
    }

    public Vacancy findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
    }

    public void deleteLogic(UUID id) {
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

    /*Colocar essa funcao em um timer, futuramente, 21/10/23*/
    private void refreshActive(Vacancy vacancy){
        Date dataAtual = new Date(); // Isso pega a data e hora atual.
        if (dataAtual.after(vacancy.getEndRegistrationDate())){
            vacancy.setActive(false);
        }
    }

    public List<Vacancy> findByCompetenceTypeAndLevel(CompetenceType competenceType, Integer level){
        List<Vacancy> vacancies = new ArrayList<>();
        for (Vacancy vacancy: findAllActive()) {
            for (Competence competence : vacancy.getCompetenceRequired()) {
                if (competence.getCompetenceType().equals(competenceType) && competence.getLevel() == level){
                    vacancies.add(vacancy);
                }
            }
        }
        return vacancies;
    }

    public Response create(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            User user = userService.getUserInSession();
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

            String typeVacancyId = (String)body.get("typeVacancyId");
            if(typeVacancyId == null) {
                throw new CompetenceException("Parametro typeVacancyId é nulo.");
            }

            List<Competence> competence = (List<Competence>) body.get("competence");
            if (competence == null){
                throw new VacancyException("Paramentro competence é nulo.");
            }

            String title = (String)body.get("title");
            if(title == null) {
                throw new CompetenceException("Parametro title é nulo.");
            }

            String description = (String)body.get("description");
            if(description == null) {
                throw new CompetenceException("Parametro description é nulo.");
            }

            String prerequisites = (String)body.get("prerequisites");
            if(prerequisites == null) {
                throw new CompetenceException("Parametro prerequisites é nulo.");
            }

            Date registrationDate = simpleDateFormat.parse((String) body.get("registrationDate"));
            if(registrationDate == null){
                throw new EducationException("Paramentro registrationDate passado é nulo");
            }

            Date endRegistrationDate = simpleDateFormat.parse((String) body.get("endRegistrationDate"));
            if(endRegistrationDate == null){
                throw new EducationException("Paramentro endRegistrationDate passado é nulo");
            }

            TypeVacancy typeVacancy = typeVacancyService.findById(UUID.fromString(typeVacancyId)).get();
            if(typeVacancy == null) {
                throw new ValidationException("Tipo de Vaga não encontrado.");
            }

            Vacancy newVacancy = new Vacancy();
            newVacancy.setTypeVacancy(typeVacancy);
            newVacancy.setTitle(title);
            newVacancy.setDescription(description);
            newVacancy.setPrerequisites(prerequisites);
            newVacancy.setCompetenceRequired(competence);
            newVacancy.setRegistrationDate(registrationDate);
            newVacancy.setEndRegistrationDate(endRegistrationDate);

            save(newVacancy);

            response.message = "Vaga Criada";
            response.success = true;

        });
    }

    public Response update(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat  simpleDateFormat = new SimpleDateFormat(dateFormat);

            String vacancyId = (String)body.get("vacancyId");
            if(vacancyId == null) {
                throw new CompetenceException("Parametro vacancyId é nulo.");
            }

            String typeVacancyId = (String)body.get("typeVacancyId");
            String title = (String)body.get("title");
            String description = (String)body.get("description");
            String prerequisites = (String)body.get("prerequisites");
            Date registrationDate = simpleDateFormat.parse((String) body.get("registrationDate"));
            Date endRegistrationDate = simpleDateFormat.parse((String) body.get("endRegistrationDate"));


            Vacancy vacancy = findFirstById(vacancyId);
            if (vacancy == null) {
                throw new CompetenceException("Vaga não encontrada.");
            }

            if(typeVacancyId != null && typeVacancyId.length()>0) {
                TypeVacancy typeVacancy = typeVacancyService.findById(UUID.fromString(typeVacancyId)).get();
                if(typeVacancy == null) {
                    throw new VacancyException("Tipo de Vaga não encontrado.");
                }
                vacancy.setTypeVacancy(typeVacancy);
            }
            if (title != null) {
                vacancy.setTitle(title);
            }
            if (description != null) {
                vacancy.setDescription(description);
            }
            if (prerequisites != null) {
                vacancy.setPrerequisites(prerequisites);
            }
            if (registrationDate != null) {
                vacancy.setRegistrationDate(registrationDate);
            }
            if (endRegistrationDate != null) {
                vacancy.setEndRegistrationDate(endRegistrationDate);
            }

            save(vacancy);

            response.message = "Vaga atualizada";
            response.success = true;

        });
    }

    public Response remove(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String vacancyId = (String)body.get("vacancyId");
            if(vacancyId == null) {
                throw new CompetenceException("Parametro vacancyId é nulo.");
            }

            Vacancy vacancy = findFirstById(vacancyId);
            if (vacancy == null) {
                throw new CompetenceException("Vaga não encontrada.");
            }

            deleteLogic(UUID.fromString(vacancyId));

            response.message = "Vaga removida";
            response.success = true;

        });
    }

    public Response get(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String vacancyId = (String)body.get("vacancyId");
            if(vacancyId == null) {
                throw new CompetenceException("Parametro vacancyId é nulo.");
            }

            Vacancy vacancy = findFirstById(vacancyId);
            if (vacancy == null) {
                throw new CompetenceException("Vaga não encontrada.");
            }

            response.body.put("Vaga", vacancy);
            response.success = true;

        });
    }

    public Response findAll(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<Vacancy> vacancies = findAll();

            response.body.put("lista", vacancies);

            response.message = "Operação realizada com exito.";
            response.success = true;

        });
    }

    public Response findAllActive(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<Vacancy> vacancies = findAllActive();

            response.body.put("lista vagas ativas", vacancies);

            response.message = "Operação realizada com exito.";
            response.success = true;

        });
    }

    public Response filtrar( Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<Vacancy> vacancies;

            String competenceTypeId = (String)body.get("competenceTypeId");
            if(competenceTypeId == null) {
                throw new CompetenceException("Parametro vacancyId é nulo.");
            }

            Integer level = (Integer)body.get("level");
            if(level == null) {
                throw new CompetenceException("Parametro level é nulo.");
            }

            CompetenceType competenceType = competenceTypeService.findFirstById(competenceTypeId);

            vacancies = findByCompetenceTypeAndLevel(competenceType, level);

            response.body.put("lista vagas por tipo da competencia e nivel", vacancies);

            response.message = "Operação realizada com exito.";
            response.success = true;

        });
    }

    public Response addCompetence(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Vacancy vacancie;

            String vacancyId = (String)body.get("vacancyId");
            if(vacancyId == null) {
                throw new CompetenceException("Parametro vacancyId é nulo.");
            }

            List<Competence> competence = (List<Competence>) body.get("competence");
            if (competence == null){
                throw new VacancyException("Paramentro competence é nulo.");
            }


            vacancie = findFirstById(vacancyId);

            vacancie.getCompetenceRequired().addAll(competence);

            response.body.put("lista de competencia adicionada", vacancie);

            response.message = "Operação realizada com exito.";
            response.success = true;

        });
    }

}
