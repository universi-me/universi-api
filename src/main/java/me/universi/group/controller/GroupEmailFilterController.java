package me.universi.group.controller;

import java.util.*;
import java.util.stream.Collectors;
import me.universi.api.entities.Response;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupSettings.GroupEmailFilter;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;

import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group/settings/email-filter")
public class GroupEmailFilterController {
    private final GroupService groupService;
    private final UserService userService;

    public GroupEmailFilterController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    // add email filter to group
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response email_filter_add(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String email = (String)body.get("email");

            Boolean enabled = (Boolean)body.get("enabled");
            Object type = body.get("type");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.addEmailFilter(group, email, type, enabled)) {
                        response.message = "Filtro adicionado com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Filtro já existe.");
                    }
                }
            }

            throw new GroupException("Falha ao adicionar filtro.");

        });
    }

    // edit email filter to group
    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response email_filter_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String groupEmailFilterId = (String)body.get("groupEmailFilterId");

            String email = (String)body.get("email");
            Boolean enabled = (Boolean)body.get("enabled");
            Object type = body.get("type");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.editEmailFilter(group, groupEmailFilterId, email, type, enabled)) {
                        response.message = "Filtro editado com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Filtro não existe.");
                    }
                }
            }

            throw new GroupException("Falha ao editar filtro.");

        });
    }

    // delete email filter
    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response email_filter_delete(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String groupEmailFilterId = (String)body.get("groupEmailFilterId");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.deleteEmailFilter(group, groupEmailFilterId)) {
                        response.message = "Filtro deletado com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Filtro não existe.");
                    }
                }
            }

        });
    }

    // list email filter of group
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response email_filter_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {

                if(!groupService.canEditGroup(group)) {
                    throw new GroupException("Você não tem permissão para gerenciar este grupo.");
                }

                Collection<GroupEmailFilter> emailFilters = group.getGroupSettings().getFilterEmails();

                List<GroupEmailFilter> emailFiltersList = emailFilters.stream()
                        .sorted(Comparator.comparing(GroupEmailFilter::getAdded).reversed())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                response.body.put("emailFilters", emailFiltersList);
                return;
            }

            throw new GroupException("Falha ao listar filtros de email.");

        });
    }
}
