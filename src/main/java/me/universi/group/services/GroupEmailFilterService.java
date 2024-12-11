package me.universi.group.services;

import java.util.*;
import java.util.stream.Collectors;
import me.universi.group.DTO.CreateEmailFilterDTO;
import me.universi.group.DTO.UpdateEmailFilterDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupSettings.GroupEmailFilter;
import me.universi.group.enums.GroupEmailFilterType;
import me.universi.group.exceptions.GroupException;
import me.universi.group.repositories.GroupEmailFilterRepository;
import me.universi.group.repositories.GroupRepository;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ConvertUtil;
import org.springframework.stereotype.Service;

@Service
public class GroupEmailFilterService {

    private final GroupService groupService;
    private final UserService userService;
    private final GroupEmailFilterRepository groupEmailFilterRepository;
    private final GroupRepository groupRepository;

    public GroupEmailFilterService(GroupService groupService, UserService userService, GroupEmailFilterRepository groupEmailFilterRepository, GroupRepository groupRepository) {
        this.groupService = groupService;
        this.userService = userService;
        this.groupEmailFilterRepository = groupEmailFilterRepository;
        this.groupRepository = groupRepository;
    }

    // add email filter to group
    public GroupEmailFilter createEmailFilter(CreateEmailFilterDTO createEmailFilterDTO) {
        Group group = groupService.getGroupByGroupIdOrGroupPath(createEmailFilterDTO.groupId(), null);

        RolesService.getInstance().checkIsAdmin(group);

        if(group != null) {
            User user = userService.getUserInSession();
            if(groupService.verifyPermissionToEditGroup(group, user)) {
                GroupEmailFilter groupEmailFilter = new GroupEmailFilter();
                groupEmailFilter.groupSettings = group.groupSettings;
                groupEmailFilter.email = createEmailFilterDTO.email();
                if(createEmailFilterDTO.type() != null) {
                    groupEmailFilter.type = GroupEmailFilterType.valueOf(String.valueOf(createEmailFilterDTO.type()));
                }
                if(createEmailFilterDTO.enabled() != null) {
                    groupEmailFilter.enabled = createEmailFilterDTO.enabled();
                }
                groupEmailFilterRepository.save(groupEmailFilter);
                return groupEmailFilter;
            }
        }

        throw new GroupException("Falha ao adicionar filtro.");
    }

    public GroupEmailFilter updateEmailFilter(UpdateEmailFilterDTO updateEmailFilterDTO) {
        Group group = getGroupByGroupEmailFilterId(updateEmailFilterDTO.groupEmailFilterId());

        RolesService.getInstance().checkIsAdmin(group);

        if(group != null) {
            User user = userService.getUserInSession();
            if(groupService.verifyPermissionToEditGroup(group, user)) {
                if(groupEmailFilterRepository.existsByGroupSettingsIdAndId(group.groupSettings.getId(), updateEmailFilterDTO.groupEmailFilterId())) {
                    GroupEmailFilter groupEmailFilter = groupEmailFilterRepository.findFirstByGroupSettingsIdAndId(group.groupSettings.getId(), updateEmailFilterDTO.groupEmailFilterId());
                    if(updateEmailFilterDTO.email() != null) {
                        groupEmailFilter.email = updateEmailFilterDTO.email();
                    }
                    if(updateEmailFilterDTO.type() != null) {
                        groupEmailFilter.type = GroupEmailFilterType.valueOf(String.valueOf(updateEmailFilterDTO.type()));
                    }
                    if(updateEmailFilterDTO.enabled() != null) {
                        groupEmailFilter.enabled = updateEmailFilterDTO.enabled();
                    }
                    groupEmailFilterRepository.save(groupEmailFilter);
                    return groupEmailFilter;
                } else {
                    throw new GroupException("Filtro não existe.");
                }
            }
        }

        throw new GroupException("Falha ao editar filtro.");
    }

    public boolean deleteEmailFilter(UUID groupEmailFilterId) {
        Group group = getGroupByGroupEmailFilterId(groupEmailFilterId);

        if(group != null) {

            RolesService.getInstance().checkIsAdmin(group);

            User user = userService.getUserInSession();

            if(groupService.verifyPermissionToEditGroup(group, user)) {
                if(groupEmailFilterRepository.existsByGroupSettingsIdAndId(group.groupSettings.getId(), groupEmailFilterId)) {
                    GroupEmailFilter groupEmailFilter = groupEmailFilterRepository.findFirstByGroupSettingsIdAndId(group.groupSettings.getId(), groupEmailFilterId);
                    groupEmailFilter.setRemoved(ConvertUtil.getDateTimeNow());
                    groupEmailFilter.setDeleted(true);
                    groupEmailFilterRepository.save(groupEmailFilter);
                    return true;
                } else {
                    throw new GroupException("Filtro não existe.");
                }
            }
        }

        throw new GroupException("Falha ao remover filtro.");
    }

    public List<GroupEmailFilter> listEmailFilter(UUID groupId) {
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, null);

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

            return emailFiltersList;
        }

        throw new GroupException("Falha ao listar filtros de email.");
    }

    // determining group by groupEmailFilterId
    public Group getGroupByGroupEmailFilterId(UUID groupEmailFilterId) {
        GroupEmailFilter groupEmailFilter = groupEmailFilterRepository.findFirstById(groupEmailFilterId).orElseThrow(() -> new GroupException("Filtro não existe."));
        UUID groupSettingId = groupEmailFilter.groupSettings.getId();
        Group group = groupRepository.findFirstByGroupSettingsId(groupSettingId);
        if(group != null) {
            return group;
        }
        throw new GroupException("Falha ao determinar grupo por filtro de email.");
    }
}
