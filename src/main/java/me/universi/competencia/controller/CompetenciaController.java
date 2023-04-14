package me.universi.competencia.controller;

import java.util.List;
import java.util.Map;

import me.universi.api.entities.Response;
import me.universi.competencia.entities.Competence;
import me.universi.competencia.entities.CompetenceType;
import me.universi.competencia.enums.Level;
import me.universi.competencia.exceptions.CompetenceException;
import me.universi.competencia.services.CompetenciaService;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CompetenciaController {
    @Autowired
    public CompetenciaService competenciaService;
    @Autowired
    public CompetenciaTipoService competenciaTipoService;

    @Autowired
    public PerfilService perfilService;

    @Autowired
    public UsuarioService usuarioService;

    @PostMapping(value = "/competencia/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            User user = usuarioService.obterUsuarioNaSessao();

            String competenciaTipoId = (String)body.get("competenciatipoId");
            if(competenciaTipoId == null) {
                throw new CompetenceException("Parametro competenciatipoId é nulo.");
            }

            String descricao = (String)body.get("descricao");
            if(descricao == null) {
                throw new CompetenceException("Parametro descricao é nulo.");
            }

            String nivel = (String)body.get("nivel");
            if(nivel == null) {
                throw new CompetenceException("Parametro nivel é nulo.");
            }

            CompetenceType compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
            if(compT == null) {
                throw new CompetenceException("Tipo de Competência não encontrado.");
            }

            Competence competenciaNew = new Competence();
            competenciaNew.setProfile(user.getPerfil());
            competenciaNew.setCompetenceType(compT);
            competenciaNew.setDescription(descricao);
            competenciaNew.setLevel(Level.valueOf(nivel));

            competenciaService.save(competenciaNew);

            resposta.message = "Competência Criada";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            String competenciaTipoId = (String)body.get("competenciaTipoId");
            String descricao = (String)body.get("descricao");
            String nivel = (String)body.get("nivel");



            Competence comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            Perfil perfil = user.getPerfil();

            if(comp.getProfile().getId() != perfil.getId()) {
                throw new CompetenceException("Você não tem permissão para editar esta Competêcia.");
            }

            if(competenciaTipoId != null && competenciaTipoId.length()>0) {
                CompetenceType compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
                if(compT == null) {
                    throw new CompetenceException("Tipo de Competência não encontrado.");
                }
                comp.setCompetenceType(compT);
            }
            if (descricao != null) {
                comp.setDescription(descricao);
            }
            if (nivel != null) {
                comp.setLevel(Level.valueOf(nivel));
            }

            competenciaService.save(comp);

            resposta.message = "Competência atualizada";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            Competence comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            Perfil perfil = user.getPerfil();

            if(comp.getProfile().getId() != perfil.getId()) {
                throw new CompetenceException("Você não tem permissão para editar esta Competêcia.");
            }

            competenciaService.delete(comp);

            resposta.message = "Competência removida";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            Competence comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenceException("Competencia não encontrada.");
            }

            resposta.body.put("competencia", comp);

            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getlist(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            List<Competence> comps = competenciaService.findAll();

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
