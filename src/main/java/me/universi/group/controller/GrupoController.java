package me.universi.group.controller;

import java.net.URI;
import me.universi.api.entities.Response;
import me.universi.group.entities.Group;
import me.universi.group.enums.GroupType;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;

import me.universi.user.entities.User;
import me.universi.user.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

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
        return Response.buildResponse(response -> {

            User user = userService.getUserInSession();

            Boolean groupRoot = (Boolean)body.get("groupRoot");

            String groupIdParent = (String)body.get("groupId");
            String groupPathParent = (String)body.get("groupPath");

            boolean hasGroupParent = groupIdParent != null || groupPathParent != null;

            if(!hasGroupParent) {
                if(!(groupRoot != null && userService.isUserAdmin(user))) {
                    throw new GroupException("Parâmetro groupId é nulo.");
                }
            } else if(((groupIdParent != null && !groupIdParent.isEmpty()) || (groupPathParent != null && !groupPathParent.isEmpty())) && (groupRoot!=null && groupRoot)) {
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
            String bannerImage = (String)body.get("bannerImageUrl");

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
                if(image != null && !image.isEmpty()) {
                    groupNew.setImage(image);
                }
                if(bannerImage != null && !bannerImage.isEmpty()) {
                    groupNew.setBannerImage(bannerImage);
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
                return;
            }

            throw new GroupException("Apenas Administradores podem criar subgrupos.");

        });
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            String name = (String)body.get("name");
            String description = (String)body.get("description");
            String groupType = (String)body.get("type");
            String image = (String)body.get("imageUrl");
            String bannerImage = (String)body.get("bannerImageUrl");

            Boolean canCreateGroup = (Boolean)body.get("canCreateGroup");
            Boolean publicGroup = (Boolean)body.get("publicGroup");
            Boolean canEnter = (Boolean)body.get("canEnter");

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            User user = userService.getUserInSession();

            if(groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(name != null && !name.isEmpty()) {
                    groupEdit.setName(name);
                }
                if(description != null && !description.isEmpty()) {
                    groupEdit.setDescription(description);
                }
                if(groupType != null && !groupType.isEmpty()) {
                    groupEdit.setType(GroupType.valueOf(groupType));
                }
                if(image != null && !image.isEmpty()) {
                    groupEdit.setImage(image);
                }
                if(bannerImage != null && !bannerImage.isEmpty()) {
                    groupEdit.setBannerImage(bannerImage);
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
                return;
            }

            throw new GroupException("Falha ao editar grupo");

        });
    }

    @PostMapping(value = "/participant/enter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_enter(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(!groupEdit.isCanEnter()) {
                throw new GroupException("Grupo não permite entrada de participantes.");
            }

            User user = userService.getUserInSession();

            if(groupEdit.isCanEnter() || groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.addParticipantToGroup(groupEdit, user.getProfile())) {
                    response.message = "Você entrou no Grupo.";
                    return;
                } else {
                    throw new GroupException("Você já esta neste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

        });
    }

    @PostMapping(value = "/participant/exit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_exit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            User user = userService.getUserInSession();

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(groupService.removeParticipantFromGroup(groupEdit, user.getProfile())) {
                response.message = "Você saiu do Grupo.";
            } else {
                throw new GroupException("Você não está neste Grupo.");
            }

        });
    }

    @PostMapping(value = "/participant/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_add(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            String participant = (String)body.get("participant");
            if(participant == null) {
                throw new GroupException("Parâmetro participant é nulo.");
            }

            User participantUser = null;
            if(participant != null && !participant.isEmpty()) {
                if (participant.contains("@")) {
                    participantUser = (User) userService.findFirstByEmail(participant);
                } else {
                    participantUser = (User) userService.loadUserByUsername(participant);
                }
            }

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            User user = userService.getUserInSession();

            if(participantUser != null && groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.addParticipantToGroup(groupEdit, participantUser.getProfile())) {
                    response.message = "Participante adicionado com sucesso.";
                    return;
                } else {
                    throw new GroupException("Participante já esta neste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

        });
    }

    @PostMapping(value = "/participant/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_remove(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            String participant = (String)body.get("participant");
            if(participant == null) {
                throw new GroupException("Parâmetro participant é nulo.");
            }

            User participantUser = null;
            if(participant != null && !participant.isEmpty()) {
                if (participant.contains("@")) {
                    participantUser = (User) userService.findFirstByEmail(participant);
                } else {
                    participantUser = (User) userService.loadUserByUsername(participant);
                }
            }

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            User user = userService.getUserInSession();

            if(participantUser != null && groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.removeParticipantFromGroup(groupEdit, participantUser.getProfile())) {
                    response.message = "Participante removido com sucesso.";
                    return;
                } else {
                    throw new GroupException("Participante não faz parte deste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

        });
    }

    @PostMapping(value = "/participant/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {
                response.body.put("participants", group.getParticipants());
                return;
            }

            throw new GroupException("Falha ao listar participante ao grupo");

        });
    }

    @PostMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

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
                return;
            }

            throw new GroupException("Erro ao executar operação.");

        });
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

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
                return;
            }

            throw new GroupException("Erro ao executar operação.");

        });
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if (group != null) {
                response.body.put("group", group);
                return;
            }

            throw new GroupException("Falha ao obter grupo.");

        });
    }

    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_subgroup(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {
                Collection<Group> subgroupList = group.getSubGroups();
                response.body.put("subgroups", subgroupList);
                return;
            }

            throw new GroupException("Falha ao listar grupo.");

        });
    }

    @PostMapping(value = "/parents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_available_parents(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Collection<Group> groups = groupService.findAll().stream()
                .filter(Group::isCanCreateGroup)
                .toList();

            response.body.put("groups", groups);

        });
    }

    @PostMapping(value = "/folders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_folders(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            response.body.put("folders", group.getFolders());

        });
    }

    @PostMapping(value = "/current-organization", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response currentOrganization() {
        return Response.buildResponse(response -> {
            try {
                response.body.put("organization", groupService.getOrganizationBasedInDomain());
            } catch (Exception e) {
                response.body.put("organization", null);
            }
        });
    }

    // get image of group
    @GetMapping(value = "/image/{groupId}")
    public ResponseEntity<Void> get_image(@PathVariable String groupId) {
        Group group = groupService.findFirstById(groupId);
        if(group != null) {
            if(group.getImage() != null) {
                String urlImage = (group.getImage().startsWith("/")) ? "/api" + group.getImage() : group.getImage();
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlImage)).build();
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
    }

    // get image banner of group
    @GetMapping(value = "/banner/{groupId}")
    public ResponseEntity<Void> get_image_banner(@PathVariable String groupId) {
        Group group = groupService.findFirstById(groupId);
        if(group != null) {
            if(group.getBannerImage() != null) {
                String urlImage = (group.getBannerImage().startsWith("/")) ? "/api" + group.getBannerImage() : group.getBannerImage();
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlImage)).build();
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
    }
}
