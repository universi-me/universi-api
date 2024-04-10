package me.universi.group.controller;

import java.util.Map;
import me.universi.api.entities.Response;
import me.universi.group.entities.Group;
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
@RequestMapping("/api/group/settings/environments")
public class GroupEnvironmentController {
    private final GroupService groupService;
    private final UserService userService;

    public GroupEnvironmentController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    // edit group environment
    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response environment_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Group group = groupService.getOrganizationBasedInDomainIfExist();

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.editEnvironment(group,
                            (Boolean)body.get("signup_enabled"), (Boolean)body.get("signup_confirm_account_enabled"),
                            (Boolean)body.get("login_google_enabled"), (String)body.get("google_client_id"),
                            (Boolean)body.get("recaptcha_enabled"), (String)body.get("recaptcha_api_key"),
                            (String)body.get("recaptcha_api_project_id"), (String)body.get("recaptcha_site_key"),
                            (Boolean)body.get("keycloak_enabled"), (String)body.get("keycloak_client_id"),
                            (String)body.get("keycloak_client_secret"), (String)body.get("keycloak_realm"),
                            (String)body.get("keycloak_url"), (String)body.get("keycloak_redirect_url")
                    )) {
                        response.message = "Variáveis Ambiente atualizada com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Variáveis Ambiente não existe.");
                    }
                }

                throw new GroupException("Falha ao editar Variáveis Ambiente.");
            }

        });
    }

    // list group environment
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response environment_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Group group = groupService.getOrganizationBasedInDomainIfExist();

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    response.body.put("environments", groupService.getGroupEnvironment(group));
                    return;
                }

                throw new GroupException("Falha ao listar o ambiente.");
            }

        });
    }
}
