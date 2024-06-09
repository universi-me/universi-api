package me.universi.institution.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.universi.api.entities.Response;
import me.universi.institution.exceptions.InstitutionException;
import me.universi.institution.services.InstitutionService;
import me.universi.util.CastingUtil;

@RestController
@RequestMapping(value = "/api/institution")
public class InstitutionController {
    private InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response listAll(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("list", institutionService.findAll());
        });
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var name = CastingUtil.getString(body.get("name")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new InstitutionException("Parâmetro 'name' inválido ou não informado.");
            });

            var institution = institutionService.create(name);

            response.setStatus(HttpStatus.CREATED);
            response.message = "Instituição '" + name + "' criado com sucesso";
            response.body.put("institution", institution);
        });
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var id = CastingUtil.getUUID(body.get("id")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new InstitutionException("Parâmetro 'id' inválido ou não informado.");
            });

            var name = CastingUtil.getString(body.get("name")).orElse(null);

            var institution = institutionService.edit(id, name);

            response.setStatus(HttpStatus.CREATED);
            response.message = "Instituição '" + name + "' alterado com sucesso";
            response.body.put("institution", institution);
        });
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response delete(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var id = CastingUtil.getUUID(body.get("id")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new InstitutionException("Parâmetro 'id' inválido ou não informado.");
            });

            institutionService.delete(id);
            response.message = "Instituição deletado com sucesso";
        });
    }
}
