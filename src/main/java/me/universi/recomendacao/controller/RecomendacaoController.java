package me.universi.recomendacao.controller;


import me.universi.api.entities.Response;
import me.universi.competencia.entities.CompetenceType;
import me.universi.competencia.services.CompetenceService;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.recomendacao.entities.Recomendacao;
import me.universi.recomendacao.exceptions.RecomendacaoInvalidaException;
import me.universi.user.entities.User;
import me.universi.user.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import me.universi.recomendacao.service.RecomendacaoService;


import java.util.List;
import java.util.Map;

@Controller
public class RecomendacaoController {
    @Autowired
    public RecomendacaoService recomendacaoService;

    @Autowired
    public CompetenceService competenciaService;

    @Autowired
    public UsuarioService usuarioService;

    @Autowired
    public PerfilService perfilService;

    @Autowired
    public CompetenciaTipoService competenciaTipoService;

    @PostMapping(value = "/recomendacao/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String origem = (String)body.get("origem");
            if(origem == null) {
                throw new RecomendacaoInvalidaException("Parametro origem é nulo.");
            }

            String destino = (String)body.get("destino");
            if(destino == null) {
                throw new RecomendacaoInvalidaException("Parametro destino é nulo.");
            }

            String competenciaTipoId = (String)body.get("competenciatipoId");
            if(competenciaTipoId == null) {
                throw new RecomendacaoInvalidaException("Parametro competenciaTipoId é nulo.");
            }

            Perfil perfilOrigem = perfilService.findFirstById(Long.valueOf(origem));
            if(perfilOrigem == null) {
                throw new RecomendacaoInvalidaException("Perfil origem não encontrado.");
            }

            Perfil perfilDestino = perfilService.findFirstById(Long.valueOf(destino));
            if(perfilDestino == null) {
                throw new RecomendacaoInvalidaException("Perfil destino não encontrado.");
            }

            if(perfilOrigem.getId() == perfilDestino.getId()) {
                throw new RecomendacaoInvalidaException("Você não pode recomendar-se.");
            }

            CompetenceType compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
            if(compT == null) {
                throw new RecomendacaoInvalidaException("Competencia não encontrada.");
            }

            String descricao = (String)body.get("descricao");

            Recomendacao recomendacoNew = new Recomendacao();
            recomendacoNew.setDestino(perfilDestino);
            recomendacoNew.setOrigem(perfilOrigem);
            recomendacoNew.setCompetenciaTipo(compT);

            if(descricao != null && descricao.length() > 0) {
                recomendacoNew.setDescricao(descricao);
            }

            recomendacaoService.save(recomendacoNew);

            resposta.message = "A sua recomendação foi feita.";
            resposta.redirectTo = "/p/" + perfilDestino.getUsuario().getUsername();
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response atualizar(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("id");
            if(id == null) {
                throw new GrupoException("Parametro id é nulo.");
            }

            String descricao = (String)body.get("descricao");

            String competenciaTipoId = (String)body.get("competenciatipoId");

            Recomendacao recomendacao = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recomendacao == null) {
                throw new GrupoException("Recomendação não encontrada.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            if(user.getPerfil().getId() != recomendacao.getOrigem().getId()) {
                throw new GrupoException("Você não tem permissão para editar esta Recomendação.");
            }

            if(descricao != null && descricao.length() > 0) {
                recomendacao.setDescricao(descricao);
            }

            if(competenciaTipoId != null && competenciaTipoId.length() > 0) {
                CompetenceType compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
                if(compT == null) {
                    throw new RecomendacaoInvalidaException("Competencia não encontrada.");
                }
                recomendacao.setCompetenciaTipo(compT);
            }

            recomendacaoService.update(recomendacao);

            resposta.message = "Recomendacao atualizada";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("id");
            if(id == null) {
                throw new GrupoException("Parametro id é nulo.");
            }

            Recomendacao recomendacao = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recomendacao == null) {
                throw new GrupoException("Recomendação não encontrada.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            if(user.getPerfil().getId() != recomendacao.getOrigem().getId()) {
                throw new GrupoException("Você não tem permissão para remover esta Recomendação.");
            }

            recomendacaoService.delete(recomendacao);

            resposta.message = "Recomendação removida.";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String id = (String)body.get("id");
            if(id == null) {
                throw new GrupoException("Parametro id é nulo.");
            }

            Recomendacao recomendacao = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recomendacao == null) {
                throw new GrupoException("Recomendação não encontrada.");
            }

            resposta.body.put("recomendacao", recomendacao);

            resposta.message = "Operação realizada com exito.";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getlist(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            List<Recomendacao> recs = recomendacaoService.findAll();

            resposta.body.put("lista", recs);

            resposta.message = "Operação realizada com exito.";
            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }
}