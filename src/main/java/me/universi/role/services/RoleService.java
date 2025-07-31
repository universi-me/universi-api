package me.universi.role.services;

import jakarta.validation.Valid;
import java.util.*;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiNoEntityException;
import me.universi.api.interfaces.EntityService;
import me.universi.group.entities.Group;
import me.universi.group.repositories.ProfileGroupRepository;
import me.universi.group.services.GroupParticipantService;
import me.universi.group.services.GroupService;
import me.universi.role.dto.CreateRoleDTO;
import me.universi.role.dto.ProfileRoleDTO;
import me.universi.role.dto.UpdateRoleDTO;
import me.universi.role.entities.Role;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.enums.RoleType;
import me.universi.profile.entities.Profile;
import me.universi.role.exceptions.RolesException;
import me.universi.role.repositories.RoleRepository;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.LoginService;
import me.universi.user.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

@Service
public class RoleService extends EntityService<Role> {
    private final UserService userService;
    private final ProfileService profileService;
    private final GroupService groupService;
    private final RoleRepository roleRepository;
    private final ProfileGroupRepository profileGroupRepository;

    @Autowired
    public RoleService(UserService userService, ProfileService profileService, GroupService groupService, RoleRepository roleRepository, ProfileGroupRepository profileGroupRepository) {
        this.userService = userService;
        this.profileService = profileService;
        this.groupService = groupService;
        this.roleRepository = roleRepository;
        this.profileGroupRepository = profileGroupRepository;

        this.entityName = "Papel";
    }

    public static RoleService getInstance() {
        return Sys.context().getBean("roleService", RoleService.class);
    }

    public static @NotNull RoleRepository getRepository() {
        return Sys.context().getBean( "roleRepository", RoleRepository.class );
    }

    @Override
    public Optional<Role> findUnchecked( UUID id ) {
        return roleRepository.findById( id );
    }

    @Override
    public List<Role> findAllUnchecked() {
        return roleRepository.findAll();
    }

    public Optional<Role> findByNameAndGroup( String name, UUID groupId ) {
        return roleRepository.findFirstByNameIgnoreCaseAndGroupId( name, groupId );
    }

    public Role findByNameAndGroupOrThrow( String name, UUID groupId ) {
        return findByNameAndGroup( name, groupId )
            .orElseThrow( () -> makeNotFoundException( "nome e grupo", name + "' e '" + groupId) );
    }

    public Optional<Role> findByIdAndGroup( @NotNull UUID id, @NotNull Group group ) {
        return find( id ).filter( role -> role.group.getId().equals( group.getId() ) );
    }

    public Role findByIdAndGroupOrThrow( @NotNull UUID id, @NotNull Group group ) {
        return findByIdAndGroup( id, group )
            .orElseThrow( () -> new UniversiNoEntityException( "Nenhum " + this.entityName + " de ID '" + id + "' foi encontrado para este grupo" ) );
    }

    public Role create( @NotNull CreateRoleDTO dto ) {
        var group = groupService.findByIdOrPathOrThrow( dto.group() );
        checkIsAdmin( group );

        var existingRole = findByNameAndGroup( dto.name(), group.getId() );
        if ( existingRole.isPresent() )
            throw new UniversiConflictingOperationException( "O grupo já possui um papel de nome '" + existingRole.get().name + "'" );

        var role = Role.makeCustom(
            dto.name().trim(),
            dto.description(),
            group,
            Permission.READ
        );

        return roleRepository.saveAndFlush( role );
    }

    public Role update( @NotNull UUID id, @NotNull UpdateRoleDTO dto ) {
        var role = findOrThrow( id );
        checkPermissionToEdit( role );

        if ( dto.name() != null && !dto.name().isBlank() )
            role.name = dto.name();

        if ( dto.description() != null && !dto.description().isBlank() )
            role.description = dto.description();

        if ( dto.features() != null ) {
            dto.features().forEach( ( feature, permission ) -> {
                if ( role.group.isActivityGroup() && (
                    feature == FeaturesTypes.GROUP
                    || feature == FeaturesTypes.JOBS
                ) ) {
                   throw new UniversiConflictingOperationException( "Você não pode alterar o nível de permissão de '" + feature.label + "' em grupos de Atividade" );
                }

                role.setPermission( feature, permission );
            } );
        }

        return roleRepository.saveAndFlush( role );
    }

    public void delete( @NotNull UUID id ) {
        var role = findOrThrow( id );
        checkPermissionToDelete( role );

        // remove role from members before deleting
        var memberRole = getGroupMemberRole( role.group );

        var profileGroups = profileGroupRepository.findAllByRoleId( id );
        profileGroups.stream().forEach( pg -> {
            pg.setRole(memberRole);
        } );

        profileGroupRepository.saveAllAndFlush( profileGroups );
        roleRepository.delete( role );
    }

    public Role assignRole( @NotNull UUID roleId, @NotNull String profileIdOrUsername ) {
        var profile = profileService.findByIdOrUsernameOrThrow( profileIdOrUsername );
        var role = findOrThrow( roleId );

        checkIsAdmin( role.group );

        if ( !role.isCanBeAssigned() )
            throw new UniversiConflictingOperationException( "O papel não pode ser atribuído" );

        if( profileService.isSessionOfProfile( profile ) )
            throw new UniversiConflictingOperationException( "Você não pode alterar seu próprio papel" );

        if ( role.group.getAdmin().getId().equals( profile.getId() ) )
            throw new UniversiConflictingOperationException( "O papel do dono do grupo não pode ser alterado" );

        var profileGroup = GroupParticipantService.getInstance().findByGroupAndProfile( role.group, profile )
            .orElseThrow( () -> new UniversiConflictingOperationException( "Você só pode atribuir o papel à um membro do grupo" ) );

        profileGroup.setRole(role);
        return profileGroupRepository.saveAndFlush( profileGroup ).getRole();
    }

    public Collection<Role> findByGroup( UUID groupId ) {
        Group group = groupService.findOrThrow( groupId );
        checkIsAdmin(group);

        return roleRepository.findAllByGroup(group);
    }

    public boolean isAdmin(Profile profile, Group group) {
        if (profile == null) {
            throw new RolesException("Perfil não encontrado.");
        }
        if (group == null) {
            throw new RolesException("Grupo não encontrado.");
        }

        return getAssignedRole( profile, group ).isAdmin();
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
        checkIsAdmin(LoginService.getInstance().getUserInSession().getProfile(), group);
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

        var role = getAssignedRole(profile.getId(), group.getId());

        var permission = role.getPermissionForFeature(feature);

        if (permission < forPermission) {
            if (!userService.isUserAdmin(profile.getUser())) {
                throw new RolesException("Você precisa de permissão para executar esta ação em \"" + feature.label + "\".");
            }
        }
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
                LoginService.getInstance().getUserInSession().getProfile(),
                group,
                feature,
                forPermission
        );
    }

    public boolean hasPermission(Group group, FeaturesTypes feature, int forPermission) {
        return hasPermission(
                LoginService.getInstance().getUserInSession().getProfile(),
                group,
                feature,
                forPermission
        );
    }

    public void checkPermission(String groupId, FeaturesTypes feature, int forPermission) {
        checkPermission(
                GroupService.getInstance().findByIdOrPathOrThrow( groupId ),
                feature,
                forPermission
        );
    }

    public boolean hasPermission(String groupId, FeaturesTypes feature, int forPermission) {
        return hasPermission(
                GroupService.getInstance().findByIdOrPathOrThrow( groupId ),
                feature,
                forPermission
        );
    }

    public Role getAssignedRole( String profileIdOrUsername, UUID groupId ) {
        return getAssignedRole(
            profileService.findByIdOrUsernameOrThrow( profileIdOrUsername ),
            groupService.findOrThrow( groupId )
        );
    }

    public Role getAssignedRole( UUID profileId, UUID groupId ) {
        return getAssignedRole(
            profileService.findOrThrow( profileId ),
            groupService.findOrThrow( groupId )
        );
    }

    private Role getAssignedRole( Profile profile, Group group ) {
        return GroupParticipantService.getInstance().findByGroupAndProfile( group, profile )
            .map( pg -> pg.getRole() )
            .orElseGet( () -> getGroupVisitorRole( group ) );
    }

    public Collection<Role> getAllRolesByProfile(Profile profile) {
        if (profile == null)
            return new ArrayList<>();

        return profileGroupRepository.findAllByProfile(profile)
            .stream()
            .filter(profile::equals)
            .map(pg -> pg.getRole())
            .toList();
    }

    // get all roles for user in session
    public Collection<Role> getAllRolesSession() {
        return getAllRolesByProfile(LoginService.getInstance().getUserInSession().getProfile());
    }

    public Role getGroupAdminRole(@NotNull Group group) {
        return roleRepository.findFirstByGroupIdAndRoleType(group.getId(), RoleType.ADMINISTRATOR);
    }

    public Role getGroupMemberRole(@NotNull Group group) {
        return roleRepository.findFirstByGroupIdAndRoleType(group.getId(), RoleType.PARTICIPANT);
    }

    public Role getGroupVisitorRole(@NotNull Group group) {
        return roleRepository.findFirstByGroupIdAndRoleType(group.getId(), RoleType.VISITOR);
    }

    public void createBaseRoles(@NotNull Group group) {
        List<Role> rolesToCreate = new ArrayList<>();

        if (getGroupAdminRole(group) == null)
            rolesToCreate.add(Role.makeAdmin(group));

        if (getGroupMemberRole(group) == null)
            rolesToCreate.add(Role.makeParticipant(group));

        if (getGroupVisitorRole(group) == null)
            rolesToCreate.add(Role.makeVisitor(group));

        roleRepository.saveAll(rolesToCreate);
    }

    @Override
    public boolean hasPermissionToEdit( @NotNull Role role ) {
        var profile = profileService.getProfileInSession();
        if ( profile.isEmpty() )
            return false;

        return ( role.isCanBeEdited()
            && isAdmin( profile.get(), role.group ) ) || userService.isUserAdmin( profile.get().getUser() );
    }

    @Override
    public boolean hasPermissionToDelete( @NotNull Role role ) {
        return hasPermissionToEdit( role );
    }

    // list paticipants group with assigned role
    public Collection<ProfileRoleDTO> getParticipantsRoles(@Valid @NotNull UUID groupId) {
        var group = groupService.findOrThrow(groupId);
        checkIsAdmin(group);

        return group.getParticipants()
            .stream()
            .map(pg -> new ProfileRoleDTO(pg.getProfile(), getAssignedRole(pg.getProfile(), group)))
            .toList();
    }
}
