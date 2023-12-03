package me.universi.group.controller;

import java.util.*;
import java.util.stream.Collectors;
import me.universi.api.entities.Response;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group/settings/features")
public class GroupFeaturesController {
    private final GroupService groupService;
    private final UserService userService;

    public GroupFeaturesController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    //create group feature
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response features_create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String name = (String)body.get("name");
            String description = (String)body.get("description");
            Boolean enabled = (Boolean)body.get("enabled");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.addFeature(group,
                            name,
                            description,
                            enabled
                    )) {
                        response.message = "Features criadas com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Feature não existe.");
                    }
                }
            }

            throw new GroupException("Falha ao criar feature.");

        });
    }

    // edit group features
    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response features_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String groupFeatureId = (String)body.get("groupFeatureId");

            Boolean enabled = (Boolean)body.get("enabled");
            String description = (String)body.get("description");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.editFeature(group,
                            groupFeatureId,
                            enabled,
                            description
                    )) {
                        response.message = "Features editadas com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Feature não existe.");
                    }
                }
            }

            throw new GroupException("Falha ao editar features.");

        });
    }

    // delete group feature
    @PostMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response features_delete(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            String groupFeatureId = (String)body.get("groupFeatureId");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {
                User user = userService.getUserInSession();

                if(groupService.verifyPermissionToEditGroup(group, user)) {
                    if(groupService.deleteFeature(group,
                            groupFeatureId
                    )) {
                        response.message = "Feature deletada com sucesso.";
                        return;
                    } else {
                        throw new GroupException("Feature não existe.");
                    }
                }
            }

            throw new GroupException("Falha ao deletar feature.");

        });
    }

    // list group features
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response features_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object groupId =   body.get("groupId");
            Object groupPath = body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {

                if(!groupService.canEditGroup(group)) {
                    throw new GroupException("Você não tem permissão para gerenciar este grupo.");
                }

                Collection<me.universi.group.entities.GroupSettings.GroupFeatures> features = group.getGroupSettings().features;

                List<me.universi.group.entities.GroupSettings.GroupFeatures> featuresList = features.stream()
                        .sorted(Comparator.comparing(me.universi.group.entities.GroupSettings.GroupFeatures::getAdded).reversed())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                response.body.put("features", featuresList);
                return;
            }

            throw new GroupException("Falha ao listar features.");

        });
    }
}
