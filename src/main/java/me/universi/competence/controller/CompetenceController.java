package me.universi.competence.controller;

import java.util.List;
import java.util.Map;

import me.universi.api.entities.Response;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.enums.Level;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CompetenceController {
    @Autowired
    public CompetenceService competenceService;
    @Autowired
    public CompetenceTypeService competenceTypeService;

    @Autowired
    public PerfilService profileService;

    @Autowired
    public UserService userService;

    @PostMapping(value = "/competencia/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            User user = userService.obterUsuarioNaSessao();

            String competenceTypeId = (String)body.get("competenciatipoId");
            if(competenceTypeId == null) {
                throw new CompetenceException("Parametro competenciatipoId é nulo.");
            }

            String description = (String)body.get("descricao");
            if(description == null) {
                throw new CompetenceException("Parametro descricao é nulo.");
            }

            String level = (String)body.get("nivel");
            if(level == null) {
                throw new CompetenceException("Parametro nivel é nulo.");
            }

            CompetenceType compT = competenceTypeService.findFirstById(Long.valueOf(competenceTypeId));
            if(compT == null) {
                throw new CompetenceException("Tipo de Competência não encontrado.");
            }

            Competence newCompetence = new Competence();
            newCompetence.setProfile(user.getProfile());
            newCompetence.setCompetenceType(compT);
            newCompetence.setDescription(description);
            newCompetence.setLevel(Level.valueOf(level));

            competenceService.save(newCompetence);

            response.message = "Competência Criada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/competencia/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            String competenceTypeId = (String)body.get("competenciaTipoId");
            String description = (String)body.get("descricao");
            String level = (String)body.get("nivel");



            Competence competence = competenceService.findFirstById(Long.valueOf(id));
            if (competence == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            User user = userService.obterUsuarioNaSessao();

            Profile profile = user.getProfile();

            if(competence.getProfile().getId() != profile.getId()) {
                throw new CompetenceException("Você não tem permissão para editar esta Competêcia.");
            }

            if(competenceTypeId != null && competenceTypeId.length()>0) {
                CompetenceType compT = competenceTypeService.findFirstById(Long.valueOf(competenceTypeId));
                if(compT == null) {
                    throw new CompetenceException("Tipo de Competência não encontrado.");
                }
                competence.setCompetenceType(compT);
            }
            if (description != null) {
                competence.setDescription(description);
            }
            if (level != null) {
                competence.setLevel(Level.valueOf(level));
            }

            competenceService.save(competence);

            response.message = "Competência atualizada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/competencia/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            Competence competence = competenceService.findFirstById(Long.valueOf(id));
            if (competence == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            User user = userService.obterUsuarioNaSessao();

            Profile profile = user.getProfile();

            if(competence.getProfile().getId() != profile.getId()) {
                throw new CompetenceException("Você não tem permissão para editar esta Competêcia.");
            }

            competenceService.delete(competence);

            response.message = "Competência removida";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/competencia/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            Competence competence = competenceService.findFirstById(Long.valueOf(id));
            if (competence == null) {
                throw new CompetenceException("Competencia não encontrada.");
            }

            response.body.put("competencia", competence);

            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/competencia/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            List<Competence> competences = competenceService.findAll();

            response.body.put("lista", competences);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

}
