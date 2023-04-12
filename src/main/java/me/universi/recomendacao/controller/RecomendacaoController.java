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
import me.universi.usuario.entities.User;
import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import me.universi.recomendacao.service.RecomendacaoService;


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
            User user = usuarioService.obterUsuarioNaSessao();

            User userDestino = (User) usuarioService.loadUserByUsername(usernameDestino);
            map.put("usuarioDestino", userDestino);

            map.put("competenciaTipoService", competenciaTipoService);

            map.put("flagPage", "flagRecomendar");

        } catch (Exception e) {
            map.put("error", "Recomendação: " + e.getMessage());
        }
        return "recomendacao/recomendacao_index";
    }

    @PostMapping(value = "/recomendacao/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta create(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
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

            CompetenciaTipo compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
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

            resposta.mensagem = "A sua recomendação foi feita.";
            resposta.enderecoParaRedirecionar = "/p/" + perfilDestino.getUsuario().getUsername();
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta atualizar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
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
                CompetenciaTipo compT = competenciaTipoService.findFirstById(Long.valueOf(competenciaTipoId));
                if(compT == null) {
                    throw new RecomendacaoInvalidaException("Competencia não encontrada.");
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

    @PostMapping(value = "/recomendacao/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta remove(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
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

            resposta.mensagem = "Recomendação removida.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta get(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("id");
            if(id == null) {
                throw new GrupoException("Parametro id é nulo.");
            }

            Recomendacao recomendacao = recomendacaoService.findFirstById(Long.valueOf(id));
            if(recomendacao == null) {
                throw new GrupoException("Recomendação não encontrada.");
            }

            resposta.conteudo.put("recomendacao", recomendacao);

            resposta.mensagem = "Operação realizada com exito.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta getlist(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            List<Recomendacao> recs = recomendacaoService.findAll();

            resposta.conteudo.put("lista", recs);

            resposta.mensagem = "Operação realizada com exito.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }
}