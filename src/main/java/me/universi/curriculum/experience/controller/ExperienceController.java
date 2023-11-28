package me.universi.curriculum.experience.controller;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.exceptions.EducationException;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.experience.entities.Experience;
import me.universi.curriculum.experience.entities.TypeExperience;
import me.universi.curriculum.experience.exceptions.TypeExperienceException;
import me.universi.curriculum.experience.servicies.ExperienceService;
import me.universi.curriculum.experience.servicies.TypeExperienceService;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum/experience")
public class ExperienceController {

    private ExperienceService experienceService;
    private UserService userService;
    private TypeExperienceService typeExperienceService;
    private ProfileService profileService;

    public ExperienceController(ExperienceService experienceService, UserService userService, TypeExperienceService typeExperienceService, ProfileService profileService){
        this.experienceService = experienceService;
        this.userService = userService;
        this.typeExperienceService = typeExperienceService;
        this.profileService = profileService;
    }

    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        return experienceService.create(body);
    }

    @PostMapping(value = "/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        return experienceService.update(body);
    }

    @PostMapping(value = "/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        return experienceService.remove(body);
    }

    @PostMapping(value = "/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        return experienceService.get(body);
    }

    @PostMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        return experienceService.findAll(body);
    }

}
