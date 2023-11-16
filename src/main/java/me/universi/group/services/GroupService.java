package me.universi.group.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.universi.Sys;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.entities.Subgroup;
import me.universi.group.exceptions.GroupException;
import me.universi.group.repositories.GroupRepository;
import me.universi.group.repositories.ProfileGroupRepository;
import me.universi.group.repositories.SubgroupRepository;
import me.universi.profile.entities.Profile;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class GroupService {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ProfileGroupRepository profileGroupRepository;
    @Autowired
    private SubgroupRepository subgroupRepository;

    private static final Pattern patternGetSubdomain = Pattern.compile("(?:http[s]*\\:\\/\\/)?(.*?)((\\.)|(:))");

    public static GroupService getInstance() {
        return Sys.context.getBean("groupService", GroupService.class);
    }

    public Group findFirstById(UUID id) {
        Optional<Group> optionalGroup = groupRepository.findFirstById(id);
        Group group = optionalGroup.orElse(null);

        // check if user is logged in and if the group is from the user organization, elso return null
        if(group != null && userService.userIsLoggedIn()) {
            UUID orgAccessId = group.rootGroup ? group.getId() : getGroupRootIdFromGroupId(group.getId());
            User user = userService.getUserInSession();
            Group userOrg = user.getOrganization();
            if(userOrg != null && !Objects.equals(userOrg.getId(), orgAccessId)) {
                return null;
            }
        }

        return group;
    }

    public Group findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
    }

    public UUID findParentGroupId(UUID id) {
        Optional<Object> optionalGroup = groupRepository.findParentGroupId(id);
        if(optionalGroup.isPresent()) {
            Object object = optionalGroup.get();
            if(object instanceof UUID) {
                return (UUID)object;
            } else if(object instanceof byte[]) {
                return ConvertUtil.convertBytesToUUID((byte[])object);
            }
        }
        return null;
    }

    public Group findFirstByNickname(String nickname) {
        Optional<Group> optionalGroup = groupRepository.findFirstByNickname(nickname);
        return optionalGroup.orElse(null);
    }

    public Group findFirstByRootGroupAndNicknameIgnoreCase(boolean rootGroup, String nickname, boolean checkUserOrganizationAccess) {
        if(nickname == null || nickname.isEmpty()) {
            return null;
        }
        Optional<Group> optionalGroup = groupRepository.findFirstByRootGroupAndNicknameIgnoreCase(rootGroup, nickname);
        Group group = optionalGroup.orElse(null);

        // check if user is logged in and if the group is from the user organization, elso return null
        if(checkUserOrganizationAccess && rootGroup && group != null && userService.userIsLoggedIn()) {
            User user = userService.getUserInSession();
            Group userOrg = user.getOrganization();
            if(userOrg != null && !Objects.equals(userOrg.getId(), group.getId())) {
                return null;
            }
        }

        return group;
    }

    public List<Group> findByPublicGroup(boolean publicGroup) {
        List<Group> optionalGroup = groupRepository.findByPublicGroup(publicGroup);
        return optionalGroup;
    }

    public long count() {
        try {
            return groupRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean verifyPermissionToEditGroup(Group group, User user) throws GroupException {

        if (group == null) {
            throw new GroupException("Grupo não encontrado.");
        }

        if (user == null) {
            throw new GroupException("Usuário não encontrado.");
        }

        Profile profile = user.getProfile();
        if (userService.userNeedAnProfile(user, true)) {
            throw new GroupException("Você precisa criar um Perfil.");
        } else if(!Objects.equals(group.getAdmin().getId(), profile.getId())) {
            if(!userService.isUserAdmin(user)) {
                throw new GroupException("Apenas administradores podem editar seus grupos!");
            }
        }

        return true;
    }

    public boolean hasPermissionToEditGroup(Group group, User user) {
        try {
            return verifyPermissionToEditGroup(group, user);
        } catch (Exception e) {
            return false;
        }
    }

    public void addSubGroup(Group group, Group sub) {
        if(!subgroupRepository.existsByGroupIdAndSubgroupId(group.getId(), sub.getId())) {
            Subgroup subgroup = new Subgroup();
            subgroup.group = group;
            subgroup.subgroup = sub;
            subgroupRepository.save(subgroup);
        }
    }

    public void removeSubGroup(Group group, Group sub) {
        if(subgroupRepository.existsByGroupIdAndSubgroupId(group.getId(), sub.getId())) {
            this.delete(sub);
        }
    }

    public boolean addParticipantToGroup(Group group, Profile profile) throws GroupException {
        if(profile == null) {
            throw new GroupException("Parametro Perfil é nulo.");
        }
        if(!profileGroupRepository.existsByGroupIdAndProfileId(group.getId(), profile.getId())) {
            ProfileGroup profileGroup = new ProfileGroup();
            profileGroup.profile = profile;
            profileGroup.group = group;
            profileGroupRepository.save(profileGroup);
            return true;
        }
        return false;
    }

    public boolean removeParticipantFromGroup(Group group, Profile profile) throws GroupException {
        if(profile == null) {
            throw new GroupException("Parametro Perfil é nulo.");
        }
        if(profileGroupRepository.existsByGroupIdAndProfileId(group.getId(), profile.getId())) {
            ProfileGroup profileGroup = profileGroupRepository.findFirstByGroupAndProfile(group, profile);
            profileGroup.exited = ConvertUtil.getDateTimeNow();
            profileGroup.deleted = true;
            profileGroupRepository.save(profileGroup);
            return true;
        }
        return false;
    }

    public Profile getParticipantInGroup(Group group, UUID participantId) {
        if(participantId != null && group.getParticipants() != null) {
            for (ProfileGroup participantNow : group.getParticipants()) {
                if (Objects.equals(participantNow.profile.getId(), participantId)) {
                    return participantNow.profile;
                }
            }
        }
        return null;
    }

    public boolean isNicknameAvailableForGroup(Group group, String nickname) {
        boolean available = true;
        try {
            String nicknameLower = nickname.toLowerCase();

            if(available) {
                available = nicknameRegex(nickname);
            }

            if(available && group != null) {
                for (Subgroup groupNow : group.getSubGroups()) {
                    if (groupNow.subgroup != null && groupNow.subgroup.nickname.toLowerCase().equals(nicknameLower)) {
                        available = false;
                        break;
                    }
                }
            }

            if(available && group==null) {
                Group groupRoot = findFirstByRootGroupAndNicknameIgnoreCase(true, nicknameLower, false);
                if(groupRoot != null) {
                    available = false;
                }
            }

        }catch (Exception e) {
            available = false;
        }
        return available;
    }

    public boolean nicknameRegex(String nickname) {
        return userService.usernameRegex(nickname);
    }

    public void save(Group group) {
        groupRepository.saveAndFlush(group);
    }

    public void delete(Group group) {
        this.deleteRecursive(group, false);
    }

    private void deleteRecursive(Group group, boolean insideRecursion) {

        if(group.subGroups != null) {
            for(Subgroup groupNow : group.subGroups) {
                this.deleteRecursive(groupNow.subgroup, true);
            }
        }

        group.setDeleted(true);
        groupRepository.save(group);
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    /** Verifies if the user is accessing the group URL correctly, from root/master group to the subgroup */
    public Group getGroupFromNicknamePath(Group rootGroup, String[] nicknameSequenceArr) {
        Group finalGroup = null;

        boolean parentCheckFailed = false;
        try {
            Group groupInsta = rootGroup;
            for (int i = 0; i < nicknameSequenceArr.length; i++) {
                String nicknameNow = nicknameSequenceArr[i];
                if (nicknameNow == null || nicknameNow.isEmpty()) {
                    continue;
                }
                if (i == 0) {
                    // ignore the first, already verified
                    continue;
                }
                Group sub = null;
                for (Subgroup grupoNow : groupInsta.subGroups) {
                    if (grupoNow.subgroup != null && nicknameNow.equals(grupoNow.subgroup.nickname.toLowerCase())) {
                        sub = grupoNow.subgroup;
                        break;
                    }
                }
                if (sub != null) {
                    groupInsta = sub;
                } else {
                    parentCheckFailed = true;
                    break;
                }
            }
            finalGroup = groupInsta;
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!parentCheckFailed) {
            return finalGroup;
        }
        return null;
    }

    /** Generates the group URL from its id */
    public String getGroupPath(UUID groupId) {
        ArrayList<String> nicknames = new ArrayList<>();
        Group groupNow = findFirstById(groupId);
        while(groupNow != null) {
            nicknames.add(groupNow.nickname);
            groupNow = findFirstById(findParentGroupId(groupNow.getId()));
        }
        Collections.reverse(nicknames);
        return "/" + String.join("/", nicknames).toLowerCase();
    }

    /** Get group from url path */
    public Group getGroupFromPath(String path) {
        try {
            String pathSt = path.toLowerCase();

            String[] nicknameArr = Arrays.stream(pathSt.split("/"))
                    .map(String::trim)
                    .filter(Predicate.isEqual("").negate())
                    .toArray(String[]::new);

            Group groupRoot = null;
            Group groupActual = null;

            // get group root, its nickname is unique in the system, it can be accessed direct
            groupRoot = findFirstByRootGroupAndNicknameIgnoreCase(true, nicknameArr[0], true);
            if(groupRoot != null) {
                // check if group path is valid and return that group
                groupActual = getGroupFromNicknamePath(groupRoot, nicknameArr);
            }
            return groupActual;

        } catch(Exception e) {
            return null;
        }
    }

    public Group getGroupByGroupIdOrGroupPath(Object groupId, Object groupPath) throws GroupException {

        if(groupId == null && groupPath == null) {
            throw new GroupException("Parâmetro groupId e groupPath é nulo.");
        }

        Group group = null;
        if(groupId != null) {
            group = findFirstById(String.valueOf(groupId));
        }
        if (group == null && groupPath != null) {
            group = getGroupFromPath(String.valueOf(groupPath));
        }

        if(group == null) {
            throw new GroupException("Grupo não encontrado.");
        }

        return group;
    }

    /** Get the root group from a group id */
    public Group getGroupRootFromGroupId(UUID groupId) {
        return findFirstById(getGroupRootIdFromGroupId(groupId));
    }
    public UUID getGroupRootIdFromGroupId(UUID groupId) {
        UUID parentGroupId = findParentGroupId(groupId);
        while(parentGroupId != null) {
            UUID partentUUID = findParentGroupId(parentGroupId);
            if(partentUUID == null) {
                break;
            } else {
                parentGroupId = partentUUID;
            }
        }
        return parentGroupId;
    }

    /** Searches the first 5 groups containing {@code name} ignoring case */
    public Collection<Group> findTop5ByNameContainingIgnoreCase(String name){
        return groupRepository.findTop5ByNameContainingIgnoreCase(name);
    }

    public Group getOrganizationBasedInDomain() throws Exception {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String url = attr.getRequest().getRequestURL().toString();

        Matcher matcher = patternGetSubdomain.matcher(url);

        String organizationId = null;

        if (matcher.find()) {
            organizationId = matcher.group(1).toLowerCase();
        } else {
            throw new GroupException("Organização não encontrada.");
        }

        if("localhost".equals(organizationId)) {
            organizationId = "dcx";
        }

        Group organizationG = findFirstByRootGroupAndNicknameIgnoreCase(true, organizationId, false);
        if(organizationG == null) {
            throw new GroupException("Organização não encontrada.");
        }
        return organizationG;
    }

    public Group getOrganizationBasedInDomainIfExist() {
        try {
            return getOrganizationBasedInDomain();
        } catch (Exception e) {
            return null;
        }
    }


    public boolean emailAvailableForOrganization(String email) {
        return true;
    }
}
