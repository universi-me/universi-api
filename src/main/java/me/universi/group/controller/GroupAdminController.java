package me.universi.group.controller;

import java.util.*;
import java.util.stream.Collectors;
import me.universi.api.entities.Response;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupAdmin;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group/settings/admin")
public class GroupAdminController {
    private final GroupService groupService;
    private final UserService userService;

    public GroupAdminController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    // add administrator to group
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response admin_add(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String admin = (String)body.get("username");
            if(admin == null || admin.isEmpty()) {
                throw new GroupException("Parâmetro username é nulo.");
            }

            User adminUser;
            try {
                adminUser = (User) userService.loadUserByUsername(admin);
            } catch (Exception e) {
                throw new GroupException("Usuário não encontrado.");
            }

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.addAdministrator(group, adminUser.getProfile())) {
                        response.message = "Administrador adicionado com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Administrador já existe.");
                    }
                }
            }

            throw new GroupException("Falha ao adicionar administrador.");

        });
    }

    // remove administrator from group
    @PostMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response admin_remove(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String admin = (String)body.get("username");
            if(admin == null || admin.isEmpty()) {
                throw new GroupException("Parâmetro username é nulo.");
            }

            User adminUser;
            try {
                adminUser = (User) userService.loadUserByUsername(admin);
            } catch (Exception e) {
                throw new GroupException("Usuário não encontrado.");
            }

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.removeAdministrator(group, adminUser.getProfile())) {
                        response.message = "Administrador removido com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Administrador não existe.");
                    }
                }
            }

            throw new GroupException("Falha ao remover administrador.");

        });
    }

    // list administrators of group
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response admin_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {

                if(!groupService.canEditGroup(group)) {
                    throw new GroupException("Você não tem permissão para gerenciar este grupo.");
                }

                Collection<GroupAdmin> administrators = group.getAdministrators();

                List<Profile> profiles = administrators.stream()
                        .sorted(Comparator.comparing(GroupAdmin::getAdded).reversed())
                        .map(GroupAdmin::getProfile)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                response.body.put("administrators", profiles);
                return;
            }

            throw new GroupException("Falha ao listar filtros de email.");

        });
    }
}
