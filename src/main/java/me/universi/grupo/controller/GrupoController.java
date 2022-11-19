package me.universi.grupo.controller;

import me.universi.api.entities.Resposta;
import me.universi.grupo.entities.Grupo;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.grupo.services.GrupoService;

import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.SecurityUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;

@Controller
public class GrupoController {
    @Autowired
    public GrupoService grupoService;
    @Autowired
    public SecurityUserDetailsService usuarioService;

    // mapaear tudo exceto, /css, /js, /img, /favicon.ico, comflita com static resources do Thymeleaf
    @GetMapping(value = {"{url:(?!css$|js$|img$|favicon.ico$).*}/**"})
    public String grupo_handler(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map) {
        try {
            Usuario usuario = (Usuario)session.getAttribute("usuario");

            String requestPathSt = request.getRequestURI().toLowerCase();

            boolean flagEditar = requestPathSt.endsWith("/editar");
            boolean flagCriar = requestPathSt.endsWith("/criar");
            boolean flagParticipanteAdicionar = requestPathSt.endsWith("/add-participante");
            boolean flagParticipanteRemover = requestPathSt.endsWith("/rem-participante");
            boolean flagEdicao = flagEditar | flagCriar | flagParticipanteAdicionar | flagParticipanteRemover;
            boolean flagParticipantesListar = requestPathSt.endsWith("/participantes");

            if(flagCriar) {
                requestPathSt = requestPathSt.substring(0, requestPathSt.length() - 6);
            } else if(flagEditar) {
                requestPathSt = requestPathSt.substring(0, requestPathSt.length() - 7);
            } else if(flagParticipanteAdicionar || flagParticipanteRemover) {
                requestPathSt = requestPathSt.substring(0, requestPathSt.length() - 16);
            } else if(flagParticipantesListar) {
                requestPathSt = requestPathSt.substring(0, requestPathSt.length() - 14);
            }

            String[] nicknameArr = requestPathSt.split("/");

            Grupo grupoRoot = null;
            Grupo grupoAtual = null;

            grupoRoot = grupoService.findFirstByGrupoRootAndNickname(true, nicknameArr[1]);
            if(grupoRoot != null) {
                grupoAtual = grupoService.parentescoCheckGrupo(grupoRoot, nicknameArr);
            }

            if(grupoAtual != null) {
                map.addAttribute("grupo", grupoAtual);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new GrupoException("Grupo não foi encontrado!");
            }

            if(flagEdicao) {
                //grupoService.verificarPermissaoParaGrupo(grupoAtual, usuario);
                session.setAttribute("lastPath", requestPathSt);
                map.addAttribute("tiposGrupo", GrupoTipo.values());

                if(flagEditar) {
                    return "grupo/editar";
                }

                if(flagCriar) {
                    return "grupo/criar";
                }

                if(flagParticipanteAdicionar) {
                    return "grupo/adicionar_participante";
                }

                if(flagParticipanteRemover) {
                    return "grupo/remover_participante";
                }
            } else if(flagParticipantesListar) {
                return "grupo/participantes";
            }

        } catch (Exception e){
            map.put("error", e.getMessage());
        }
        return "grupo/grupo";
    }


    @ResponseBody
    @RequestMapping(value = "/grupo/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_criar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String grupoIdPai = (String)body.get("grupoId");
            if(grupoIdPai == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            String nickname = (String)body.get("nickname");
            if(nickname == null) {
                throw new GrupoException("Parametro nickname é nulo.");
            }

            String nome = (String)body.get("nome");
            if(nome == null) {
                throw new GrupoException("Parametro nome é nulo.");
            }

            String descricao = (String)body.get("descricao");
            if(descricao == null) {
                throw new GrupoException("Parametro descricao é nulo.");
            }

            String tipo = (String)body.get("tipo");
            if(tipo == null) {
                throw new GrupoException("Parametro tipo é nulo.");
            }

            Boolean podeCriarGrupo = (Boolean)body.get("podeCriarGrupo");

            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Grupo grupoPai = grupoService.findFirstById(Long.valueOf(grupoIdPai));

            if(!grupoService.nicknameDisponivelParaGrupo(grupoPai, nickname)) {
                throw new GrupoException("Este Nickname não está disponível para este grupo.");
            }

            if((grupoPai.podeCriarGrupo) || grupoService.verificarPermissaoParaGrupo(grupoPai, usuario)) {
                Grupo grupoNew = new Grupo();
                grupoNew.setNickname(nickname);
                grupoNew.setNome(nome);
                grupoNew.setDescricao(descricao);
                grupoNew.setTipo(GrupoTipo.valueOf(tipo));
                grupoNew.setAdmin(usuario.getPerfil());
                if(podeCriarGrupo != null) {
                    grupoNew.setPodeCriarGrupo(podeCriarGrupo);
                }

                grupoService.adicionarSubgrupo(grupoPai, grupoNew);

                resposta.mensagem = "Grupo criado com sucesso.";
                resposta.sucess = true;
                return resposta;
            }

            throw new GrupoException("Apenas Administradores podem criar subgrupos.");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/grupo/editar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_editar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            String nome = (String)body.get("nome");
            String descricao = (String)body.get("descricao");
            String tipo = (String)body.get("tipo");
            Boolean podeCriarGrupo = (Boolean)body.get("podeCriarGrupo");

            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));

            if(grupoService.verificarPermissaoParaGrupo(grupoEdit, usuario)) {
                if(nome != null) {
                    grupoEdit.setNome(nome);
                }
                if(descricao != null) {
                    grupoEdit.setDescricao(descricao);
                }
                if(tipo != null) {
                    grupoEdit.setTipo(GrupoTipo.valueOf(tipo));
                }
                if(podeCriarGrupo != null) {
                    grupoEdit.setPodeCriarGrupo(podeCriarGrupo);
                }

                grupoService.save(grupoEdit);

                resposta.mensagem = "As Alterações foram salvas com sucesso.";
                resposta.sucess = true;
                return resposta;
            }

            throw new GrupoException("Falha ao editar grupo");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @PostMapping(value = "/grupo/participante/adicionar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_participante_adicionar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            String participante = (String)body.get("participante");
            if(participante == null) {
                throw new GrupoException("Parametro participante é nulo.");
            }

            Usuario usuario = (Usuario) session.getAttribute("usuario");

            Usuario participanteUser = null;
            if(participante != null && participante.length() > 0) {
                if (participante.contains("@")) {
                    participanteUser = (Usuario) usuarioService.findFirstByEmail(participante);
                } else {
                    participanteUser = (Usuario) usuarioService.loadUserByUsername(participante);
                }
            }

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));

            if(participanteUser != null && grupoService.verificarPermissaoParaGrupo(grupoEdit, usuario)) {
                if(grupoService.adicionarParticipante(grupoEdit, participanteUser.getPerfil())) {
                    resposta.sucess = true;
                    resposta.mensagem = "Participante adicionado com sucesso.";
                    return resposta;
                } else {
                    throw new GrupoException("Participante já esta neste Grupo.");
                }
            }

            throw new GrupoException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @PostMapping(value = "/grupo/participante/remover", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_participante_remover(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            String participante = (String)body.get("participante");
            if(participante == null) {
                throw new GrupoException("Parametro participante é nulo.");
            }

            Usuario usuario = (Usuario) session.getAttribute("usuario");

            Usuario participanteUser = null;
            if(participante != null && participante.length() > 0) {
                if (participante.contains("@")) {
                    participanteUser = (Usuario) usuarioService.findFirstByEmail(participante);
                } else {
                    participanteUser = (Usuario) usuarioService.loadUserByUsername(participante);
                }
            }

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));

            if(participanteUser != null && grupoService.verificarPermissaoParaGrupo(grupoEdit, usuario)) {
                if(grupoService.removerParticipante(grupoEdit, participanteUser.getPerfil())) {
                    resposta.sucess = true;
                    resposta.mensagem = "Participante removido com sucesso.";
                    return resposta;
                } else {
                    throw new GrupoException("Participante não faz parte deste Grupo.");
                }
            }

            throw new GrupoException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @RequestMapping("/grupo/remover")
    @ResponseBody
    public Object grupo_remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));

            if(grupoService.verificarPermissaoParaGrupo(grupo, usuario)) {
                grupoService.delete(grupo);

                resposta.mensagem = "Grupo removido com exito.";
                resposta.sucess = true;
                return resposta;
            }

            throw new GrupoException("Erro ao executar operação.");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/grupo/obter", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object obter_grupo(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo != null) {
                resposta.conteudo.put("grupo", grupo);

                resposta.mensagem = "Operação Realizada com exito.";
                resposta.sucess = true;
                return resposta;
            }

            throw new GrupoException("Falha ao obter grupo.");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @PostMapping(value = "/grupo/listar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object listar_subgrupo(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo != null) {
                Collection<Grupo> listaSubgrupos = grupo.getSubGrupos();
                resposta.conteudo.put("subgrupos", listaSubgrupos);

                resposta.mensagem = "Operação Realizada com exito.";
                resposta.sucess = true;
                return resposta;
            }

            throw new GrupoException("Falha ao listar grupo.");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }
}
