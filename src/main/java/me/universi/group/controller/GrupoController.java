package me.universi.group.controller;

import me.universi.api.entities.Response;
import me.universi.group.entities.Group;
import me.universi.group.enums.GroupType;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;

import me.universi.user.entities.User;
import me.universi.user.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/group")
public class GrupoController {
    @Autowired
    public GroupService groupService;
    @Autowired
    public UserService userService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {
            User user = userService.getUserInSession();

            Boolean groupRoot = (Boolean)body.get("groupRoot");

            String groupIdParent = (String)body.get("groupId");
            String groupPathParent = (String)body.get("groupPath");

            boolean hasGroupParent = groupIdParent != null || groupPathParent != null;

            if(!hasGroupParent) {
                if(!(groupRoot != null && userService.isUserAdmin(user))) {
                    throw new GroupException("Parâmetro groupId é nulo.");
                }
            } else if(((groupIdParent != null && groupIdParent.length() > 0) || (groupPathParent != null && groupPathParent.length() > 0)) && (groupRoot!=null && groupRoot)) {
                throw new GroupException("Você não pode criar Grupo Master em Subgrupos.");
            }

            String nickname = (String)body.get("nickname");
            if(nickname == null) {
                throw new GroupException("Parâmetro nickname é nulo.");
            }

            String name = (String)body.get("name");
            if(name == null) {
                throw new GroupException("Parâmetro nome é nulo.");
            }

            String image = (String)body.get("imageUrl");

            String description = (String)body.get("description");
            if(description == null) {
                throw new GroupException("Parâmetro description é nulo.");
            }

            String groupType = (String)body.get("type");
            if(groupType == null) {
                throw new GroupException("Parâmetro type é nulo.");
            }

            Boolean canCreateGroup = (Boolean)body.get("canCreateGroup");
            Boolean publicGroup = (Boolean)body.get("publicGroup");
            Boolean canEnter = (Boolean)body.get("canEnter");

            Group parentGroup = (groupIdParent==null && groupPathParent==null)?null:groupService.getGroupByGroupIdOrGroupPath(groupIdParent, groupPathParent);

            // support only lowercase nickname
            nickname = nickname.toLowerCase();

            if(!groupService.isNicknameAvailableForGroup(parentGroup, nickname)) {
                throw new GroupException("Este Nickname não está disponível para este grupo.");
            }

            if((groupRoot != null && groupRoot && userService.isUserAdmin(user)) || ((parentGroup !=null && parentGroup.canCreateGroup) || groupService.verifyPermissionToEditGroup(parentGroup, user))) {
                Group groupNew = new Group();
                groupNew.setNickname(nickname);
                groupNew.setName(name);
                if(image != null && image.length()>0) {
                    groupNew.setImage(image);
                }
                groupNew.setDescription(description);
                groupNew.setType(GroupType.valueOf(groupType));
                groupNew.setAdmin(user.getProfile());
                if(canCreateGroup != null) {
                    groupNew.setCanCreateGroup(canCreateGroup);
                }
                if(publicGroup != null) {
                    groupNew.setPublicGroup(publicGroup);
                }
                if(canEnter != null) {
                    groupNew.setCanEnter(canEnter);
                }
                if((groupRoot != null && groupRoot) && userService.isUserAdmin(user)) {
                    groupNew.setRootGroup(true);
                    groupService.save(groupNew);
                } else {
                    groupService.addSubGroup(parentGroup, groupNew);
                }

                response.message = "Grupo criado com sucesso.";
                response.success = true;
                return response;
            }

            throw new GroupException("Apenas Administradores podem criar subgrupos.");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            String name = (String)body.get("name");
            String description = (String)body.get("description");
            String groupType = (String)body.get("type");
            String image = (String)body.get("imageUrl");

            Boolean canCreateGroup = (Boolean)body.get("canCreateGroup");
            Boolean publicGroup = (Boolean)body.get("publicGroup");
            Boolean canEnter = (Boolean)body.get("canEnter");

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            User user = userService.getUserInSession();

            if(groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(name != null && name.length() > 0) {
                    groupEdit.setName(name);
                }
                if(description != null && description.length() > 0) {
                    groupEdit.setDescription(description);
                }
                if(groupType != null && groupType.length() > 0) {
                    groupEdit.setType(GroupType.valueOf(groupType));
                }
                if(image != null && image.length()>0) {
                    groupEdit.setImage(image);
                }
                if(canCreateGroup != null) {
                    groupEdit.setCanCreateGroup(canCreateGroup);
                }
                if(publicGroup != null) {
                    groupEdit.setPublicGroup(publicGroup);
                }
                if(canEnter != null) {
                    groupEdit.setCanEnter(canEnter);
                }


                groupService.save(groupEdit);

                response.message = "As Alterações foram salvas com sucesso.";
                response.success = true;
                return response;
            }

            throw new GroupException("Falha ao editar grupo");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/participant/enter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_enter(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            User user = userService.getUserInSession();

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(!groupEdit.isCanEnter()) {
                throw new GroupException("Grupo não permite entrada de participantes.");
            }

            if(groupEdit.isCanEnter() || groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.addParticipantToGroup(groupEdit, user.getProfile())) {
                    response.success = true;
                    response.message = "Você entrou no Grupo.";
                    return response;
                } else {
                    throw new GroupException("Você já esta neste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/participant/exit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_exit(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            User user = userService.getUserInSession();

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(groupService.removeParticipantFromGroup(groupEdit, user.getProfile())) {
                response.success = true;
                response.message = "Você saiu do Grupo.";
                return response;
            } else {
                throw new GroupException("Você não está neste Grupo.");
            }

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/participant/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_add(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            String participant = (String)body.get("participant");
            if(participant == null) {
                throw new GroupException("Parâmetro participant é nulo.");
            }

            User user = userService.getUserInSession();

            User participantUser = null;
            if(participant != null && participant.length() > 0) {
                if (participant.contains("@")) {
                    participantUser = (User) userService.findFirstByEmail(participant);
                } else {
                    participantUser = (User) userService.loadUserByUsername(participant);
                }
            }

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(participantUser != null && groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.addParticipantToGroup(groupEdit, participantUser.getProfile())) {
                    response.success = true;
                    response.message = "Participante adicionado com sucesso.";
                    return response;
                } else {
                    throw new GroupException("Participante já esta neste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/participant/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_remove(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            String participant = (String)body.get("participant");
            if(participant == null) {
                throw new GroupException("Parâmetro participant é nulo.");
            }

            User user = userService.getUserInSession();

            User participantUser = null;
            if(participant != null && participant.length() > 0) {
                if (participant.contains("@")) {
                    participantUser = (User) userService.findFirstByEmail(participant);
                } else {
                    participantUser = (User) userService.loadUserByUsername(participant);
                }
            }

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(participantUser != null && groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.removeParticipantFromGroup(groupEdit, participantUser.getProfile())) {
                    response.success = true;
                    response.message = "Participante removido com sucesso.";
                    return response;
                } else {
                    throw new GroupException("Participante não faz parte deste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/participant/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_list(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {
                response.body.put("participants", group.getParticipants());
                response.success = true;
                return response;
            }

            throw new GroupException("Falha ao listar participante ao grupo");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            String groupIdRemove = (String)body.get("groupIdRemove");
            if(groupIdRemove == null) {
                throw new GroupException("Parâmetro groupIdRemove é nulo.");
            }

            if(group == null) {
                throw new GroupException("Grupo não encontrado.");
            }

            Group groupRemove = groupService.findFirstById(groupIdRemove);
            if(groupRemove == null) {
                throw new GroupException("Subgrupo não encontrado.");
            }

            User user = userService.getUserInSession();

            if(groupService.verifyPermissionToEditGroup(group, user)) {
                groupService.removeSubGroup(group, groupRemove);

                response.message = "Grupo removido com exito.";
                response.success = true;
                return response;
            }

            throw new GroupException("Erro ao executar operação.");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            User user = userService.getUserInSession();

            if(groupService.verifyPermissionToEditGroup(group, user)) {

                response.redirectTo = "/groups";
                UUID parentId = groupService.findParentGroupId(group.getId());
                if(parentId != null) {
                    response.redirectTo = groupService.getGroupPath(parentId);
                }

                groupService.delete(group);

                response.message = "Grupo deletado com exito.";
                response.success = true;
                return response;
            }

            throw new GroupException("Erro ao executar operação.");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if (group != null) {
                response.body.put("group", group);
                response.success = true;
                return response;
            }

            throw new GroupException("Falha ao obter grupo.");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_subgroup(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {
                Collection<Group> subgroupList = group.getSubGroups();
                response.body.put("subgroups", subgroupList);
                response.success = true;
                return response;
            }

            throw new GroupException("Falha ao listar grupo.");

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/parents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_available_parents(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {
            Collection<Group> groups = groupService.findAll().stream()
                .filter(Group::isCanCreateGroup)
                .toList();

            response.body.put("groups", groups);
            response.success = true;
        }

        catch (Exception e) {
            response.message = e.getMessage();
            response.success = false;
        }

        return response;
    }
}
