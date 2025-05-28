package me.universi.group.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import me.universi.Sys;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.DTO.AddGroupParticipantDTO;
import me.universi.group.DTO.CompetenceFilterDTO;
import me.universi.group.DTO.CompetenceInfoDTO;
import me.universi.group.DTO.ProfileWithCompetencesDTO;
import me.universi.group.DTO.RemoveGroupParticipantDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.repositories.ProfileGroupRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.services.RoleService;
import me.universi.user.services.LoginService;

import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

@Service
public class GroupParticipantService {
    private final CompetenceService competenceService;
    private final ProfileGroupRepository profileGroupRepository;
    private final GroupService groupService;
    private final CompetenceTypeService competenceTypeService;
    private final RoleService roleService;
    private final LoginService loginService;

    public GroupParticipantService(ProfileGroupRepository profileGroupRepository, GroupService groupService, CompetenceTypeService competenceTypeService, CompetenceService competenceService, RoleService roleService, LoginService loginService) {
        this.profileGroupRepository = profileGroupRepository;
        this.groupService = groupService;
        this.competenceTypeService = competenceTypeService;
        this.competenceService = competenceService;
        this.roleService = roleService;
        this.loginService = loginService;
    }

    public static GroupParticipantService getInstance() {
        return Sys.context.getBean( "groupParticipantService" , GroupParticipantService.class );
    }

    public boolean isParticipant( Group group ) {
        return isParticipant( group, loginService.getUserInSession().getProfile() );
    }

    public boolean isParticipant( Group group, Profile profile ) {
        return group != null
            && profileGroupRepository.existsByGroupIdAndProfileId( group.getId(), profile.getId() );
    }

    public Optional<ProfileGroup> findByGroupAndProfile( @NotNull Group group, @NotNull Profile profile ) {
        return profileGroupRepository.findFirstByGroupAndProfile( group, profile );
    }

    public @NotNull ProfileGroup join( UUID groupId ) {
        var group = groupService.findOrThrow( groupId );
        var profile = loginService.getUserInSession().getProfile();

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

    public @NotNull ProfileGroup joinOrganization( @NotNull Profile profile ) {
        var organization = profile.getUser().getOrganization();
        var existingProfileGroup = findByGroupAndProfile( organization, profile );

        if ( existingProfileGroup.isPresent() )
            return existingProfileGroup.get();

        var profileGroup = new ProfileGroup();
        profileGroup.setGroup( organization );
        profileGroup.setProfile( profile );
        profileGroup.role = RoleService.getInstance().getGroupMemberRole( organization );

        return profileGroupRepository.saveAndFlush( profileGroup );
    }

    public void leave( UUID groupId ) {
        var group = groupService.findOrThrow( groupId );
        var profile = loginService.getUserInSession().getProfile();

        if( group.isRootGroup() )
            throw new UniversiForbiddenAccessException( "Você não pode sair deste grupo." );

        findByGroupAndProfile( group, profile )
            .ifPresent( profileGroupRepository::delete );
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
        findByGroupAndProfile( group, participant )
            .ifPresent( profileGroupRepository::delete );
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

    private record CompetenceTypeWithLevel( CompetenceType type, int level ){}
    private Predicate<Profile> getFilterByCompetence( CompetenceFilterDTO dto ) {
        if ( dto.competences() == null || dto.competences().isEmpty() )
            return p -> true;

        var competenceTypesWithLevel = dto.competences().stream()
            .map( c -> new CompetenceTypeWithLevel( competenceTypeService.findByIdOrNameOrThrow( c.id() ), c.level() ) )
            .toList();

        if ( dto.matchEveryCompetence() )
            return p -> {
                var pwc = new ProfileWithCompetencesDTO( p, competenceService.findByProfile( p.getId() ) );
                return competenceTypesWithLevel.stream().allMatch( c -> pwc.hasCompetence( c.type().getId(), c.level() ) );
            };

        else
            return p -> {
                var pwc = new ProfileWithCompetencesDTO( p, competenceService.findByProfile( p.getId() ) );
                return competenceTypesWithLevel.stream().anyMatch( c -> pwc.hasCompetence( c.type().getId(), c.level() ) );
            };
    }

    public List<Profile> filterParticipants( CompetenceFilterDTO dto ) {
        var group = groupService.findByIdOrPathOrThrow( dto.group() );
        roleService.checkPermission( group, FeaturesTypes.PEOPLE, Permission.READ );

        return group.getParticipants().stream()
            .sorted( Comparator.comparing( ProfileGroup::getJoined ).reversed() )
            .map( ProfileGroup::getProfile )
            .filter( ProfileService.getInstance()::isValid )
            .filter( getFilterByCompetence( dto ) )
            .toList();
    }
}
