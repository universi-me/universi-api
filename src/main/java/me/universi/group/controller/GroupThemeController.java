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
@RequestMapping("/api/group/settings/theme")
public class GroupThemeController {
    private final GroupService groupService;
    private final UserService userService;

    public GroupThemeController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    // edit group theme
    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response theme_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String primary_color             = (String)body.get("primary_color");
            String secondary_color           = (String)body.get("secondary_color");
            String tertiary_color            = (String)body.get("tertiary_color");
            String background_color          = (String)body.get("background_color");
            String card_background_color     = (String)body.get("card_background_color");
            String card_item_color           = (String)body.get("card_item_color");
            String font_color_v1             = (String)body.get("font_color_v1");
            String font_color_v2             = (String)body.get("font_color_v2");
            String font_color_v3             = (String)body.get("font_color_v3");
            String font_color_v4             = (String)body.get("font_color_v4");
            String font_color_v5             = (String)body.get("font_color_v5");
            String font_disabled_color       = (String)body.get("font_disabled_color");
            String skills_1_color            = (String)body.get("skills_1_color");
            String button_hover_color        = (String)body.get("button_hover_color");
            String alert_color               = (String)body.get("alert_color");
            String success_color             = (String)body.get("success_color");
            String wrong_invalid_color       = (String)body.get("wrong_invalid_color");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.editTheme(group,
                            primary_color,
                            secondary_color,
                            tertiary_color,
                            background_color,
                            card_background_color,
                            card_item_color,
                            font_color_v1,
                            font_color_v2,
                            font_color_v3,
                            font_color_v4,
                            font_color_v5,
                            font_disabled_color,
                            skills_1_color,
                            button_hover_color,
                            alert_color,
                            success_color,
                            wrong_invalid_color
                    )) {
                        response.message = "Tema editado com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Tema não existe.");
                    }
                }
            }

            throw new GroupException("Falha ao editar tema.");

        });
    }
}
