package me.universi.roles.services;

import java.util.*;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.interfaces.EntityService;
import me.universi.group.entities.Group;
import me.universi.group.repositories.ProfileGroupRepository;
import me.universi.group.services.GroupService;
import me.universi.roles.dto.CreateRoleDTO;
import me.universi.roles.dto.UpdateRoleDTO;
import me.universi.roles.entities.Roles;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.enums.RoleType;
import me.universi.profile.entities.Profile;
import me.universi.roles.exceptions.RolesException;
import me.universi.roles.repositories.RolesRepository;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

@Service
public class RolesService extends EntityService<Roles> {
    private final UserService userService;
    private final ProfileService profileService;
    private final GroupService groupService;
    private final RolesRepository rolesRepository;
    private final ProfileGroupRepository profileGroupRepository;

    @Autowired
    public RolesService(UserService userService, ProfileService profileService, GroupService groupService, RolesRepository rolesRepository, ProfileGroupRepository profileGroupRepository) {
        this.userService = userService;
        this.profileService = profileService;
        this.groupService = groupService;
        this.rolesRepository = rolesRepository;
        this.profileGroupRepository = profileGroupRepository;

        this.entityName = "Papel";
    }

    public static RolesService getInstance() {
        return Sys.context.getBean("rolesService", RolesService.class);
    }

    @Override
    public Optional<Roles> find( UUID id ) {
        return rolesRepository.findById( id );
    }

    @Override
    public List<Roles> findAll() {
        return rolesRepository.findAll();
    }

    public Optional<Roles> findByNameAndGroup( String name, UUID groupId ) {
        return rolesRepository.findFirstByNameIgnoreCaseAndGroupId( name, groupId );
    }

    public Roles findByNameAndGroupOrThrow( String name, UUID groupId ) {
        return findByNameAndGroup( name, groupId )
            .orElseThrow( () -> makeNotFoundException( "nome e grupo", name + "' e '" + groupId) );
    }

    public Roles create( @NotNull CreateRoleDTO dto ) {
        var group = groupService.findByIdOrPathOrThrow( dto.group() );
        checkIsAdmin( group );

        var existingRole = findByNameAndGroup( dto.name(), group.getId() );
        if ( existingRole.isPresent() )
            throw new UniversiConflictingOperationException( "O grupo já possui um papel de nome '" + existingRole.get().name + "'" );

        var role = Roles.makeCustom(
            dto.name().trim(),
            dto.description(),
            group,
            Permission.READ
        );

        return rolesRepository.saveAndFlush( role );
    }

    public Roles update( @NotNull UUID id, @NotNull UpdateRoleDTO dto ) {
        var role = findOrThrow( id );
        checkPermissionToEdit( role );

        if ( dto.name() != null && !dto.name().isBlank() )
            role.name = dto.name();

        if ( dto.description() != null && !dto.description().isBlank() )
            role.description = dto.description();

        if ( dto.features() != null ) {
            dto.features().forEach( role::setPermission );
        }

        return rolesRepository.saveAndFlush( role );
    }

    public void delete( @NotNull UUID id ) {
        var role = findOrThrow( id );
        checkPermissionToDelete( role );

        // remove role from members before deleting
        var memberRole = getGroupMemberRole( role.group );

        var profileGroups = profileGroupRepository.findAllByRoleId( id );
        profileGroups.stream().forEach( pg -> {
            pg.role = memberRole;
        } );

        profileGroupRepository.saveAllAndFlush( profileGroups );
        rolesRepository.delete( role );
    }

    public Roles assignRole( @NotNull UUID roleId, @NotNull String profileIdOrUsername ) {
        var profile = profileService.findByIdOrUsernameOrThrow( profileIdOrUsername );
        var role = findOrThrow( roleId );

        checkIsAdmin( role.group );

        if ( !role.isCanBeAssigned() )
            throw new UniversiConflictingOperationException( "O papel não pode ser atribuído" );

        if( profileService.isSessionOfProfile( profile ) )
            throw new UniversiConflictingOperationException( "Você não pode alterar seu próprio papel" );

        if ( role.group.getAdmin().getId().equals( profile.getId() ) )
            throw new UniversiConflictingOperationException( "O papel do dono do grupo não pode ser alterado" );

        // todo: ProfileGroupService to handle ProfileGroup
        var profileGroup = profileGroupRepository.findFirstByGroupAndProfile( role.group, profile );
        if ( profileGroup == null )
            throw new UniversiConflictingOperationException( "Você só pode atribuir o papel à um membro do grupo" );

        profileGroup.role = role;
        return profileGroupRepository.saveAndFlush( profileGroup ).role;
    }

    public Collection<Roles> findByGroup( UUID groupId ) {
        Group group = groupService.findOrThrow( groupId );
        checkIsAdmin(group);

        return rolesRepository.findAllByGroup(group);
    }

    public boolean isAdmin(Profile profile, Group group) {
        if (profile == null) {
            throw new RolesException("Perfil não encontrado.");
        }
        if (group == null) {
            throw new RolesException("Grupo não encontrado.");
        }

        return getAssignedRoles(profile.getId(), group.getId()).getRoleType().equals(RoleType.ADMINISTRATOR);
    }

    public void checkIsAdmin(Profile profile, Group group) {
        if(userService.isUserAdminSession()) {
            return;
        }
        if (!isAdmin(profile,  group)) {
            throw new RolesException("Você precisa ser administrador para executar esta ação.");
        }
    }

    public void checkIsAdmin(Group group) {
        checkIsAdmin(UserService.getInstance().getUserInSession().getProfile(), group);
    }

    public void checkIsAdmin(String groupId) {
        checkIsAdmin(GroupService.getInstance().getGroupByGroupIdOrGroupPath(groupId, null));
    }

    public void checkPermission(Profile profile, Group group, FeaturesTypes feature, int forPermission) {
        if (profile == null) {
            throw new RolesException("Perfil não encontrado.");
        }
        if (group == null) {
            throw new RolesException("Grupo não encontrado.");
        }
        if (feature == null) {
            throw new RolesException("Funcionalidade não encontrada.");
        }

        var roles = getAssignedRoles(profile.getId(), group.getId());

        var permission = roles.getPermissionForFeature(feature);

        if (permission < forPermission)
            throw new RolesException("Você precisa de permissão para executar esta ação em \""+ feature.label +"\".");
    }

    public boolean hasPermission(Profile profile, Group group, FeaturesTypes feature, int forPermission) {
        try {
            checkPermission(profile, group, feature, forPermission);
            return true;
        } catch (RolesException e) {
            return false;
        }
    }

    public void checkPermission(Group group, FeaturesTypes feature, int forPermission) {
        checkPermission(
                UserService.getInstance().getUserInSession().getProfile(),
                group,
                feature,
                forPermission
        );
    }

    public boolean hasPermission(Group group, FeaturesTypes feature, int forPermission) {
        return hasPermission(
                UserService.getInstance().getUserInSession().getProfile(),
                group,
                feature,
                forPermission
        );
    }

    public void checkPermission(String groupId, FeaturesTypes feature, int forPermission) {
        checkPermission(
                GroupService.getInstance().getGroupByGroupIdOrGroupPath(groupId, null),
                feature,
                forPermission
        );
    }

    public boolean hasPermission(String groupId, FeaturesTypes feature, int forPermission) {
        return hasPermission(
                GroupService.getInstance().getGroupByGroupIdOrGroupPath(groupId, null),
                feature,
                forPermission
        );
    }

    public Roles getAssignedRoles( String profileIdOrUsername, UUID groupId ) {
        return getAssignedRoles(
            profileService.findByIdOrUsernameOrThrow( profileIdOrUsername ),
            groupService.findOrThrow( groupId )
        );
    }

    public Roles getAssignedRoles( UUID profileId, UUID groupId ) {
        return getAssignedRoles(
            profileService.findOrThrow( profileId ),
            groupService.findOrThrow( groupId )
        );
    }

    private Roles getAssignedRoles( Profile profile, Group group ) {
        var profileGroup = profileGroupRepository.findFirstByGroupAndProfile(group, profile);
        return profileGroup != null
            ? profileGroup.role
            : getGroupVisitorRole(group);
    }

    public Collection<Roles> getAllRolesByProfile(Profile profile) {
        if (profile == null)
            return new ArrayList<>();

        return profileGroupRepository.findAllByProfile(profile)
            .stream()
            .filter(profile::equals)
            .map(pg -> pg.role)
            .toList();
    }

    // get all roles for user in session
    public Collection<Roles> getAllRolesSession() {
        return getAllRolesByProfile(UserService.getInstance().getUserInSession().getProfile());
    }

    public Roles getGroupAdminRole(@NotNull Group group) {
        return rolesRepository.findFirstByGroupIdAndRoleType(group.getId(), RoleType.ADMINISTRATOR);
    }

    public Roles getGroupMemberRole(@NotNull Group group) {
        return rolesRepository.findFirstByGroupIdAndRoleType(group.getId(), RoleType.PARTICIPANT);
    }

    public Roles getGroupVisitorRole(@NotNull Group group) {
        return rolesRepository.findFirstByGroupIdAndRoleType(group.getId(), RoleType.VISITOR);
    }

    public void createBaseRoles(@NotNull Group group) {
        List<Roles> rolesToCreate = new ArrayList<>();

        if (getGroupAdminRole(group) == null)
            rolesToCreate.add(Roles.makeAdmin(group));

        if (getGroupMemberRole(group) == null)
            rolesToCreate.add(Roles.makeParticipant(group));

        if (getGroupVisitorRole(group) == null)
            rolesToCreate.add(Roles.makeVisitor(group));

        rolesRepository.saveAll(rolesToCreate);
    }

    @Override
    public boolean hasPermissionToEdit( @NotNull Roles role ) {
        return role.isCanBeEdited()
            && isAdmin( profileService.getProfileInSession(), role.group );
    }

    @Override
    public boolean hasPermissionToDelete( @NotNull Roles role ) {
        return hasPermissionToEdit( role );
    }
}
