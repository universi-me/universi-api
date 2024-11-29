package me.universi.competence.controller;

import me.universi.api.entities.Response;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.util.CastingUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompetenceTypeController {
    private final CompetenceTypeService competenceTypeService;

    public CompetenceTypeController(CompetenceTypeService competenceTypeService) {
        this.competenceTypeService = competenceTypeService;
    }

    @PostMapping(value = "/competencetype/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response create(@RequestBody Map<String, Object> body) {
       return Response.buildResponse(response -> {

            var name = CastingUtil.getString(body.get("name"));
            if(name.isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                throw new CompetenceException("Parâmetro nome é nulo.");
            }

            var competenceType = new CompetenceType();
            competenceType.setName(name.get());
            competenceType = competenceTypeService.create(competenceType);

            response.message = "Tipo de Competência Criada";
            response.body.put("competenceType", competenceType);
            response.setStatus(HttpStatus.CREATED);
        });
    }

    @PostMapping(value = "/admin/competencetype/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response update(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            var id = CastingUtil.getUUID(body.get("competenceTypeId"));
            if(id.isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            var name = CastingUtil.getString(body.get("name"));
            var reviewed = CastingUtil.getBoolean(body.get("reviewed"));

            var updateTo = new CompetenceType();
            updateTo.setName(name.orElse(null));
            updateTo.setReviewed(reviewed.orElse(false));

            competenceTypeService.update(updateTo, id.get());

            response.message = "Tipo de Competência atualizada";
        });
    }

    @PostMapping(value = "/admin/competencetype/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response remove(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            var id = CastingUtil.getUUID(body.get("competenceTypeId"));
            if(id.isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            CompetenceType competenceType = competenceTypeService.findFirstById(id.get());
            if (competenceType == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                throw new CompetenceException("Competência não encontrada.");
            }

            competenceTypeService.delete(competenceType);

            response.message = "Tipo de Competência removida";
        });
    }

    @PostMapping(value = "/competencetype/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            var id = CastingUtil.getUUID(body.get("competenceTypeId"));
            if(id.isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            CompetenceType competenceType = competenceTypeService.findFirstById(id.get());
            if (competenceType == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                throw new CompetenceException("Tipo de Competência não encontrada.");
            }

            response.body.put("competenceType", competenceType);
        });
    }

    @PostMapping(value = "/competencetype/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response findAll(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("list", competenceTypeService.findAll());
        });
    }

    @PostMapping(value = "/admin/competencetype/merge", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response merge(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            var removedCompetenceTypeUuid = CastingUtil
                .getUUID(body.get("removedCompetenceTypeId"))
                .orElseThrow(() -> {
                    response.setStatus(HttpStatus.BAD_REQUEST);
                    return new CompetenceException("Parâmetro removedCompetenceType é nulo");
                });

            var remainingCompetenceTypeUuid = CastingUtil
                .getUUID(body.get("remainingCompetenceTypeId"))
                .orElseThrow(() -> {
                    response.setStatus(HttpStatus.BAD_REQUEST);
                    return new CompetenceException("Parâmetro remainingCompetenceType é nulo");
                });

            var removedCompetenceType = competenceTypeService.findFirstById(removedCompetenceTypeUuid);
            var remainingCompetenceType = competenceTypeService.findFirstById(remainingCompetenceTypeUuid);

            if (removedCompetenceType == null || remainingCompetenceType == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                throw new CompetenceException("Tipo de competência não encontrada.");
            }

            competenceTypeService.merge(removedCompetenceType, remainingCompetenceType);
            response.message = "Tipo de Competências foram fundidas";
        });
    }
}
