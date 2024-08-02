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

            UUID groupId =   CastingUtil.getUUID("groupId").orElse(null);
            String groupPath = CastingUtil.getString("groupPath").orElse(null);

            String primaryColor             = CastingUtil.getString("primary_color").orElse(null);
            String secondaryColor           = CastingUtil.getString("secondary_color").orElse(null);
            String tertiaryColor            = CastingUtil.getString("tertiary_color").orElse(null);
            String backgroundColor          = CastingUtil.getString("background_color").orElse(null);
            String cardBackgroundColor      = CastingUtil.getString("card_background_color").orElse(null);
            String cardItemColor            = CastingUtil.getString("card_item_color").orElse(null);
            String fontColorV1              = CastingUtil.getString("font_color_v1").orElse(null);
            String fontColorV2              = CastingUtil.getString("font_color_v2").orElse(null);
            String fontColorV3              = CastingUtil.getString("font_color_v3").orElse(null);
            String fontColorV4              = CastingUtil.getString("font_color_v4").orElse(null);
            String fontColorDisabled        = CastingUtil.getString("font_color_disabled").orElse(null);
            String skills1Color             = CastingUtil.getString("skills_1_color").orElse(null);
            String buttonHoverColor         = CastingUtil.getString("button_hover_color").orElse(null);
            String fontColorAlert           = CastingUtil.getString("font_color_alert").orElse(null);
            String fontColorSuccess         = CastingUtil.getString("font_color_success").orElse(null);
            String wrongInvalidColor        = CastingUtil.getString("wrong_invalid_color").orElse(null);

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            RolesService.getInstance().checkIsAdmin(group);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.editTheme(group,
                            primaryColor,
                            secondaryColor,
                            tertiaryColor,
                            backgroundColor,
                            cardBackgroundColor,
                            cardItemColor,
                            fontColorV1,
                            fontColorV2,
                            fontColorV3,
                            fontColorV4,
                            fontColorDisabled,
                            skills1Color,
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
