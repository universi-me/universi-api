package me.universi.recomendacao.controller;


import me.universi.api.entities.Resposta;
import me.universi.competencia.entities.Competencia;
import me.universi.competencia.services.CompetenciaService;
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
    private UsuarioService usuarioService;

    @Autowired
    private PerfilService perfilService;

    @GetMapping("/recomendar")
    public String recomendacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map) {
        return "recomendacao/recomendacao_index";
    }

    @GetMapping("/recomendar/{usuario}")
    public String recomendar_usuario(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map, @PathVariable("usuario") String usernameDestino) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            Usuario usuarioDestino = (Usuario) usuarioService.loadUserByUsername(usernameDestino);

            map.put("usuarioDestino", usuarioDestino);

            map.put("flagPage", "flagRecomendar");

        } catch (Exception e) {
            map.put("error", "Recomendação: " + e.getMessage());
        }
        return "recomendacao/recomendacao_index";
    }

    @PostMapping(value = "/recomendacao/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object create(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
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

            String descricao = (String)body.get("descricao");
            if(descricao == null) {
                throw new RecomendacaoInvalidaException("Parametro descricao é nulo.");
            }

            String competencia = (String)body.get("competenciaId");

            Perfil perfilOrigem = perfilService.findFirstById(Long.valueOf(origem));
            if(perfilOrigem == null) {
                throw new RecomendacaoInvalidaException("Perfil origem não encontrado.");
            }

            Perfil perfilDestino = perfilService.findFirstById(Long.valueOf(destino));
            if(perfilDestino == null) {
                throw new RecomendacaoInvalidaException("Perfil destino não encontrado.");
            }

            Competencia comp = null;
            if(competencia != null) {
                comp = competenciaService.findFirstById(Long.valueOf(competencia));
                if(comp == null) {
                    throw new RecomendacaoInvalidaException("Competencia não encontrada.");
                }
            }

            Recomendacao recomendacoNew = new Recomendacao();

            recomendacoNew.setDestino(perfilDestino);
            recomendacoNew.setOrigem(perfilOrigem);
            recomendacoNew.setDescricao(descricao);
            if(comp != null) {
                recomendacoNew.setCompetencia(comp);
            }

            recomendacaoService.save(recomendacoNew);

            resposta.mensagem = "Recomendacao criada.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object atualizar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long id = (Long)Long.valueOf((String)body.get("id"));
            String descricao = (String)body.get("descricao");

            //TODO - CRIAR VALIDAÇÃO DE PARÂMETROS
            Recomendacao recomendacao = recomendacaoService.findById(id);
            if(recomendacao != null){
                recomendacao.setDescricao(descricao);
                recomendacaoService.update(recomendacao);

                resposta.mensagem = "Recomendacao atualizada: " + recomendacao.toString();
                resposta.sucess = true;
                return resposta;
            }

            resposta.mensagem = "Recomendação não encontrada";
            return resposta;

        }catch(EntityNotFoundException e) {
            resposta.mensagem = "Recomendação não encontrada";
            return resposta;
        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/remover", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long id = (Long)Long.valueOf((String)body.get("id"));

            Recomendacao recomendacao = recomendacaoService.findById(id);
            if (recomendacao != null) {
                recomendacaoService.delete(recomendacao);

                resposta.mensagem = "Recomendação Removida: " + recomendacao.toString();
                resposta.sucess = true;
                return resposta;
            }
        } catch (EntityNotFoundException e) {
            resposta.mensagem = "Recomendação não encontrada";
            return resposta;
        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
        return "Falha ao remover";
    }

    @PostMapping(value = "/recomendacao/obter/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long id = (Long)Long.valueOf((String)body.get("id"));

            Recomendacao recomendacao = recomendacaoService.findById(id);
            if(recomendacao != null){
                resposta.conteudo.put("recomendacao", recomendacao);

                resposta.mensagem = "Operação realizada com exito.";
                resposta.sucess = true;
                return resposta;
            }

            resposta.mensagem = "Recomedação não encontrada.";
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/recomendacao/listar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getlist(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
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