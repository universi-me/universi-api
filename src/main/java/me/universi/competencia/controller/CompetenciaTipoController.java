package me.universi.competencia.controller;

import me.universi.api.entities.Response;
import me.universi.competencia.entities.CompetenceType;
import me.universi.competencia.exceptions.CompetenciaException;
import me.universi.competencia.services.CompetenciaTipoService;
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
public class CompetenciaTipoController {
    @Autowired
    public CompetenciaTipoService competenciaTipoService;

    @PostMapping(value = "/admin/competenciatipo/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response resposta = new Response();
        try {

            String nome = (String)body.get("nome");
            if(nome == null) {
                throw new CompetenciaException("Parametro nome é nulo.");
            }

            if(competenciaTipoService.findFirstByNome(nome) != null) {
                throw new CompetenciaException("Tipo de competência já existe.");
            }

            CompetenceType competenciaNew = new CompetenceType();
            competenciaNew.setName(nome);

            competenciaTipoService.save(competenciaNew);

            resposta.message = "Competência Criada";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciatipoId é nulo.");
            }

            String nome = (String)body.get("nome");

            CompetenceType comp = competenciaTipoService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competência não encontrada.");
            }

            if(competenciaTipoService.findFirstByNome(nome) != null) {
                throw new CompetenciaException("Tipo de competência já existe.");
            }

            if(nome != null) {
                comp.setName(nome);
            }

            competenciaTipoService.save(comp);

            resposta.message = "Competência atualizada";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciatipoId é nulo.");
            }

            CompetenceType comp = competenciaTipoService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competência não encontrada.");
            }

            competenciaTipoService.delete(comp);

            resposta.message = "Competência removida";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciatipoId é nulo.");
            }

            CompetenceType comp = competenciaTipoService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competencia não encontrada.");
            }

            resposta.body.put("competenciaTipo", comp);

            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getlist(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response resposta = new Response();
        try {

            List<CompetenceType> comps = competenciaTipoService.findAll();

            resposta.body.put("lista", comps);

            resposta.message = "Operação realizada com exito.";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }
}
