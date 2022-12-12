package me.universi.grupo.controller;

import me.universi.api.entities.Resposta;
import me.universi.grupo.entities.Grupo;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.grupo.services.GrupoService;

import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Controller
public class GrupoController {
    @Autowired
    public GrupoService grupoService;
    @Autowired
    public UsuarioService usuarioService;

    // mapaear tudo exceto, /css, /js, /img, /favicon.ico, comflita com static resources do Thymeleaf
    @GetMapping(value = {"{url:(?!css$|js$|img$|favicon.ico$).*}/**"})
    public String grupo_handler(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map) {
        try {
            Usuario usuario = (Usuario)session.getAttribute("usuario");

            if(usuarioService.usuarioPrecisaDePerfil(usuario)) {
                return "redirect:/p/"+ usuario.getUsername() +"/editar";
            }

            // obter diretorio caminho url
            String requestPathSt = request.getRequestURI().toLowerCase();

            boolean flagEditar = requestPathSt.endsWith("/editar");
            boolean flagCriar = requestPathSt.endsWith("/criar");
            boolean flagEdicao = flagEditar | flagCriar;
            boolean flagParticipantesListar = requestPathSt.endsWith("/participantes");
            boolean flagGruposListar = requestPathSt.endsWith("/grupos");

            String[] nicknameArr = requestPathSt.split("/");

            if(flagEdicao || flagParticipantesListar || flagGruposListar) {
                // remover ultimo componente no caminho, flags
                nicknameArr = Arrays.copyOf(nicknameArr, nicknameArr.length - 1);
            }

            Grupo grupoRoot = null;
            Grupo grupoAtual = null;

            // obter grupo pai, nickname unico e que pode ser acessado diretamente pela url
            grupoRoot = grupoService.findFirstByGrupoRootAndNickname(true, nicknameArr[1]);
            if(grupoRoot != null) {
                // verificar se o caminho é valido para o grupo, a partir do grupo pai
                grupoAtual = grupoService.parentescoCheckGrupo(grupoRoot, nicknameArr);
            }

            if(grupoAtual != null) {
                map.addAttribute("grupoService", grupoService);
                map.addAttribute("usuarioService", usuarioService);
                map.addAttribute("grupo", grupoAtual);
                map.addAttribute("grupoDiretorio", grupoService.diretorioParaGrupo(grupoAtual.getId()));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new GrupoException("Grupo não foi encontrado!");
            }

            if(flagEdicao) { // flags para determinar o tipo da página

                // verficar permissao de edição do grupo
                //grupoService.verificarPermissaoParaGrupo(grupoAtual, usuario);

                map.addAttribute("tiposGrupo", GrupoTipo.values());
                map.addAttribute("grupoSubDiretorio", String.join("/", Arrays.copyOf(nicknameArr, nicknameArr.length - 1)));

                if(flagEditar) {
                    map.addAttribute("flagPage", "flagEditar");
                } else if(flagCriar) {
                    map.addAttribute("flagPage", "flagCriar");
                }
            } else if(flagParticipantesListar) {
                map.addAttribute("flagPage", "flagParticipantesListar");
            } else if(flagGruposListar) {
                map.addAttribute("flagPage", "flagGruposListar");
            }

        } catch (Exception e){
            map.put("error", "Grupo: " + e.getMessage());
        }
        return "grupo/grupo_index";
    }

    @GetMapping("/grupos")
    public String grupos_handler(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map) {

        map.addAttribute("grupoService", grupoService);

        return "grupo/publicos";
    }


    @ResponseBody
    @RequestMapping(value = "/grupo/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_criar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            Boolean grupoRoot = (Boolean)body.get("grupoRoot");

            String grupoIdPai = (String)body.get("grupoId");
            if(grupoIdPai == null) {
                if(!(grupoRoot != null && usuarioService.isContaAdmin(usuario))) {
                    throw new GrupoException("Parametro grupoId é nulo.");
                }
            } else if(grupoIdPai.length() > 0 && (grupoRoot!=null && grupoRoot)) {
                throw new GrupoException("Você não pode criar Grupo Master em Subgrupos.");
            }

            String nickname = (String)body.get("nickname");
            if(nickname == null) {
                throw new GrupoException("Parametro nickname é nulo.");
            }

            String nome = (String)body.get("nome");
            if(nome == null) {
                throw new GrupoException("Parametro nome é nulo.");
            }

            String imagem = (String)body.get("imagemUrl");

            String descricao = (String)body.get("descricao");
            if(descricao == null) {
                throw new GrupoException("Parametro descricao é nulo.");
            }

            String tipo = (String)body.get("tipo");
            if(tipo == null) {
                throw new GrupoException("Parametro tipo é nulo.");
            }

            Boolean podeCriarGrupo = (Boolean)body.get("podeCriarGrupo");
            Boolean grupoPublico = (Boolean)body.get("grupoPublico");

            Grupo grupoPai = grupoIdPai==null?null:grupoService.findFirstById(Long.valueOf(grupoIdPai));

            if(grupoPai!=null && !grupoService.nicknameDisponivelParaGrupo(grupoPai, nickname)) {
                throw new GrupoException("Este Nickname não está disponível para este grupo.");
            }

            if((grupoRoot != null && grupoRoot && usuarioService.isContaAdmin(usuario)) || ((grupoPai !=null && grupoPai.podeCriarGrupo) || grupoService.verificarPermissaoParaGrupo(grupoPai, usuario))) {
                Grupo grupoNew = new Grupo();
                grupoNew.setNickname(nickname);
                grupoNew.setNome(nome);
                if(imagem != null && imagem.length()>0) {
                    grupoNew.setImagem(imagem);
                }
                grupoNew.setDescricao(descricao);
                grupoNew.setTipo(GrupoTipo.valueOf(tipo));
                grupoNew.setAdmin(usuario.getPerfil());
                if(podeCriarGrupo != null) {
                    grupoNew.setPodeCriarGrupo(podeCriarGrupo);
                }
                if(grupoPublico != null) {
                    grupoNew.setGrupoPublico(grupoPublico);
                }
                if((grupoRoot != null && grupoRoot) && usuarioService.isContaAdmin(usuario)) {
                    grupoNew.setGrupoRoot(true);
                    grupoService.save(grupoNew);
                } else {
                    grupoService.adicionarSubgrupo(grupoPai, grupoNew);
                }

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
            String imagem = (String)body.get("imagemUrl");

            Boolean podeCriarGrupo = (Boolean)body.get("podeCriarGrupo");
            Boolean grupoPublico = (Boolean)body.get("grupoPublico");

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
                if(imagem != null && imagem.length()>0) {
                    grupoEdit.setImagem(imagem);
                }
                if(podeCriarGrupo != null) {
                    grupoEdit.setPodeCriarGrupo(podeCriarGrupo);
                }
                if(grupoPublico != null) {
                    grupoEdit.setGrupoPublico(grupoPublico);
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

    @ResponseBody
    @PostMapping(value = "/grupo/participante/listar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_participante_listar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));

            if(grupo != null) {
                resposta.conteudo.put("participantes", grupo.getParticipantes());
                resposta.sucess = true;
                resposta.mensagem = "Operação realizada com exito.";
                return resposta;
            }

            throw new GrupoException("Falha ao listar participante ao grupo");

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

            String grupoIdRemover = (String)body.get("grupoIdRemover");
            if(grupoIdRemover == null) {
                throw new GrupoException("Parametro grupoIdRemover é nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo == null) {
                throw new GrupoException("Grupo não encontrado.");
            }

            Grupo grupoRemover = grupoService.findFirstById(Long.valueOf(grupoIdRemover));
            if(grupoRemover == null) {
                throw new GrupoException("Subgrupo não encontrado.");
            }

            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if(grupoService.verificarPermissaoParaGrupo(grupo, usuario)) {
                grupoService.removerSubgrupo(grupo, grupoRemover);

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
