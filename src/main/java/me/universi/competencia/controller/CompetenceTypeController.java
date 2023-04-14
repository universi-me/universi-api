package me.universi.competencia.controller;

import me.universi.api.entities.Response;
import me.universi.competencia.entities.CompetenceType;
import me.universi.competencia.exceptions.CompetenceException;
import me.universi.competencia.services.CompetenceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class CompetenceTypeController {
    @Autowired
    public CompetenceTypeService competenceTypeService;

    @PostMapping(value = "/admin/competenciatipo/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String name = (String)body.get("nome");
            if(name == null) {
                throw new CompetenceException("Parametro nome é nulo.");
            }

            if(competenceTypeService.findFirstByName(name) != null) {
                throw new CompetenceException("Tipo de competência já existe.");
            }

            CompetenceType newCompetence = new CompetenceType();
            newCompetence.setName(name);

            competenceTypeService.save(newCompetence);

            response.message = "Competência Criada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciatipoId é nulo.");
            }

            String name = (String)body.get("nome");

            CompetenceType competenceType = competenceTypeService.findFirstById(Long.valueOf(id));
            if (competenceType == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            if(competenceTypeService.findFirstByName(name) != null) {
                throw new CompetenceException("Tipo de competência já existe.");
            }

            if(name != null) {
                competenceType.setName(name);
            }

            competenceTypeService.save(competenceType);

            response.message = "Competência atualizada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciatipoId é nulo.");
            }

            CompetenceType competenceType = competenceTypeService.findFirstById(Long.valueOf(id));
            if (competenceType == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            competenceTypeService.delete(competenceType);

            response.message = "Competência removida";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciatipoId é nulo.");
            }

            CompetenceType competenceType = competenceTypeService.findFirstById(Long.valueOf(id));
            if (competenceType == null) {
                throw new CompetenceException("Competencia não encontrada.");
            }

            response.body.put("competenciaTipo", competenceType);

            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            List<CompetenceType> competences = competenceTypeService.findAll();

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
