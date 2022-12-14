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
    public String grupo_handler(HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        try {
            Usuario usuario = usuarioService.obterUsuarioNaSessao();

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
                // verificar se o caminho ?? valido para o grupo, a partir do grupo pai
                grupoAtual = grupoService.parentescoCheckGrupo(grupoRoot, nicknameArr);
            }

            if(grupoAtual != null) {
                map.addAttribute("grupoService", grupoService);
                map.addAttribute("usuarioService", usuarioService);
                map.addAttribute("grupo", grupoAtual);
                map.addAttribute("grupoDiretorio", grupoService.diretorioParaGrupo(grupoAtual.getId()));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new GrupoException("Grupo n??o foi encontrado!");
            }

            if(flagEdicao) { // flags para determinar o tipo da p??gina

                // verficar permissao de edi????o do grupo
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
    public String grupos_handler(ModelMap map) {

        map.addAttribute("grupoService", grupoService);

        return "grupo/publicos";
    }


    @ResponseBody
    @RequestMapping(value = "/grupo/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_criar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {
            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            Boolean grupoRoot = (Boolean)body.get("grupoRoot");

            String grupoIdPai = (String)body.get("grupoId");
            if(grupoIdPai == null) {
                if(!(grupoRoot != null && usuarioService.isContaAdmin(usuario))) {
                    throw new GrupoException("Parametro grupoId ?? nulo.");
                }
            } else if(grupoIdPai.length() > 0 && (grupoRoot!=null && grupoRoot)) {
                throw new GrupoException("Voc?? n??o pode criar Grupo Master em Subgrupos.");
            }

            String nickname = (String)body.get("nickname");
            if(nickname == null) {
                throw new GrupoException("Parametro nickname ?? nulo.");
            }

            String nome = (String)body.get("nome");
            if(nome == null) {
                throw new GrupoException("Parametro nome ?? nulo.");
            }

            String imagem = (String)body.get("imagemUrl");

            String descricao = (String)body.get("descricao");
            if(descricao == null) {
                throw new GrupoException("Parametro descricao ?? nulo.");
            }

            String tipo = (String)body.get("tipo");
            if(tipo == null) {
                throw new GrupoException("Parametro tipo ?? nulo.");
            }

            Boolean podeCriarGrupo = (Boolean)body.get("podeCriarGrupo");
            Boolean grupoPublico = (Boolean)body.get("grupoPublico");
            Boolean podeEntrar = (Boolean)body.get("podeEntrar");


            Grupo grupoPai = grupoIdPai==null?null:grupoService.findFirstById(Long.valueOf(grupoIdPai));

            if(grupoPai!=null && !grupoService.nicknameDisponivelParaGrupo(grupoPai, nickname)) {
                throw new GrupoException("Este Nickname n??o est?? dispon??vel para este grupo.");
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
                if(podeEntrar != null) {
                    grupoNew.setPodeEntrar(podeEntrar);
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
    public Object grupo_editar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            String nome = (String)body.get("nome");
            String descricao = (String)body.get("descricao");
            String tipo = (String)body.get("tipo");
            String imagem = (String)body.get("imagemUrl");

            Boolean podeCriarGrupo = (Boolean)body.get("podeCriarGrupo");
            Boolean grupoPublico = (Boolean)body.get("grupoPublico");
            Boolean podeEntrar = (Boolean)body.get("podeEntrar");

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GrupoException("Grupo n??o encontrado.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verificarPermissaoParaGrupo(grupoEdit, usuario)) {
                if(nome != null && nome.length() > 0) {
                    grupoEdit.setNome(nome);
                }
                if(descricao != null && descricao.length() > 0) {
                    grupoEdit.setDescricao(descricao);
                }
                if(tipo != null && tipo.length() > 0) {
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
                if(podeEntrar != null) {
                    grupoEdit.setPodeEntrar(podeEntrar);
                }


                grupoService.save(grupoEdit);

                resposta.mensagem = "As Altera????es foram salvas com sucesso.";
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
    @PostMapping(value = "/grupo/participante/entrar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_participante_entrar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GrupoException("Grupo n??o encontrado.");
            }

            if(!grupoEdit.isPodeEntrar()) {
                throw new GrupoException("Grupo n??o permite entrada de paticipantes.");
            }

            if(grupoEdit.isPodeEntrar() || grupoService.verificarPermissaoParaGrupo(grupoEdit, usuario)) {
                if(grupoService.adicionarParticipante(grupoEdit, usuario.getPerfil())) {
                    resposta.sucess = true;
                    resposta.mensagem = "Voc?? entrou no Grupo.";
                    return resposta;
                } else {
                    throw new GrupoException("Voc?? j?? esta neste Grupo.");
                }
            }

            throw new GrupoException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @PostMapping(value = "/grupo/participante/sair", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_participante_sair(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GrupoException("Grupo n??o encontrado.");
            }

            if(grupoService.removerParticipante(grupoEdit, usuario.getPerfil())) {
                resposta.sucess = true;
                resposta.mensagem = "Voc?? saiu do Grupo.";
                return resposta;
            } else {
                throw new GrupoException("Voc?? n??o est?? neste Grupo.");
            }

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @PostMapping(value = "/grupo/participante/adicionar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object grupo_participante_adicionar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            String participante = (String)body.get("participante");
            if(participante == null) {
                throw new GrupoException("Parametro participante ?? nulo.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

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
                    throw new GrupoException("Participante j?? esta neste Grupo.");
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
    public Object grupo_participante_remover(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            String participante = (String)body.get("participante");
            if(participante == null) {
                throw new GrupoException("Parametro participante ?? nulo.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

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
                    throw new GrupoException("Participante n??o faz parte deste Grupo.");
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
    public Object grupo_participante_listar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));

            if(grupo != null) {
                resposta.conteudo.put("participantes", grupo.getParticipantes());
                resposta.sucess = true;
                resposta.mensagem = "Opera????o realizada com exito.";
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
    public Object grupo_remove(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            String grupoIdRemover = (String)body.get("grupoIdRemover");
            if(grupoIdRemover == null) {
                throw new GrupoException("Parametro grupoIdRemover ?? nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo == null) {
                throw new GrupoException("Grupo n??o encontrado.");
            }

            Grupo grupoRemover = grupoService.findFirstById(Long.valueOf(grupoIdRemover));
            if(grupoRemover == null) {
                throw new GrupoException("Subgrupo n??o encontrado.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verificarPermissaoParaGrupo(grupo, usuario)) {
                grupoService.removerSubgrupo(grupo, grupoRemover);

                resposta.mensagem = "Grupo removido com exito.";
                resposta.sucess = true;
                return resposta;
            }

            throw new GrupoException("Erro ao executar opera????o.");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @RequestMapping("/grupo/deletar")
    @ResponseBody
    public Object grupo_deletar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo == null) {
                throw new GrupoException("Grupo n??o encontrado.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verificarPermissaoParaGrupo(grupo, usuario)) {

                resposta.enderecoParaRedirecionar = "/grupos";
                Long paiId = grupoService.findGrupoPaiDoGrupo(grupo.getId());
                if(paiId != null) {
                    resposta.enderecoParaRedirecionar = grupoService.diretorioParaGrupo(paiId);
                }

                grupoService.delete(grupo);

                resposta.mensagem = "Grupo deletado com exito.";
                resposta.sucess = true;
                return resposta;
            }

            throw new GrupoException("Erro ao executar opera????o.");

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/grupo/obter", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object obter_grupo(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo != null) {
                resposta.conteudo.put("grupo", grupo);

                resposta.mensagem = "Opera????o Realizada com exito.";
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
    public Object listar_subgrupo(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId ?? nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo != null) {
                Collection<Grupo> listaSubgrupos = grupo.getSubGrupos();
                resposta.conteudo.put("subgrupos", listaSubgrupos);

                resposta.mensagem = "Opera????o Realizada com exito.";
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
