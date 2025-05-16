package me.universi.group.services;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import me.universi.Sys;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.group.DTO.AddGroupParticipantDTO;
import me.universi.group.DTO.CompetenceInfoDTO;
import me.universi.group.DTO.RemoveGroupParticipantDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.repositories.ProfileGroupRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.services.RoleService;
import me.universi.user.services.UserService;

import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

@Service
public class GroupParticipantService {
    private final ProfileGroupRepository profileGroupRepository;
    private final GroupService groupService;
    private final UserService userService;

    public GroupParticipantService(ProfileGroupRepository profileGroupRepository, GroupService groupService, UserService userService) {
        this.profileGroupRepository = profileGroupRepository;
        this.groupService = groupService;
        this.userService = userService;
    }

    public static GroupParticipantService getInstance() {
        return Sys.context.getBean( "groupParticipantService" , GroupParticipantService.class );
    }

    public boolean isParticipant( Group group ) {
        return isParticipant( group, userService.getUserInSession().getProfile() );
    }

    public boolean isParticipant( Group group, Profile profile ) {
        return group != null
            && profileGroupRepository.existsByGroupIdAndProfileId( group.getId(), profile.getId() );
    }

    public @NotNull ProfileGroup join( UUID groupId ) {
        var group = groupService.findOrThrow( groupId );
        var profile = userService.getUserInSession().getProfile();

        if ( isParticipant( group, profile ) )
            throw new UniversiConflictingOperationException( "Você já participa deste grupo." );

        if( !group.isCanEnter() )
            throw new UniversiForbiddenAccessException( "O grupo não permite entrada de novos participantes." );

        var profileGroup = new ProfileGroup();
        profileGroup.setGroup( group );
        profileGroup.setProfile( profile );
        profileGroup.role = RoleService.getInstance().getGroupMemberRole( group );

        return profileGroupRepository.saveAndFlush( profileGroup );
    }

    public void leave( UUID groupId ) {
        var group = groupService.findOrThrow( groupId );
        var profile = userService.getUserInSession().getProfile();

        if( group.isRootGroup() )
            throw new UniversiForbiddenAccessException( "Você não pode sair deste grupo." );

        var profileGroup = profileGroupRepository.findFirstByGroupAndProfile( group, profile );
        if ( profileGroup == null )
            // is not participant
            return;

        profileGroupRepository.delete( profileGroup );
    }

    public ProfileGroup addParticipant( AddGroupParticipantDTO dto ) {
        var participant = ProfileService.getInstance().findByIdOrUsernameOrThrow( dto.participant() );
        Group group = groupService.findByIdOrPathOrThrow( dto.group() );

        RoleService.getInstance().checkPermission( group, FeaturesTypes.PEOPLE,Permission.READ_WRITE );
        if ( isParticipant( group, participant ) )
            throw new UniversiConflictingOperationException( "O usuário já participa do grupo" );

        var role = dto.role().map( roleId -> {
            var foundRole = RoleService.getInstance().findOrThrow( roleId );
            if ( !foundRole.group.getId().equals( group.getId() ) )
                throw new UniversiConflictingOperationException( "O Papel indicado não pertence ao grupo" );
            return foundRole;
        } ).orElse(
            RoleService.getInstance().getGroupMemberRole( group )
        );

        var profileGroup = new ProfileGroup();
        profileGroup.setGroup( group );
        profileGroup.setProfile( participant );
        profileGroup.role = role;

        return profileGroupRepository.saveAndFlush( profileGroup );
    }

    public void removeParticipant( RemoveGroupParticipantDTO dto ) {
        Group group = groupService.findByIdOrPathOrThrow( dto.group() );

        if( group.isRootGroup() )
            throw new UniversiForbiddenAccessException( "Você não pode remover alguém deste grupo." );

        RoleService.getInstance().checkPermission( group, FeaturesTypes.PEOPLE, Permission.READ_WRITE_DELETE );

        var participant = ProfileService.getInstance().findByIdOrUsernameOrThrow( dto.participant() );
        var profileGroup = profileGroupRepository.findFirstByGroupAndProfile( group, participant );
        if ( profileGroup == null )
            // is not participant
            return;

        profileGroupRepository.delete( profileGroup );
    }

    public List<Profile> listParticipantsByGroupId(UUID groupId) {

        Group group = groupService.findOrThrow( groupId );

        RoleService.getInstance().checkPermission(group, FeaturesTypes.PEOPLE, Permission.READ);

        return group.getParticipants()
            .stream()
            .sorted( Comparator.comparing(ProfileGroup::getJoined).reversed() )
            .map( ProfileGroup::getProfile )
            .filter( p -> !p.isHidden() )
            .toList();
    }

    public List<CompetenceInfoDTO> getGroupCompetencesByGroupId(UUID id) {
        RoleService.getInstance().checkPermission(id.toString(), FeaturesTypes.COMPETENCE, Permission.READ);

        Group group = groupService.findOrThrow( id );

        return groupService.getGroupCompetences(group);
    }
}
