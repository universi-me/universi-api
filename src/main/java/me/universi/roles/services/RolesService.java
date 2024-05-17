package me.universi.roles.services;

import java.util.*;
import me.universi.Sys;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.repositories.ProfileGroupRepository;
import me.universi.group.services.GroupService;
import me.universi.roles.dto.RoleDTO;
import me.universi.roles.entities.Roles;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.enums.RoleType;
import me.universi.roles.repositories.RolesFeatureRepository;
import me.universi.profile.entities.Profile;
import me.universi.roles.entities.RolesFeature;
import me.universi.roles.exceptions.RolesException;
import me.universi.roles.repositories.RolesRepository;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

@Service
public class RolesService {
    private final UserService userService;
    private final ProfileService profileService;
    private final GroupService groupService;
    private final RolesRepository rolesRepository;
    private final ProfileGroupRepository profileGroupRepository;
    private final RolesFeatureRepository rolesFeatureRepository;

    @Autowired
    public RolesService(UserService userService, ProfileService profileService, GroupService groupService, RolesRepository rolesRepository, RolesFeatureRepository rolesFeatureRepository, ProfileGroupRepository profileGroupRepository) {
        this.userService = userService;
        this.profileService = profileService;
        this.groupService = groupService;
        this.rolesRepository = rolesRepository;
        this.rolesFeatureRepository = rolesFeatureRepository;
        this.profileGroupRepository = profileGroupRepository;
    }

    public static RolesService getInstance() {
        return Sys.context.getBean("rolesService", RolesService.class);
    }

    public Roles saveRole(Roles roles) {
        return rolesRepository.save(roles);
    }

    public Roles createRole(Map<String, Object> body) {

        Object name = body.get("name");
        Object description = body.get("description");
        Object groupId = body.get("groupId");

        if (name == null) {
            throw new RolesException("Parâmetro name é nulo.");
        }

        if(groupId == null) {
            throw new RolesException("Parâmetro groupId é nulo.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);

        checkIsAdmin(group);

        Roles roles = new Roles();
        roles.name = name.toString();
        roles.description = description != null ? description.toString() : null;
        roles.group = group;

        return saveRole(roles);
    }

    public Roles editRole(UUID rolesId, String name, String description) {
        if (rolesId == null) {
            throw new RolesException("Parâmetro rolesId é nulo.");
        }

        Roles roles = rolesRepository.findFirstById(rolesId)
            .orElseThrow(() -> new RolesException("Papel não encontrado."));

        if(!roles.isCanBeEdited()) {
            throw new RolesException("Papel não pode ser editado.");
        }

        checkIsAdmin(roles.group);

        if (name != null)
            roles.name = name;

        if (description != null)
            roles.description = description;

        return saveRole(roles);
    }

    public Roles assignRole(UUID roleId, UUID groupId, UUID profileId) {
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);

        checkIsAdmin(group);

        Profile profile = profileService.findFirstById(profileId);
        if(profile == null) {
            throw new RolesException("Perfil não encontrado.");
        }

        Roles roles = rolesRepository.findById(roleId).orElseThrow(() -> {
            return new RolesException("Papel não encontrado.");
        });

        if(!roles.isCanBeAssigned()) {
            throw new RolesException("O papel de visitante não pode ser colocado em um participante.");
        }

        ProfileGroup profileGroup = profileGroupRepository.findFirstByGroupAndProfile(group, profile);
        if (profileGroup == null) {
            throw new RolesException("Só pode atribuir o papel à um membro do grupo");
        }

        profileGroup.role = roles;
        profileGroupRepository.save(profileGroup);

        return roles;
    }

    public RolesFeature setRolesFeatureValue(Map<String, Object> body) {
        Object rolesId = body.get("rolesId");
        Object featureString = body.get("feature");

        Object value = body.get("value");

        if (value == null) {
            throw new RolesException("Parâmetro value é nulo.");
        }

        RolesFeature rolesFeature = null;

        if(rolesFeature == null && rolesId != null && featureString != null) {
            Roles roles = rolesRepository.findFirstById(UUID.fromString(rolesId.toString())).orElse(null);
            if(roles == null) {
                throw new RolesException("Papel de usuário não encontrado.");
            }
            FeaturesTypes feature = FeaturesTypes.valueOf(featureString.toString());
            if(feature == null) {
                throw new RolesException("Funcionalidade não encontrado.");
            }
            rolesFeature = rolesFeatureRepository.findFirstByRolesAndFeatureType(roles, feature).orElse(null);
            if(rolesFeature == null) {
                rolesFeature = new RolesFeature();
                rolesFeature.featureType = feature;
                rolesFeature.roles = roles;
            }
        }
        if(rolesFeature == null) {
            throw new RolesException("Feature não encontrada.");
        }

        checkIsAdmin(rolesFeature.roles.group);

        rolesFeature.permission = Integer.parseInt(value.toString());

        return rolesFeatureRepository.save(rolesFeature);
    }

    public Collection<Roles> listRolesGroup(Map<String, Object> body) {
        Object groupId = body.get("groupId");

        if(groupId == null) {
            throw new RolesException("Parâmetro groupId é nulo.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);

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
        var rolesFeature = rolesFeatureRepository.findFirstByRolesAndFeatureType(roles, feature);

        var permission = rolesFeature.isPresent()
            ? rolesFeature.get().permission
            : Permission.NONE;

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

    public Collection<Profile> listRolesProfile(Map<String, Object> body) {
        Object groupId = body.get("groupId");

        if(groupId == null) {
            throw new RolesException("Parâmetro groupId é nulo.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);

        Collection<ProfileGroup> participants = group.participants;

        return participants.stream()
            .map(p -> {
                p.profile.roles = p.role;
                return p.profile;
            })
            .toList();
    }

    public Roles getAssignedRoles(UUID profileId, UUID groupId) {
        if(profileId == null)
            throw new RolesException("Parâmetro profileId é nulo.");

        if(groupId == null)
            throw new RolesException("Parâmetro groupId é nulo.");

        Profile profile = profileService.findFirstById(profileId);
        if(profile == null)
            throw new RolesException("Perfil não encontrado.");

        Group group = groupService.findFirstById(groupId);
        if(group == null)
            throw new RolesException("Grupo não encontrado.");

        var profileGroup = profileGroupRepository.findFirstByGroupAndProfile(group, profile);
        return profileGroup != null
            ? profileGroup.role
            : getGroupVisitorRole(group);
    }

    public Collection<RoleDTO> getAllRolesByProfile(Profile profile) {
        if (profile == null)
            return new ArrayList<>();

        return profileGroupRepository.findAllByProfile(profile)
            .stream()
            .filter(profile::equals)
            .map(RoleDTO::fromProfileGroup)
            .toList();
    }

    // get all roles for user in session
    public Collection<RoleDTO> getAllRolesSession() {
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
}
