package me.universi.competence.controller;

import me.universi.api.entities.Response;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.enums.Level;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/competencia")
public class CompetenceController {
    @Autowired
    public CompetenceService competenceService;
    @Autowired
    public CompetenceTypeService competenceTypeService;

    @Autowired
    public ProfileService profileService;

    @Autowired
    public UserService userService;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
           return competenceService.create(body);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        return competenceService.update(body);
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        return competenceService.remove(body);
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        return competenceService.get(body);
    }

    @GetMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        return competenceService.findAll(body);
    }

}
