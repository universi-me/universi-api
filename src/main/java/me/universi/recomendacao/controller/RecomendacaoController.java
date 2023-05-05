package me.universi.recomendacao.controller;


import me.universi.api.entities.Response;
import me.universi.competencia.entities.CompetenceType;
import me.universi.competencia.services.CompetenceService;
import me.universi.competencia.services.CompetenceTypeService;
import me.universi.grupo.exceptions.GroupException;
import me.universi.perfil.entities.Profile;
import me.universi.perfil.services.PerfilService;
import me.universi.recomendacao.entities.Recommendation;
import me.universi.recomendacao.exceptions.RecomendacaoInvalidaException;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
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
    public UserService userService;

    @Autowired
    public PerfilService perfilService;

    @Autowired
    public CompetenceTypeService competenciaTipoService;

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

            Profile profileOrigem = perfilService.findFirstById(Long.valueOf(origem));
            if(profileOrigem == null) {
                throw new RecomendacaoInvalidaException("Perfil origem não encontrado.");
            }

            Profile profileDestino = perfilService.findFirstById(Long.valueOf(destino));
            if(profileDestino == null) {
                throw new RecomendacaoInvalidaException("Perfil destino não encontrado.");
            }

            if(profileOrigem.getId() == profileDestino.getId()) {
                throw new RecomendacaoInvalidaException("Você não pode recomendar-se.");
            }

            CompetenceType compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
            if(compT == null) {
                throw new RecomendacaoInvalidaException("Competencia não encontrada.");
            }

            String descricao = (String)body.get("descricao");

            Recommendation recomendacoNew = new Recommendation();
            recomendacoNew.setDestiny(profileDestino);
            recomendacoNew.setOrigin(profileOrigem);
            recomendacoNew.setCompetenceType(compT);

            if(descricao != null && descricao.length() > 0) {
                recomendacoNew.setDescription(descricao);
            }

            recomendacaoService.save(recomendacoNew);

            resposta.message = "A sua recomendação foi feita.";
            resposta.redirectTo = "/p/" + profileDestino.getUsuario().getUsername();
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
                throw new GroupException("Parametro id é nulo.");
            }

            String descricao = (String)body.get("descricao");

            String competenciaTipoId = (String)body.get("competenciatipoId");

            Recommendation recommendation = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recommendation == null) {
                throw new GroupException("Recomendação não encontrada.");
            }

            User user = userService.obterUsuarioNaSessao();

            if(user.getProfile().getId() != recommendation.getOrigin().getId()) {
                throw new GroupException("Você não tem permissão para editar esta Recomendação.");
            }

            if(descricao != null && descricao.length() > 0) {
                recommendation.setDescription(descricao);
            }

            if(competenciaTipoId != null && competenciaTipoId.length() > 0) {
                CompetenceType compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
                if(compT == null) {
                    throw new RecomendacaoInvalidaException("Competencia não encontrada.");
                }
                recommendation.setCompetenceType(compT);
            }

            recomendacaoService.update(recommendation);

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
                throw new GroupException("Parametro id é nulo.");
            }

            Recommendation recommendation = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recommendation == null) {
                throw new GroupException("Recomendação não encontrada.");
            }

            User user = userService.obterUsuarioNaSessao();

            if(user.getProfile().getId() != recommendation.getOrigin().getId()) {
                throw new GroupException("Você não tem permissão para remover esta Recomendação.");
            }

            recomendacaoService.delete(recommendation);

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
                throw new GroupException("Parametro id é nulo.");
            }

            Recommendation recommendation = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recommendation == null) {
                throw new GroupException("Recomendação não encontrada.");
            }

            resposta.body.put("recomendacao", recommendation);

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

            List<Recommendation> recs = recomendacaoService.findAll();

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