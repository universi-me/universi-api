package me.universi.group.services;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import me.universi.group.DTO.CompetenceInfoDTO;
import me.universi.group.DTO.UpdateGroupParticipantDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.exceptions.GroupException;
import me.universi.profile.entities.Profile;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class GroupParticipantService {
    private final GroupService groupService;
    private final UserService userService;

    public GroupParticipantService(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    public boolean joinGroup(UUID groupId) {

        Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, null);

        if(groupEdit.isRootGroup()) {
            throw new GroupException("Você não pode sair do Grupo.");
        }

        if(!groupEdit.isCanEnter()) {
            throw new GroupException("Grupo não permite entrada de participantes.");
        }

        User user = userService.getUserInSession();

        if(groupEdit.isCanEnter() || groupService.verifyPermissionToEditGroup(groupEdit, user)) {
            if(groupService.addParticipantToGroup(groupEdit, user.getProfile())) {
                return true;
            } else {
                throw new GroupException("Você já esta neste Grupo.");
            }
        }

        throw new GroupException("Falha ao entrar ao grupo");
    }

    public boolean leaveGroup(UUID groupId) {

        Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, null);

        if(groupEdit.isRootGroup()) {
            throw new GroupException("Você não pode sair do Grupo.");
        }

        User user = userService.getUserInSession();

        if(groupService.removeParticipantFromGroup(groupEdit, user.getProfile())) {
            return true;
        } else {
            throw new GroupException("Você não está neste Grupo.");
        }

    }

    public boolean addParticipantGroup(UpdateGroupParticipantDTO updateGroupParticipantDTO) {
        if(updateGroupParticipantDTO.participant() == null) {
            throw new GroupException("Parâmetro participant é nulo.");
        }

        User participantUser = null;
        if(updateGroupParticipantDTO.participant() != null && !updateGroupParticipantDTO.participant().isEmpty()) {
            if (updateGroupParticipantDTO.participant().contains("@")) {
                participantUser = (User) userService.findFirstByEmail(updateGroupParticipantDTO.participant());
            } else {
                participantUser = (User) userService.loadUserByUsername(updateGroupParticipantDTO.participant());
            }
        }

        Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(updateGroupParticipantDTO.groupId(), null);

        User user = userService.getUserInSession();

        if(participantUser != null && groupService.verifyPermissionToEditGroup(groupEdit, user)) {
            if(groupService.addParticipantToGroup(groupEdit, participantUser.getProfile())) {
                return true;
            } else {
                return false;
            }
        }

        throw new GroupException("Falha ao adicionar participante ao grupo");
    }

    public boolean removeParticipantGroup(UpdateGroupParticipantDTO updateGroupParticipantDTO) {
        if(updateGroupParticipantDTO.participant() == null) {
            throw new GroupException("Parâmetro participant é nulo.");
        }

        User participantUser = null;
        if(updateGroupParticipantDTO.participant() != null && !updateGroupParticipantDTO.participant().isEmpty()) {
            if (updateGroupParticipantDTO.participant().contains("@")) {
                participantUser = (User) userService.findFirstByEmail(updateGroupParticipantDTO.participant());
            } else {
                participantUser = (User) userService.loadUserByUsername(updateGroupParticipantDTO.participant());
            }
        }

        Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(updateGroupParticipantDTO.groupId(), null);

        User user = userService.getUserInSession();

        if(participantUser != null && groupService.verifyPermissionToEditGroup(groupEdit, user)) {
            if(groupService.removeParticipantFromGroup(groupEdit, participantUser.getProfile())) {
                return true;
            } else {
                return false;
            }
        }

        throw new GroupException("Falha ao remover participante do grupo");
    }

    public List<Profile> listParticipantsByGroupId(UUID groupId) {

        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, null);

        RolesService.getInstance().checkPermission(group, FeaturesTypes.PEOPLE, Permission.READ);

        if(group != null) {
            Collection<ProfileGroup> participants = group.getParticipants();

            List<Profile> profiles = participants.stream()
                    .sorted(Comparator.comparing(ProfileGroup::getJoined).reversed())
                    .map(ProfileGroup::getProfile)
                    .filter(p -> p != null && !p.isHidden())
                    .collect(Collectors.toList());

            return profiles;
        }

        throw new GroupException("Falha ao listar participante ao grupo");
    }

    public List<CompetenceInfoDTO> getGroupCompetencesByGroupId(UUID id) {
        RolesService.getInstance().checkPermission(id.toString(), FeaturesTypes.COMPETENCE, Permission.READ);

        Group group = groupService.getGroupByGroupIdOrGroupPath(id, null);

        return groupService.getGroupCompetences(group);
    }
}
