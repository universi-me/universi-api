package me.universi.competence.controller;

import me.universi.api.entities.Response;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.services.CompetenceService;
import me.universi.profile.services.ProfileService;
import me.universi.util.CastingUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/competencia")
public class CompetenceController {
    private final CompetenceService competenceService;

    public CompetenceController(CompetenceService competenceService) {
        this.competenceService = competenceService;
    }

    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var competenceTypeId = CastingUtil.getUUID(body.get("competenciatipoId")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new CompetenceException("Parâmetro 'competenceiatipoId' não informado ou inválido.");
            });

            var description = CastingUtil.getString(body.get("descricao")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new CompetenceException("Parâmetro 'descricao' não informado ou inválido.");
            });

            var level = CastingUtil.getInteger(body.get("nivel")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new CompetenceException("Parâmetro 'nivel' não informado ou inválido.");
            });

            competenceService.create(competenceTypeId, description, level, ProfileService.getInstance().getProfileInSession());
            response.message = "Competência Criada e adicionado ao perfil";
        });
    }

    @PostMapping(value = "/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        return competenceService.update(body);
    }

    @PostMapping(value = "/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        return competenceService.remove(body);
    }

    @PostMapping(value = "/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        return competenceService.get(body);
    }

    @PostMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        return competenceService.findAll(body);
    }

}
