package me.universi.competencia.controller;

import java.util.List;
import java.util.Map;

import me.universi.api.entities.Resposta;
import me.universi.competencia.entities.Competencia;
import me.universi.competencia.entities.CompetenciaTipo;
import me.universi.competencia.enums.Nivel;
import me.universi.competencia.exceptions.CompetenciaException;
import me.universi.competencia.services.CompetenciaService;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.usuario.entities.Usuario;
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
    public Resposta create(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

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
            competenciaNew.setPerfil(usuario.getPerfil());
            competenciaNew.setCompetenciaTipo(compT);
            competenciaNew.setDescricao(descricao);
            competenciaNew.setNivel(Nivel.valueOf(nivel));

            competenciaService.save(competenciaNew);

            resposta.mensagem = "Competência Criada";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta update(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
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

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            Perfil perfil = usuario.getPerfil();

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

            resposta.mensagem = "Competência atualizada";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta remove(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciaId é nulo.");
            }

            Competencia comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competência não encontrada.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            Perfil perfil = usuario.getPerfil();

            if(comp.getPerfil().getId() != perfil.getId()) {
                throw new CompetenciaException("Você não tem permissão para editar esta Competêcia.");
            }

            competenciaService.delete(comp);

            resposta.mensagem = "Competência removida";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta get(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciaId é nulo.");
            }

            Competencia comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competencia não encontrada.");
            }

            resposta.conteudo.put("competencia", comp);

            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta getlist(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            List<Competencia> comps = competenciaService.findAll();

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
