package me.universi.group.controller;

import java.util.Map;
import java.util.UUID;

import me.universi.api.entities.Response;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

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
    public Response themeEdit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            UUID groupId = CastingUtil.getUUID(body.get("groupId")).orElse(null);
            String groupPath = CastingUtil.getString(body.get("groupPath")).orElse(null);

            String primaryColor        = CastingUtil.getString(body.get("primary_color")).orElse(null);
            String secondaryColor      = CastingUtil.getString(body.get("secondary_color")).orElse(null);
            String backgroundColor     = CastingUtil.getString(body.get("background_color")).orElse(null);
            String cardBackgroundColor = CastingUtil.getString(body.get("card_background_color")).orElse(null);
            String cardItemColor       = CastingUtil.getString(body.get("card_item_color")).orElse(null);
            String fontColorV1         = CastingUtil.getString(body.get("font_color_v1")).orElse(null);
            String fontColorV2         = CastingUtil.getString(body.get("font_color_v2")).orElse(null);
            String fontColorV3         = CastingUtil.getString(body.get("font_color_v3")).orElse(null);
            String fontColorLinks      = CastingUtil.getString(body.get("font_color_links")).orElse(null);
            String fontColorDisabled   = CastingUtil.getString(body.get("font_color_disabled")).orElse(null);
            String buttonHoverColor    = CastingUtil.getString(body.get("button_hover_color")).orElse(null);
            String fontColorAlert      = CastingUtil.getString(body.get("font_color_alert")).orElse(null);
            String fontColorSuccess    = CastingUtil.getString(body.get("font_color_success")).orElse(null);
            String wrongInvalidColor   = CastingUtil.getString(body.get("wrong_invalid_color")).orElse(null);

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.editTheme(group,
                            primaryColor,
                            secondaryColor,
                            backgroundColor,
                            cardBackgroundColor,
                            cardItemColor,
                            fontColorV1,
                            fontColorV2,
                            fontColorV3,
                            fontColorLinks,
                            fontColorDisabled,
                            buttonHoverColor,
                            fontColorAlert,
                            fontColorSuccess,
                            wrongInvalidColor
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
