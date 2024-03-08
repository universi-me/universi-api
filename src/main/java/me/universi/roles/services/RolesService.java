package me.universi.roles.services;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import me.universi.Sys;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupAdmin;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.services.GroupService;
import me.universi.roles.dto.FeatureDTO;
import me.universi.roles.dto.RoleDTO;
import me.universi.roles.entities.RolesProfile;
import me.universi.roles.entities.Roles;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.repositories.RolesFeatureRepository;
import me.universi.profile.entities.Profile;
import me.universi.roles.entities.RolesFeature;
import me.universi.roles.exceptions.RolesException;
import me.universi.roles.repositories.RolesProfileRepository;
import me.universi.roles.repositories.RolesRepository;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolesService {
    private final UserService userService;
    private final ProfileService profileService;
    private final GroupService groupService;
    private final RolesRepository rolesRepository;
    private final RolesProfileRepository rolesProfileRepository;
    private final RolesFeatureRepository rolesFeatureRepository;

    private static Roles adminRoles;
    private static Roles userRoles;

    @Autowired
    public RolesService(UserService userService, ProfileService profileService, GroupService groupService, RolesRepository rolesRepository, RolesProfileRepository rolesProfileRepository, RolesFeatureRepository rolesFeatureRepository) {
        this.userService = userService;
        this.profileService = profileService;
        this.groupService = groupService;
        this.rolesRepository = rolesRepository;
        this.rolesProfileRepository = rolesProfileRepository;
        this.rolesFeatureRepository = rolesFeatureRepository;

        createDefaultRoles();
    }

    public void createDefaultRoles() {
        adminRoles = new Roles();
        adminRoles.isDefault = true;
        adminRoles.id = UUID.randomUUID();
        adminRoles.name = "Administrador";
        Collection<RolesFeature> adminRolesFeatures = new ArrayList<>();
        for(FeaturesTypes feature : FeaturesTypes.values()) {
            RolesFeature rolesFeature = new RolesFeature();
            rolesFeature.roles = adminRoles;
            rolesFeature.featureType = feature;
            rolesFeature.permission = Permission.READ_WRITE_DELETE;
            adminRolesFeatures.add(rolesFeature);
        }
        adminRoles.rolesFeatures = adminRolesFeatures;

        userRoles = new Roles();
        userRoles.isDefault = true;
        userRoles.id = UUID.randomUUID();
        userRoles.name = "Usuário";
        Collection<RolesFeature> userRolesFeatures = new ArrayList<>();
        for(FeaturesTypes feature : FeaturesTypes.values()) {
            RolesFeature rolesFeature = new RolesFeature();
            rolesFeature.roles = userRoles;
            rolesFeature.featureType = feature;
            rolesFeature.permission = Permission.READ;
            userRolesFeatures.add(rolesFeature);
        }
        userRoles.rolesFeatures = userRolesFeatures;
    }

    public static RolesService getInstance() {
        return Sys.context.getBean("rolesService", RolesService.class);
    }

    public Roles saveRole(Roles roles) {
        return rolesRepository.save(roles);
    }

    public Roles createRole(Map<String, Object> body) {

        if(!userService.isUserAdminSession()) {
            throw new RolesException("Usuário não possui permissão para criar um papel de usuário.");
        }

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

        Roles roles = new Roles();
        roles.name = name.toString();
        roles.description = description != null ? description.toString() : null;
        roles.group = group;

        return saveRole(roles);
    }

    public Roles editRole(Map<String, Object> body) {

        if(!userService.isUserAdminSession()) {
            throw new RolesException("Usuário não possui permissão para editar um papel de usuário.");
        }

        Object paperId = body.get("rolesId");
        Object name = body.get("name");
        Object description = body.get("description");

        if (paperId == null) {
            throw new RolesException("Parâmetro paperId é nulo.");
        }

        Roles roles = rolesRepository.findFirstById(UUID.fromString(paperId.toString())).orElse(null);
        if(roles == null) {
            throw new RolesException("Papel não encontrado.");
        }

        if(roles.isDefault) {
            throw new RolesException("Papel não pode ser editado.");
        }

        if (name != null) {
            roles.name = name.toString();
        }
        if (description != null) {
            roles.description = description.toString();
        }

        return saveRole(roles);
    }

    public RolesProfile assignRole(Map<String, Object> body) {

        if(!userService.isUserAdminSession()) {
            throw new RolesException("Usuário não possui permissão para atribuir um papel de usuário.");
        }

        Object rolesId = body.get("rolesId");
        Object groupId = body.get("groupId");
        Object profileId = body.get("profileId");


        if (rolesId == null) {
            throw new RolesException("Parâmetro rolesId é nulo.");
        }
        if (groupId == null) {
            throw new RolesException("Parâmetro groupId é nulo.");
        }
        if (profileId == null) {
            throw new RolesException("Parâmetro profileId é nulo.");
        }

        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);

        Profile profile = profileService.findFirstById(UUID.fromString(profileId.toString()));
        if(profile == null) {
            throw new RolesException("Perfil não encontrado.");
        }

        Roles roles = rolesRepository.findFirstById(UUID.fromString(rolesId.toString())).orElse(null);

        if(roles == null && !(Objects.equals(UUID.fromString((String) rolesId), adminRoles.id) || Objects.equals(UUID.fromString((String) rolesId), userRoles.id))) {
            throw new RolesException("Papel não encontrado.");
        }

        RolesProfile rolesProfile = rolesProfileRepository.findFirstByProfileAndGroup(profile, roles==null ? group : roles.group).orElse(null);
        if(rolesProfile == null) {
            rolesProfile = new RolesProfile();
        }
        rolesProfile.roles = roles;
        rolesProfile.group = roles==null ? group : roles.group;
        rolesProfile.profile = profile;

        if(roles == null) {
            rolesProfile.defaultRole = Objects.equals(UUID.fromString((String) rolesId), adminRoles.id) ? 1 : 0;
        }

        rolesProfileRepository.save(rolesProfile);

        rolesProfile.roles = rolesProfile.defaultRole == 1 ? adminRoles : userRoles;

        return rolesProfile;
    }

    public RolesFeature setRolesFeatureValue(Map<String, Object> body) {

        if(!userService.isUserAdminSession()) {
            throw new RolesException("Usuário não possui permissão para alterar status de uma feature.");
        }

        Object paperId = body.get("rolesId");
        Object featureString = body.get("feature");

        Object value = body.get("value");

        if (value == null) {
            throw new RolesException("Parâmetro value é nulo.");
        }

        RolesFeature rolesFeature = null;

        if(rolesFeature == null && paperId != null && featureString != null) {
            Roles roles = rolesRepository.findFirstById(UUID.fromString(paperId.toString())).orElse(null);
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

        if(rolesFeature.roles.isDefault) {
            throw new RolesException("Papel não pode ser editado.");
        }

        rolesFeature.permission = Integer.parseInt(value.toString());

        return rolesFeatureRepository.save(rolesFeature);
    }

    public Collection<Roles> listRolesGroup(Map<String, Object> body) {
        if(!userService.isUserAdminSession()) {
            throw new RolesException("Você não possui permissão para listar papéis.");
        }

        Object groupId = body.get("groupId");

        if(groupId == null) {
            throw new RolesException("Parâmetro groupId é nulo.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);

        Collection<Roles> roles = rolesRepository.findAllByGroup(group);

        roles.add(adminRoles);
        roles.add(userRoles);

        return roles;
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

        RolesProfile rolesProfile = rolesProfileRepository.findFirstByProfileAndGroup(profile, group).orElse(null);

        if (rolesProfile != null) {

            Roles roles = rolesProfile.roles;
            if (roles != null) {
                RolesFeature rolesFeature = rolesFeatureRepository.findFirstByRolesAndFeatureType(roles, feature).orElse(getDefaultRolesForProfile(profile, group).rolesFeatures.stream().filter(f -> f.featureType == feature).findFirst().orElse(null));

                if (rolesFeature != null) {

                    if (rolesFeature.permission < forPermission) {
                        throw new RolesException("Você não possui permissão para acessar a funcionalidade \""+ feature.label +"\".");
                    }

                } else {
                    //throw new PaperException("Funcionalidade não encontrada.");
                }

            } else {
                //throw new PaperException("Papel não encontrado.");
            }

        } else {
            //throw new PaperException("Perfil não possui papel no grupo.");
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

    public void checkPermission(String groupId, FeaturesTypes feature, int forPermission) {
        checkPermission(
                GroupService.getInstance().getGroupByGroupIdOrGroupPath(groupId, null),
                feature,
                forPermission
        );
    }

    public Collection<Profile> listPaperProfile(Map<String, Object> body) {
        Object groupId = body.get("groupId");

        if(groupId == null) {
            throw new RolesException("Parâmetro groupId é nulo.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);


        Collection<ProfileGroup> participants = group.participants;
        Collection<RolesProfile> papers = rolesProfileRepository.findAllByGroup(group);

        Collection<Profile> profiles = new ArrayList<>();

        for(ProfileGroup profileGroup : participants) {
            Profile profile = profileGroup.profile;
            for(RolesProfile rolesProfile : papers) {
                if(rolesProfile.profile.equals(profile)) {
                    profile.roles = rolesProfile.roles;
                    break;
                }
            }
            profiles.add(profileGroup.profile);
        }

        for(Profile profile : profiles) {
            if(profile.roles == null) {
                profile.roles = getDefaultRolesForProfile(profile, group);
            }
        }

        return profiles;
    }

    public Roles getAssignedPaper(Map<String, Object> body) {
        Object profileId = body.get("profileId");
        Object groupId = body.get("groupId");

        if(profileId == null) {
            throw new RolesException("Parâmetro profileId é nulo.");
        }
        if(groupId == null) {
            throw new RolesException("Parâmetro groupId é nulo.");
        }
        Profile profile = profileService.findFirstById(UUID.fromString(profileId.toString()));
        if(profile == null) {
            throw new RolesException("Perfil não encontrado.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);
        if(group == null) {
            throw new RolesException("Grupo não encontrado.");
        }

        RolesProfile rolesProfile = rolesProfileRepository.findFirstByProfileAndGroup(profile, group).orElse(null);

        return rolesProfile == null ? getDefaultRolesForProfile(profile, group) : rolesProfile.roles;
    }

    public Collection<RoleDTO> getAllRolesByProfile(Profile profile) {

        Collection<RolesProfile> roles = rolesProfileRepository.findAllByProfile(profile);

        Collection<RoleDTO> roleDTOs = new ArrayList<>();
        for(RolesProfile role : roles) {
            RoleDTO roleDTO = new RoleDTO();
            Roles roles1 = role.roles == null ? role.defaultRole==1?adminRoles:userRoles : role.roles;
            roleDTO.id = roles1.id;
            roleDTO.name = roles1.name;
            roleDTO.profile = profile.getId();
            roleDTO.group =  role.group.getId();
            Collection<FeatureDTO> features = new ArrayList<>();
            for(RolesFeature rolesFeature : roles1.rolesFeatures) {
                FeatureDTO feature = new FeatureDTO();
                feature.id = rolesFeature.id;
                feature.featureType = rolesFeature.featureType;
                feature.permission = rolesFeature.permission;
                features.add(feature);
                roleDTO.features = features;
            }
            roleDTOs.add(roleDTO);
        }

        return roleDTOs;
    }

    // get all roles for user in session
    public Collection<RoleDTO> getAllRolesSession() {
        return getAllRolesByProfile(UserService.getInstance().getUserInSession().getProfile());
    }

    // get default role based in profile and group
    public Roles getDefaultRolesForProfile(Profile profile, Group group) {
        Roles retRoles = userRoles;

        // get if is admin
        for(GroupAdmin admin : group.administrators) {
            if (admin.profile.equals(profile)) {
                retRoles = adminRoles;
                break;
            }
        }

        // get if has set default role
        Optional<RolesProfile> rolesProfile = rolesProfileRepository.findFirstByProfileAndGroup(profile, group);
        if(rolesProfile.isPresent()) {
            retRoles = rolesProfile.get().defaultRole == 1 ? adminRoles : userRoles;
        }

        return retRoles;
    }
}