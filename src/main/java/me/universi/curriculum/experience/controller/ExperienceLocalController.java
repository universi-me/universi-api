package me.universi.curriculum.experience.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.universi.api.entities.Response;
import me.universi.curriculum.experience.exceptions.ExperienceException;
import me.universi.curriculum.experience.servicies.ExperienceLocalService;
import me.universi.util.CastingUtil;

@RestController
@RequestMapping(value = "/api/curriculum/experience/local")
public class ExperienceLocalController {
    private ExperienceLocalService experienceLocalService;

    public ExperienceLocalController(ExperienceLocalService experienceLocalService) {
        this.experienceLocalService = experienceLocalService;
    }

    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response listLocal(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("list", experienceLocalService.findAll());
        });
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response createLocal(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var name = CastingUtil.getString(body.get("name")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new ExperienceException("Parâmetro 'name' inválido ou não informado.");
            });

            var local = experienceLocalService.create(name);

            response.setStatus(HttpStatus.CREATED);
            response.message = "Local '" + name + "' criado com sucesso";
            response.body.put("experienceLocal", local);
        });
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response editLocal(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var id = CastingUtil.getUUID(body.get("id")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new ExperienceException("Parâmetro 'id' inválido ou não informado.");
            });

            var name = CastingUtil.getString(body.get("name")).orElse(null);

            var local = experienceLocalService.edit(id, name);

            response.setStatus(HttpStatus.CREATED);
            response.message = "Local '" + name + "' alterado com sucesso";
            response.body.put("experienceLocal", local);
        });
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response deleteLocal(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var id = CastingUtil.getUUID(body.get("id")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new ExperienceException("Parâmetro 'id' inválido ou não informado.");
            });

            experienceLocalService.delete(id);
            response.message = "Local deletado com sucesso";
        });
    }
}
