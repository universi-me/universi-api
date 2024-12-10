package me.universi.group.services;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.exceptions.GroupException;
import me.universi.profile.entities.Profile;
import org.springframework.stereotype.Service;

@Service
public class GroupAdminService {
    private final GroupService groupService;

    public GroupAdminService(GroupService groupService) {
        this.groupService = groupService;
    }

    // list administrators of group
    public List<Profile> listAdmininistratorsByGroupId(UUID groupId) {

        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, null);

        if (group == null)
            throw new GroupException("Falha ao listar administradores do grupo");

        if(!groupService.canEditGroup(group))
            throw new GroupException("Você não tem permissão para gerenciar este grupo.");

        List<Profile> profiles = groupService.getAdministrators(group).stream()
                .sorted(Comparator.comparing(ProfileGroup::getJoined).reversed())
                .map(ProfileGroup::getProfile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return profiles;
    }
}
