package me.universi.competence.controller;

import me.universi.api.entities.Response;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.services.CompetenceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompetenceTypeController {
    @Autowired
    public CompetenceTypeService competenceTypeService;

    @PostMapping(value = "/admin/competencetype/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String name = (String)body.get("name");
            if(name == null) {
                throw new CompetenceException("Parâmetro nome é nulo.");
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

    @PostMapping(value = "/admin/competencetype/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            String name = (String)body.get("name");

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

    @PostMapping(value = "/admin/competencetype/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
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

    @PostMapping(value = "/competencetype/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            CompetenceType competenceType = competenceTypeService.findFirstById(Long.valueOf(id));
            if (competenceType == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            response.body.put("competenceType", competenceType);

            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/competencetype/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            List<CompetenceType> competences = competenceTypeService.findAll();

            response.body.put("list", competences);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }
}
