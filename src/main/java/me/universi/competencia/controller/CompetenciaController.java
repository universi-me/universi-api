package me.universi.competencia.controller;

import java.util.List;
import java.util.Map;

import me.universi.api.entities.Response;
import me.universi.competencia.entities.Competencia;
import me.universi.competencia.entities.CompetenciaTipo;
import me.universi.competencia.enums.Nivel;
import me.universi.competencia.exceptions.CompetenciaException;
import me.universi.competencia.services.CompetenciaService;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.usuario.entities.User;
import me.universi.usuario.services.UsuarioService;
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
                throw new CompetenciaException("Parametro competenciatipoId é nulo.");
            }

            String descricao = (String)body.get("descricao");
            if(descricao == null) {
                throw new CompetenciaException("Parametro descricao é nulo.");
            }

            String nivel = (String)body.get("nivel");
            if(nivel == null) {
                throw new CompetenciaException("Parametro nivel é nulo.");
            }

            CompetenciaTipo compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
            if(compT == null) {
                throw new CompetenciaException("Tipo de Competência não encontrado.");
            }

            Competencia competenciaNew = new Competencia();
            competenciaNew.setPerfil(user.getPerfil());
            competenciaNew.setCompetenciaTipo(compT);
            competenciaNew.setDescricao(descricao);
            competenciaNew.setNivel(Nivel.valueOf(nivel));

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
                throw new CompetenciaException("Parametro competenciaId é nulo.");
            }

            String competenciaTipoId = (String)body.get("competenciaTipoId");
            String descricao = (String)body.get("descricao");
            String nivel = (String)body.get("nivel");



            Competencia comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competência não encontrada.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            Perfil perfil = user.getPerfil();

            if(comp.getPerfil().getId() != perfil.getId()) {
                throw new CompetenciaException("Você não tem permissão para editar esta Competêcia.");
            }

            if(competenciaTipoId != null && competenciaTipoId.length()>0) {
                CompetenciaTipo compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
                if(compT == null) {
                    throw new CompetenciaException("Tipo de Competência não encontrado.");
                }
                comp.setCompetenciaTipo(compT);
            }
            if (descricao != null) {
                comp.setDescricao(descricao);
            }
            if (nivel != null) {
                comp.setNivel(Nivel.valueOf(nivel));
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
                throw new CompetenciaException("Parametro competenciaId é nulo.");
            }

            Competencia comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competência não encontrada.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            Perfil perfil = user.getPerfil();

            if(comp.getPerfil().getId() != perfil.getId()) {
                throw new CompetenciaException("Você não tem permissão para editar esta Competêcia.");
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
                throw new CompetenciaException("Parametro competenciaId é nulo.");
            }

            Competencia comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competencia não encontrada.");
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

            List<Competencia> comps = competenciaService.findAll();

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
