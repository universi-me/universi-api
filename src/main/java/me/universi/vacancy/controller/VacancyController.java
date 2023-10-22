package me.universi.vacancy.controller;

import me.universi.api.entities.Response;
import me.universi.competence.entities.Competence;
import me.universi.competence.enums.Level;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.vacancy.entities.Vacancy;
import me.universi.vacancy.exceptions.VacancyException;
import me.universi.vacancy.services.VacancyService;
import me.universi.vacancy.typeVacancy.entities.TypeVacancy;
import me.universi.vacancy.typeVacancy.service.TypeVacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.ValidationException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/vacancy")
public class VacancyController {

    public VacancyService vacancyService;
    private UserService userService;
    private TypeVacancyService typeVacancyService;

    public VacancyController(VacancyService vacancyService, UserService userService, TypeVacancyService typeVacancyService){
        this.vacancyService = vacancyService;
        this.userService = userService;
        this.typeVacancyService = typeVacancyService;
    }

    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            User user = userService.getUserInSession();
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

            String typeVacancyId = (String)body.get("typeVacancyId");
            if(typeVacancyId == null) {
                throw new CompetenceException("Parametro typeVacancyId é nulo.");
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
            newVacancy.setRegistrationDate(registrationDate);
            newVacancy.setEndRegistrationDate(endRegistrationDate);

            vacancyService.save(newVacancy);

            response.message = "Vaga Criada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

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


            Vacancy vacancy = vacancyService.findFirstById(vacancyId);
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

            vacancyService.save(vacancy);

            response.message = "Vaga atualizada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String vacancyId = (String)body.get("vacancyId");
            if(vacancyId == null) {
                throw new CompetenceException("Parametro vacancyId é nulo.");
            }

            Vacancy vacancy = vacancyService.findFirstById(vacancyId);
            if (vacancy == null) {
                throw new CompetenceException("Vaga não encontrada.");
            }

            vacancyService.deleteLogic(UUID.fromString(vacancyId));

            response.message = "Vaga removida";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String vacancyId = (String)body.get("vacancyId");
            if(vacancyId == null) {
                throw new CompetenceException("Parametro vacancyId é nulo.");
            }

            Vacancy vacancy = vacancyService.findFirstById(vacancyId);
            if (vacancy == null) {
                throw new CompetenceException("Vaga não encontrada.");
            }

            response.body.put("Vaga", vacancy);

            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            List<Vacancy> vacancies = vacancyService.findAll();

            response.body.put("lista", vacancies);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/listarActive", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAllActive(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            List<Vacancy> vacancies = vacancyService.findAllActive();

            response.body.put("lista vagas ativas", vacancies);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }
}
