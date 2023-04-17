package me.universi.grupo.controller;

import me.universi.api.entities.Response;
import me.universi.grupo.entities.Group;
import me.universi.grupo.enums.GroupType;
import me.universi.grupo.exceptions.GroupException;
import me.universi.grupo.services.GroupService;

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
    public GroupService grupoService;
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
                    throw new GroupException("Parametro grupoId é nulo.");
                }
            } else if(grupoIdPai.length() > 0 && (grupoRoot!=null && grupoRoot)) {
                throw new GroupException("Você não pode criar Grupo Master em Subgrupos.");
            }

            String nickname = (String)body.get("nickname");
            if(nickname == null) {
                throw new GroupException("Parametro nickname é nulo.");
            }

            String nome = (String)body.get("nome");
            if(nome == null) {
                throw new GroupException("Parametro nome é nulo.");
            }

            String imagem = (String)body.get("imagemUrl");

            String descricao = (String)body.get("descricao");
            if(descricao == null) {
                throw new GroupException("Parametro descricao é nulo.");
            }

            String tipo = (String)body.get("tipo");
            if(tipo == null) {
                throw new GroupException("Parametro tipo é nulo.");
            }

            Boolean podeCriarGrupo = (Boolean)body.get("podeCriarGrupo");
            Boolean grupoPublico = (Boolean)body.get("grupoPublico");
            Boolean podeEntrar = (Boolean)body.get("podeEntrar");


            Group grupoPai = grupoIdPai==null?null:grupoService.findFirstById(Long.valueOf(grupoIdPai));

            if(grupoPai!=null && !grupoService.isNicknameAvailableForGroup(grupoPai, nickname)) {
                throw new GroupException("Este Nickname não está disponível para este grupo.");
            }

            if((grupoRoot != null && grupoRoot && usuarioService.isContaAdmin(user)) || ((grupoPai !=null && grupoPai.canCreateGroup) || grupoService.verifyPermissionToEditGroup(grupoPai, user))) {
                Group grupoNew = new Group();
                grupoNew.setNickname(nickname);
                grupoNew.setName(nome);
                if(imagem != null && imagem.length()>0) {
                    grupoNew.setImage(imagem);
                }
                grupoNew.setDescription(descricao);
                grupoNew.setType(GroupType.valueOf(tipo));
                grupoNew.setAdmin(user.getPerfil());
                if(podeCriarGrupo != null) {
                    grupoNew.setCanCreateGroup(podeCriarGrupo);
                }
                if(grupoPublico != null) {
                    grupoNew.setPublicGroup(grupoPublico);
                }
                if(podeEntrar != null) {
                    grupoNew.setCanEnter(podeEntrar);
                }
                if((grupoRoot != null && grupoRoot) && usuarioService.isContaAdmin(user)) {
                    grupoNew.setRootGroup(true);
                    grupoService.save(grupoNew);
                } else {
                    grupoService.addSubGroup(grupoPai, grupoNew);
                }

                resposta.message = "Grupo criado com sucesso.";
                resposta.success = true;
                return resposta;
            }

            throw new GroupException("Apenas Administradores podem criar subgrupos.");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            String nome = (String)body.get("nome");
            String descricao = (String)body.get("descricao");
            String tipo = (String)body.get("tipo");
            String imagem = (String)body.get("imagemUrl");

            Boolean podeCriarGrupo = (Boolean)body.get("podeCriarGrupo");
            Boolean grupoPublico = (Boolean)body.get("grupoPublico");
            Boolean podeEntrar = (Boolean)body.get("podeEntrar");

            Group grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GroupException("Grupo não encontrado.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verifyPermissionToEditGroup(grupoEdit, user)) {
                if(nome != null && nome.length() > 0) {
                    grupoEdit.setName(nome);
                }
                if(descricao != null && descricao.length() > 0) {
                    grupoEdit.setDescription(descricao);
                }
                if(tipo != null && tipo.length() > 0) {
                    grupoEdit.setType(GroupType.valueOf(tipo));
                }
                if(imagem != null && imagem.length()>0) {
                    grupoEdit.setImage(imagem);
                }
                if(podeCriarGrupo != null) {
                    grupoEdit.setCanCreateGroup(podeCriarGrupo);
                }
                if(grupoPublico != null) {
                    grupoEdit.setPublicGroup(grupoPublico);
                }
                if(podeEntrar != null) {
                    grupoEdit.setCanEnter(podeEntrar);
                }


                grupoService.save(grupoEdit);

                resposta.message = "As Alterações foram salvas com sucesso.";
                resposta.success = true;
                return resposta;
            }

            throw new GroupException("Falha ao editar grupo");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            Group grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GroupException("Grupo não encontrado.");
            }

            if(!grupoEdit.isCanEnter()) {
                throw new GroupException("Grupo não permite entrada de paticipantes.");
            }

            if(grupoEdit.isCanEnter() || grupoService.verifyPermissionToEditGroup(grupoEdit, user)) {
                if(grupoService.addParticipantToGroup(grupoEdit, user.getPerfil())) {
                    resposta.success = true;
                    resposta.message = "Você entrou no Grupo.";
                    return resposta;
                } else {
                    throw new GroupException("Você já esta neste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            Group grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupoEdit == null) {
                throw new GroupException("Grupo não encontrado.");
            }

            if(grupoService.removeParticipantFromGroup(grupoEdit, user.getPerfil())) {
                resposta.success = true;
                resposta.message = "Você saiu do Grupo.";
                return resposta;
            } else {
                throw new GroupException("Você não está neste Grupo.");
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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            String participante = (String)body.get("participante");
            if(participante == null) {
                throw new GroupException("Parametro participante é nulo.");
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

            Group grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));

            if(participanteUser != null && grupoService.verifyPermissionToEditGroup(grupoEdit, user)) {
                if(grupoService.addParticipantToGroup(grupoEdit, participanteUser.getPerfil())) {
                    resposta.success = true;
                    resposta.message = "Participante adicionado com sucesso.";
                    return resposta;
                } else {
                    throw new GroupException("Participante já esta neste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            String participante = (String)body.get("participante");
            if(participante == null) {
                throw new GroupException("Parametro participante é nulo.");
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

            Group grupoEdit = grupoService.findFirstById(Long.valueOf(grupoId));

            if(participanteUser != null && grupoService.verifyPermissionToEditGroup(grupoEdit, user)) {
                if(grupoService.removeParticipantFromGroup(grupoEdit, participanteUser.getPerfil())) {
                    resposta.success = true;
                    resposta.message = "Participante removido com sucesso.";
                    return resposta;
                } else {
                    throw new GroupException("Participante não faz parte deste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            Group grupo = grupoService.findFirstById(Long.valueOf(grupoId));

            if(grupo != null) {
                resposta.body.put("participantes", grupo.getParticipants());
                resposta.success = true;
                resposta.message = "Operação realizada com exito.";
                return resposta;
            }

            throw new GroupException("Falha ao listar participante ao grupo");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            String grupoIdRemover = (String)body.get("grupoIdRemover");
            if(grupoIdRemover == null) {
                throw new GroupException("Parametro grupoIdRemover é nulo.");
            }

            Group grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo == null) {
                throw new GroupException("Grupo não encontrado.");
            }

            Group grupoRemover = grupoService.findFirstById(Long.valueOf(grupoIdRemover));
            if(grupoRemover == null) {
                throw new GroupException("Subgrupo não encontrado.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verifyPermissionToEditGroup(grupo, user)) {
                grupoService.removeSubGroup(grupo, grupoRemover);

                resposta.message = "Grupo removido com exito.";
                resposta.success = true;
                return resposta;
            }

            throw new GroupException("Erro ao executar operação.");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            Group grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo == null) {
                throw new GroupException("Grupo não encontrado.");
            }

            User user = usuarioService.obterUsuarioNaSessao();

            if(grupoService.verifyPermissionToEditGroup(grupo, user)) {

                resposta.redirectTo = "/grupos";
                Long paiId = grupoService.findParentGroupId(grupo.getId());
                if(paiId != null) {
                    resposta.redirectTo = grupoService.getGroupPath(paiId);
                }

                grupoService.delete(grupo);

                resposta.message = "Grupo deletado com exito.";
                resposta.success = true;
                return resposta;
            }

            throw new GroupException("Erro ao executar operação.");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            Group grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo != null) {
                resposta.body.put("grupo", grupo);

                resposta.message = "Operação Realizada com exito.";
                resposta.success = true;
                return resposta;
            }

            throw new GroupException("Falha ao obter grupo.");

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
                throw new GroupException("Parametro grupoId é nulo.");
            }

            Group grupo = grupoService.findFirstById(Long.valueOf(grupoId));
            if(grupo != null) {
                Collection<Group> listaSubgrupos = grupo.getSubGroups();
                resposta.body.put("subgrupos", listaSubgrupos);

                resposta.message = "Operação Realizada com exito.";
                resposta.success = true;
                return resposta;
            }

            throw new GroupException("Falha ao listar grupo.");

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }
}
