package me.universi.grupo.controller;

import me.universi.api.entities.Response;
import me.universi.grupo.entities.Grupo;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.grupo.services.GrupoService;

import me.universi.user.entities.User;
import me.universi.user.services.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@Controller
public class GrupoController {
    @Autowired
    public GrupoService grupoService;
    @Autowired
    public UsuarioService usuarioService;

    @PostMapping(value = "/grupo/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_criar(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {
            User user = usuarioService.obterUsuarioNaSessao();

            Boolean grupoRoot = (Boolean)body.get("grupoRoot");

            String grupoIdPai = (String)body.get("grupoId");
            if(grupoIdPai == null) {
                if(!(grupoRoot != null && usuarioService.isContaAdmin(user))) {
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
            Boolean podeEntrar = (Boolean)body.get("podeEntrar");


            Grupo grupoPai = grupoIdPai==null?null:grupoService.findFirstById(Long.valueOf(grupoIdPai));

            if(grupoPai!=null && !grupoService.nicknameDisponivelParaGrupo(grupoPai, nickname)) {
                throw new GrupoException("Este Nickname não está disponível para este grupo.");
            }

            if((grupoRoot != null && grupoRoot && usuarioService.isContaAdmin(user)) || ((grupoPai !=null && grupoPai.podeCriarGrupo) || grupoService.verificarPermissaoParaGrupo(grupoPai, user))) {
                Grupo grupoNew = new Grupo();
                grupoNew.setNickname(nickname);
                grupoNew.setNome(nome);
                if(imagem != null && imagem.length()>0) {
                    grupoNew.setImagem(imagem);
                }
                grupoNew.setDescricao(descricao);
                grupoNew.setTipo(GrupoTipo.valueOf(tipo));
                grupoNew.setAdmin(user.getPerfil());
                if(podeCriarGrupo != null) {
                    grupoNew.setPodeCriarGrupo(podeCriarGrupo);
                }
                if(grupoPublico != null) {
                    grupoNew.setGrupoPublico(grupoPublico);
                }
                if(podeEntrar != null) {
                    grupoNew.setPodeEntrar(podeEntrar);
                }
                if((grupoRoot != null && grupoRoot) && usuarioService.isContaAdmin(user)) {
                    grupoNew.setGrupoRoot(true);
                    grupoService.save(grupoNew);
                } else {
                    grupoService.adicionarSubgrupo(grupoPai, grupoNew);
                }

                resposta.message = "Grupo criado com sucesso.";
                resposta.success = true;
                return resposta;
            }

            throw new GrupoException("Apenas Administradores podem criar subgrupos.");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/editar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_editar(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
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
            Boolean podeEntrar = (Boolean)body.get("podeEntrar");

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GrupoException("Grupo não encontrado.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verificarPermissaoParaGrupo(grupoEdit, user)) {
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

                resposta.message = "As Alterações foram salvas com sucesso.";
                resposta.success = true;
                return resposta;
            }

            throw new GrupoException("Falha ao editar grupo");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/participante/entrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_participante_entrar(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GrupoException("Grupo não encontrado.");
            }

            if(!grupoEdit.isPodeEntrar()) {
                throw new GrupoException("Grupo não permite entrada de paticipantes.");
            }

            if(grupoEdit.isPodeEntrar() || grupoService.verificarPermissaoParaGrupo(grupoEdit, user)) {
                if(grupoService.adicionarParticipante(grupoEdit, user.getPerfil())) {
                    resposta.success = true;
                    resposta.message = "Você entrou no Grupo.";
                    return resposta;
                } else {
                    throw new GrupoException("Você já esta neste Grupo.");
                }
            }

            throw new GrupoException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/participante/sair", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_participante_sair(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GrupoException("Grupo não encontrado.");
            }

            if(grupoService.removerParticipante(grupoEdit, user.getPerfil())) {
                resposta.success = true;
                resposta.message = "Você saiu do Grupo.";
                return resposta;
            } else {
                throw new GrupoException("Você não está neste Grupo.");
            }

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/participante/adicionar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_participante_adicionar(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            String participante = (String)body.get("participante");
            if(participante == null) {
                throw new GrupoException("Parametro participante é nulo.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            User participanteUser = null;
            if(participante != null && participante.length() > 0) {
                if (participante.contains("@")) {
                    participanteUser = (User) usuarioService.findFirstByEmail(participante);
                } else {
                    participanteUser = (User) usuarioService.loadUserByUsername(participante);
                }
            }

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));

            if(participanteUser != null && grupoService.verificarPermissaoParaGrupo(grupoEdit, user)) {
                if(grupoService.adicionarParticipante(grupoEdit, participanteUser.getPerfil())) {
                    resposta.success = true;
                    resposta.message = "Participante adicionado com sucesso.";
                    return resposta;
                } else {
                    throw new GrupoException("Participante já esta neste Grupo.");
                }
            }

            throw new GrupoException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/participante/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_participante_remover(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            String participante = (String)body.get("participante");
            if(participante == null) {
                throw new GrupoException("Parametro participante é nulo.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            User participanteUser = null;
            if(participante != null && participante.length() > 0) {
                if (participante.contains("@")) {
                    participanteUser = (User) usuarioService.findFirstByEmail(participante);
                } else {
                    participanteUser = (User) usuarioService.loadUserByUsername(participante);
                }
            }

            Grupo grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));

            if(participanteUser != null && grupoService.verificarPermissaoParaGrupo(grupoEdit, user)) {
                if(grupoService.removerParticipante(grupoEdit, participanteUser.getPerfil())) {
                    resposta.success = true;
                    resposta.message = "Participante removido com sucesso.";
                    return resposta;
                } else {
                    throw new GrupoException("Participante não faz parte deste Grupo.");
                }
            }

            throw new GrupoException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/participante/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_participante_listar(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));

            if(grupo != null) {
                resposta.body.put("participantes", grupo.getParticipantes());
                resposta.success = true;
                resposta.message = "Operação realizada com exito.";
                return resposta;
            }

            throw new GrupoException("Falha ao listar participante ao grupo");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_remove(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
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

            User user = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verificarPermissaoParaGrupo(grupo, user)) {
                grupoService.removerSubgrupo(grupo, grupoRemover);

                resposta.message = "Grupo removido com exito.";
                resposta.success = true;
                return resposta;
            }

            throw new GrupoException("Erro ao executar operação.");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/deletar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response grupo_deletar(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo == null) {
                throw new GrupoException("Grupo não encontrado.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verificarPermissaoParaGrupo(grupo, user)) {

                resposta.redirectTo = "/grupos";
                Long paiId = grupoService.findGrupoPaiDoGrupo(grupo.getId());
                if(paiId != null) {
                    resposta.redirectTo = grupoService.diretorioParaGrupo(paiId);
                }

                grupoService.delete(grupo);

                resposta.message = "Grupo deletado com exito.";
                resposta.success = true;
                return resposta;
            }

            throw new GrupoException("Erro ao executar operação.");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response obter_grupo(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo != null) {
                resposta.body.put("grupo", grupo);

                resposta.message = "Operação Realizada com exito.";
                resposta.success = true;
                return resposta;
            }

            throw new GrupoException("Falha ao obter grupo.");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/grupo/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response listar_subgrupo(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String grupoId = (String)body.get("grupoId");
            if(grupoId == null) {
                throw new GrupoException("Parametro grupoId é nulo.");
            }

            Grupo grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo != null) {
                Collection<Grupo> listaSubgrupos = grupo.getSubGrupos();
                resposta.body.put("subgrupos", listaSubgrupos);

                resposta.message = "Operação Realizada com exito.";
                resposta.success = true;
                return resposta;
            }

            throw new GrupoException("Falha ao listar grupo.");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }
}
