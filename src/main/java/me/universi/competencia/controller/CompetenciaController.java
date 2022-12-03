package me.universi.competencia.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.universi.api.entities.Resposta;
import me.universi.competencia.entities.Competencia;
import me.universi.competencia.enums.Nivel;
import me.universi.competencia.exceptions.CompetenciaException;
import me.universi.competencia.services.CompetenciaService;
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
    private PerfilService perfilService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping(value = "/competencia/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object create(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Usuario usuario = (Usuario) session.getAttribute("usuario");

            String nome = (String)body.get("nome");
            if(nome == null) {
                throw new CompetenciaException("Parametro nome é nulo.");
            }

            String descricao = (String)body.get("descricao");
            if(descricao == null) {
                throw new CompetenciaException("Parametro descricao é nulo.");
            }

            String nivel = (String)body.get("nivel");
            if(nivel == null) {
                throw new CompetenciaException("Parametro nivel é nulo.");
            }

            Competencia competenciaNew = new Competencia(); // nova competência
            competenciaNew.setNome(nome);
            competenciaNew.setDescricao(descricao);
            competenciaNew.setNivel(Nivel.valueOf(nivel));

            competenciaService.save(competenciaNew);

            Perfil perfil = usuario.getPerfil();
            perfilService.adicionarCompetencia(perfil, competenciaNew);

            resposta.mensagem = "Competencia Criada";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object update(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenciaException("Parametro competenciaId é nulo.");
            }

            String nome = (String)body.get("nome");
            String descricao = (String)body.get("descricao");
            String nivel = (String)body.get("nivel");

            Competencia comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competencia não encontrada.");
            }

            Usuario usuario = (Usuario) session.getAttribute("usuario");

            Perfil perfil = usuario.getPerfil();

            if(perfil.getCompetencias() == null || !perfil.getCompetencias().contains(comp)) {
                throw new CompetenciaException("Você não tem permissão para editar esta Competêcia.");
            }

            if(nome != null) {
                comp.setNome(nome);
            }
            if (descricao != null) {
                comp.setDescricao(descricao);
            }
            if (nivel != null) {
                comp.setNivel(Nivel.valueOf(nivel));
            }

            competenciaService.save(comp);

            resposta.mensagem = "Competencia atualizada";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/remover", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
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

            Usuario usuario = (Usuario) session.getAttribute("usuario");

            Perfil perfil = usuario.getPerfil();

            if(perfil.getCompetencias() == null || !perfil.getCompetencias().contains(comp)) {
                throw new CompetenciaException("Você não tem permissão para editar esta Competêcia.");
            }

            //competenciaService.delete(comp);

            perfilService.removerCompetencia(perfil, comp);

            resposta.mensagem = "Competencia removida";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/obter", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
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

    @PostMapping(value = "/competencia/listar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getlist(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
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
