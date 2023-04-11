package me.universi.competencia.controller;

import me.universi.api.entities.Resposta;
import me.universi.competencia.entities.CompetenciaTipo;
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
    public Resposta create(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String nome = (String)body.get("nome");
            if(nome == null) {
                throw new CompetenciaException("Parametro nome é nulo.");
            }

            if(competenciaTipoService.findFirstByNome(nome) != null) {
                throw new CompetenciaException("Tipo de competência já existe.");
            }

            CompetenciaTipo competenciaNew = new CompetenciaTipo();
            competenciaNew.setNome(nome);

            competenciaTipoService.save(competenciaNew);

            resposta.mensagem = "Competência Criada";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta update(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciatipoId é nulo.");
            }

            String nome = (String)body.get("nome");

            CompetenciaTipo comp = competenciaTipoService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competência não encontrada.");
            }

            if(competenciaTipoService.findFirstByNome(nome) != null) {
                throw new CompetenciaException("Tipo de competência já existe.");
            }

            if(nome != null) {
                comp.setNome(nome);
            }

            competenciaTipoService.save(comp);

            resposta.mensagem = "Competência atualizada";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciatipoId é nulo.");
            }

            CompetenciaTipo comp = competenciaTipoService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competência não encontrada.");
            }

            competenciaTipoService.delete(comp);

            resposta.mensagem = "Competência removida";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("competenciatipoId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciatipoId é nulo.");
            }

            CompetenciaTipo comp = competenciaTipoService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competencia não encontrada.");
            }

            resposta.conteudo.put("competenciaTipo", comp);

            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/competenciatipo/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta getlist(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            List<CompetenciaTipo> comps = competenciaTipoService.findAll();

            resposta.conteudo.put("lista", comps);

            resposta.mensagem = "Operação realizada com exito.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }
}
