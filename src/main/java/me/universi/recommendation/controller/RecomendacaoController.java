package me.universi.recommendation.controller;


import me.universi.api.entities.Response;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.exceptions.GroupException;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.recommendation.entities.Recommendation;
import me.universi.recommendation.exceptions.RecomendacaoInvalidaException;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import me.universi.recommendation.service.RecomendacaoService;


import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class RecomendacaoController {
    @Autowired
    public RecomendacaoService recomendacaoService;

    @Autowired
    public CompetenceService competenciaService;

    @Autowired
    public UserService userService;

    @Autowired
    public ProfileService profileService;

    @Autowired
    public CompetenceTypeService competenciaTipoService;

    @PostMapping(value = "/recomendacao/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String destino = (String)body.get("destino");
            if(destino == null) {
                throw new RecomendacaoInvalidaException("Parametro destino é nulo.");
            }

            String competenciaTipoId = (String)body.get("competenciatipoId");
            if(competenciaTipoId == null) {
                throw new RecomendacaoInvalidaException("Parametro competenciaTipoId é nulo.");
            }

            Profile profileDestiny = profileService.findFirstById(destino);
            if(profileDestiny == null) {
                throw new RecomendacaoInvalidaException("Perfil destino não encontrado.");
            }

            Profile profileOrigin = userService.getUserInSession().getProfile();

            if(userService.isSessionOfUser(profileDestiny.getUser())) {
                throw new RecomendacaoInvalidaException("Você não pode recomendar-se.");
            }

            CompetenceType compT = competenciaTipoService.findFirstById(competenciaTipoId);
            if(compT == null) {
                throw new RecomendacaoInvalidaException("Competencia não encontrada.");
            }

            String descricao = (String)body.get("descricao");

            Recommendation recomendacoNew = new Recommendation();
            recomendacoNew.setDestiny(profileDestiny);
            recomendacoNew.setOrigin(profileOrigin);
            recomendacoNew.setCompetenceType(compT);

            if(descricao != null && descricao.length() > 0) {
                recomendacoNew.setDescription(descricao);
            }

            recomendacaoService.save(recomendacoNew);

            resposta.message = "A sua recomendação foi feita.";
            resposta.redirectTo = "/p/" + profileDestiny.getUser().getUsername();
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

            Recommendation recommendation = recomendacaoService.findFirstById(id);
            if(recommendation == null) {
                throw new GroupException("Recomendação não encontrada.");
            }

            if(!userService.isSessionOfUser(recommendation.getOrigin().getUser())) {
                throw new GroupException("Você não tem permissão para editar esta Recomendação.");
            }

            if(descricao != null && descricao.length() > 0) {
                recommendation.setDescription(descricao);
            }

            if(competenciaTipoId != null && competenciaTipoId.length() > 0) {
                CompetenceType compT = competenciaTipoService.findFirstById(competenciaTipoId);
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

            Recommendation recommendation = recomendacaoService.findFirstById(id);
            if(recommendation == null) {
                throw new GroupException("Recomendação não encontrada.");
            }

            if(!userService.isSessionOfUser(recommendation.getOrigin().getUser())) {
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

            Recommendation recommendation = recomendacaoService.findFirstById(id);
            if(recommendation == null) {
                throw new GroupException("Recomendação não encontrada.");
            }

            resposta.body.put("recomendacao", recommendation);

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

            resposta.success = true;
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }
}