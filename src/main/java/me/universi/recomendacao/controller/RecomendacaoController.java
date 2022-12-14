package me.universi.recomendacao.controller;


import me.universi.api.entities.Resposta;
import me.universi.competencia.entities.CompetenciaTipo;
import me.universi.competencia.services.CompetenciaService;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.recomendacao.entities.Recomendacao;
import me.universi.recomendacao.exceptions.RecomendacaoInvalidaException;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import me.universi.recomendacao.service.RecomendacaoService;


import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class RecomendacaoController {
    @Autowired
    public RecomendacaoService recomendacaoService;

    @Autowired
    public CompetenciaService competenciaService;

    @Autowired
    public UsuarioService usuarioService;

    @Autowired
    public PerfilService perfilService;

    @Autowired
    public CompetenciaTipoService competenciaTipoService;

    @GetMapping("/recomendar")
    public String recomendacao(ModelMap map) {
        return "recomendacao/recomendacao_index";
    }

    @GetMapping("/recomendar/{usuario}")
    public String recomendar_usuario(ModelMap map, @PathVariable("usuario") String usernameDestino) {
        try {
            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            Usuario usuarioDestino = (Usuario) usuarioService.loadUserByUsername(usernameDestino);
            map.put("usuarioDestino", usuarioDestino);

            map.put("competenciaTipoService", competenciaTipoService);

            map.put("flagPage", "flagRecomendar");

        } catch (Exception e) {
            map.put("error", "Recomenda????o: " + e.getMessage());
        }
        return "recomendacao/recomendacao_index";
    }

    @PostMapping(value = "/recomendacao/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object create(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String origem = (String)body.get("origem");
            if(origem == null) {
                throw new RecomendacaoInvalidaException("Parametro origem ?? nulo.");
            }

            String destino = (String)body.get("destino");
            if(destino == null) {
                throw new RecomendacaoInvalidaException("Parametro destino ?? nulo.");
            }

            String competenciaTipoId = (String)body.get("competenciatipoId");
            if(competenciaTipoId == null) {
                throw new RecomendacaoInvalidaException("Parametro competenciaTipoId ?? nulo.");
            }

            Perfil perfilOrigem = perfilService.findFirstById(Long.valueOf(origem));
            if(perfilOrigem == null) {
                throw new RecomendacaoInvalidaException("Perfil origem n??o encontrado.");
            }

            Perfil perfilDestino = perfilService.findFirstById(Long.valueOf(destino));
            if(perfilDestino == null) {
                throw new RecomendacaoInvalidaException("Perfil destino n??o encontrado.");
            }

            if(perfilOrigem.getId() == perfilDestino.getId()) {
                throw new RecomendacaoInvalidaException("Voc?? n??o pode recomendar-se.");
            }

            CompetenciaTipo compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
            if(compT == null) {
                throw new RecomendacaoInvalidaException("Competencia n??o encontrada.");
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

            resposta.mensagem = "A sua recomenda????o foi feita.";
            resposta.enderecoParaRedirecionar = "/p/" + perfilDestino.getUsuario().getUsername();
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object atualizar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("id");
            if(id == null) {
                throw new GrupoException("Parametro id ?? nulo.");
            }

            String descricao = (String)body.get("descricao");

            String competenciaTipoId = (String)body.get("competenciatipoId");

            Recomendacao recomendacao = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recomendacao == null) {
                throw new GrupoException("Recomenda????o n??o encontrada.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            if(usuario.getPerfil().getId() != recomendacao.getOrigem().getId()) {
                throw new GrupoException("Voc?? n??o tem permiss??o para editar esta Recomenda????o.");
            }

            if(descricao != null && descricao.length() > 0) {
                recomendacao.setDescricao(descricao);
            }

            if(competenciaTipoId != null && competenciaTipoId.length() > 0) {
                CompetenciaTipo compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
                if(compT == null) {
                    throw new RecomendacaoInvalidaException("Competencia n??o encontrada.");
                }
                recomendacao.setCompetenciaTipo(compT);
            }

            recomendacaoService.update(recomendacao);

            resposta.mensagem = "Recomendacao atualizada";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/remover", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object remove(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("id");
            if(id == null) {
                throw new GrupoException("Parametro id ?? nulo.");
            }

            Recomendacao recomendacao = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recomendacao == null) {
                throw new GrupoException("Recomenda????o n??o encontrada.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            if(usuario.getPerfil().getId() != recomendacao.getOrigem().getId()) {
                throw new GrupoException("Voc?? n??o tem permiss??o para remover esta Recomenda????o.");
            }

            recomendacaoService.delete(recomendacao);

            resposta.mensagem = "Recomenda????o removida.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/obter", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object get(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("id");
            if(id == null) {
                throw new GrupoException("Parametro id ?? nulo.");
            }

            Recomendacao recomendacao = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recomendacao == null) {
                throw new GrupoException("Recomenda????o n??o encontrada.");
            }

            resposta.conteudo.put("recomendacao", recomendacao);

            resposta.mensagem = "Opera????o realizada com exito.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/listar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getlist(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            List<Recomendacao> recs = recomendacaoService.findAll();

            resposta.conteudo.put("lista", recs);

            resposta.mensagem = "Opera????o realizada com exito.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }
}