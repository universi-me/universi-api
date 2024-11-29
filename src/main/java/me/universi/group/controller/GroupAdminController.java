package me.universi.group.controller;

import java.util.*;
import java.util.stream.Collectors;
import me.universi.api.entities.Response;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.util.CastingUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group/settings/admin")
public class GroupAdminController {
    private final GroupService groupService;

    public GroupAdminController(GroupService groupService) {
        this.groupService = groupService;
    }

    // list administrators of group
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response admin_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var groupId = CastingUtil.getUUID(body.get("groupId")).orElse(null);
            var groupPath = CastingUtil.getUUID(body.get("groupPath")).orElse(null);

            if (groupId == null && groupPath == null) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                throw new GroupException("Parâmetros 'groupId' e 'groupPath' não informados ou inválidos");
            }

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if (group == null)
                throw new GroupException("Falha ao listar administradores do grupo");

            if(!groupService.canEditGroup(group))
                throw new GroupException("Você não tem permissão para gerenciar este grupo.");

            List<Profile> profiles = groupService.getAdministrators(group).stream()
                    .sorted(Comparator.comparing(ProfileGroup::getJoined).reversed())
                    .map(ProfileGroup::getProfile)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            response.body.put("administrators", profiles);
        });
    }
}
