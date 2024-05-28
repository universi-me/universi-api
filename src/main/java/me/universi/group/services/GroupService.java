package me.universi.group.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.universi.Sys;
import me.universi.capacity.entidades.Folder;
import me.universi.competence.entities.Competence;

import me.universi.group.DTO.CompetenceFilterDTO;
import me.universi.group.DTO.CompetenceFilterRequestDTO;
import me.universi.group.DTO.ProfileWithCompetencesDTO;
import me.universi.group.DTO.CompetenceInfoDTO;

import me.universi.group.entities.*;
import me.universi.group.entities.GroupSettings.*;
import me.universi.group.enums.GroupEmailFilterType;
import me.universi.group.enums.GroupType;
import me.universi.group.exceptions.GroupException;
import me.universi.group.repositories.*;
import me.universi.profile.entities.Profile;
import me.universi.roles.entities.Roles;
import me.universi.roles.enums.RoleType;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final ProfileGroupRepository profileGroupRepository;
    private final SubgroupRepository subgroupRepository;
    private final GroupSettingsRepository groupSettingsRepository;
    private final GroupEmailFilterRepository groupEmailFilterRepository;
    private final GroupThemeRepository groupThemeRepository;
    private final GroupFeaturesRepository groupFeaturesRepository;
    private final GroupEnvironmentRepository groupEnvironmentRepository;

    @Value("${LOCAL_ORGANIZATION_ID_ENABLED}")
    private boolean localOrganizationIdEnabled;

    @Value("${LOCAL_ORGANIZATION_ID}")
    private String localOrganizationId;

    public GroupService(UserService userService, GroupRepository groupRepository, ProfileGroupRepository profileGroupRepository, SubgroupRepository subgroupRepository, GroupSettingsRepository groupSettingsRepository, GroupEmailFilterRepository groupEmailFilterRepository, GroupThemeRepository groupThemeRepository, GroupFeaturesRepository groupFeaturesRepository, GroupEnvironmentRepository groupEnvironmentRepository) {
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.profileGroupRepository = profileGroupRepository;
        this.subgroupRepository = subgroupRepository;
        this.groupSettingsRepository = groupSettingsRepository;
        this.groupEmailFilterRepository = groupEmailFilterRepository;
        this.groupThemeRepository = groupThemeRepository;
        this.groupFeaturesRepository = groupFeaturesRepository;
        this.groupEnvironmentRepository = groupEnvironmentRepository;
    }


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

        if(userService.isUserAdmin(user)) {
            return true;
        }

        Profile profile = user.getProfile();
        if (userService.userNeedAnProfile(user, true)) {
            throw new GroupException("Você precisa criar um Perfil.");
        }

        if(Objects.equals(group.getAdmin().getId(), profile.getId())) {
            return true;
        }

        for(ProfileGroup groupAdminNow : getAdministrators(group)) {
            if(groupAdminNow != null && Objects.equals(groupAdminNow.profile.getId(), profile.getId())) {
                return true;
            }
        }

        throw new GroupException("Você não tem permissão para editar este grupo.");
    }

    public boolean hasPermissionToEditGroup(Group group, User user) {
        try {
            return verifyPermissionToEditGroup(group, user);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canEditGroup(Group group) {
        return hasPermissionToEditGroup(group, userService.getUserInSession());
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

    public boolean addParticipantToGroup(@NotNull Group group, @NotNull Profile profile) throws GroupException {
        return addParticipantToGroup(
            group,
            profile,
            RolesService.getInstance().getGroupMemberRole(group)
        );
    }

    public boolean addParticipantToGroup(@NotNull Group group, @NotNull Profile profile, Roles roles) throws GroupException {
        if(!profileGroupRepository.existsByGroupIdAndProfileId(group.getId(), profile.getId())) {
            ProfileGroup profileGroup = new ProfileGroup();
            profileGroup.profile = profile;
            profileGroup.group = group;
            profileGroup.role = roles;
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
                if(groupNow.subgroup != null) {
                    this.deleteRecursive(groupNow.subgroup, true);
                }
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

    // calculate organization based in domain
    public Group obtainOrganizationBasedInDomain() {
        String domain = userService.getDomainFromRequest();

        String organizationId = (domain.contains(".") ? domain.split("\\.")[0] : domain).toLowerCase().trim();

        if(!userService.isProduction() || localOrganizationIdEnabled) {
            organizationId = localOrganizationId;
        }

        return findFirstByRootGroupAndNicknameIgnoreCase(true, organizationId, false);
    }

    public Group getOrganizationBasedInDomain() {
        // if logged find updated organization of user without cached from session, else calculate from domain
        Group gOrg = userService.userIsLoggedIn() && userService.getUserInSession() != null && userService.getUserInSession().getOrganization() != null ?
                findFirstById(userService.getUserInSession().getOrganization().getId()) : obtainOrganizationBasedInDomain();
        if(gOrg == null) {
            throw new GroupException("Falha ao obter Organização.");
        }
        return gOrg;
    }

    public Group getOrganizationBasedInDomainIfExist() {
        try {
            return getOrganizationBasedInDomain();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean emailAvailableForOrganization(String email) {
        boolean available = false;
        boolean hasAnyFilter = false;
        Group organization = getOrganizationBasedInDomainIfExist();

        if (organization != null) {
            GroupSettings orgSettings = organization.getGroupSettings();

            for (GroupEmailFilter emailFilterNow : orgSettings.getFilterEmails()) {
                if (emailFilterNow.enabled) {
                    if (!hasAnyFilter) {
                        hasAnyFilter = true;
                    }
                    switch (emailFilterNow.type) {
                        case END_WITH:
                            if (email.endsWith(emailFilterNow.email)) {
                                available = true;
                            }
                            break;
                        case START_WITH:
                            if (email.startsWith(emailFilterNow.email)) {
                                available = true;
                            }
                            break;
                        case CONTAINS:
                            if (email.contains(emailFilterNow.email)) {
                                available = true;
                            }
                            break;
                        case EQUALS:
                            if (email.equals(emailFilterNow.email)) {
                                available = true;
                            }
                            break;
                        case MASK:
                            Pattern patternMask = Pattern.compile(emailFilterNow.email.replaceAll("\\*", "(.*)"));
                            Matcher matcherMask = patternMask.matcher(email);
                            if (matcherMask.find()) {
                                available = true;
                            }
                            break;
                        case REGEX:
                            Pattern pattern = Pattern.compile(emailFilterNow.email);
                            Matcher matcher = pattern.matcher(email);
                            if (matcher.find()) {
                                available = true;
                            }
                            break;
                    }
                }
            }
        }

        return hasAnyFilter ? available : !available;
    }

    public boolean addEmailFilter(Group group, String email, Object type, Boolean enabled) {
        if(group == null || email == null || email.isEmpty()) {
            return false;
        }
        GroupEmailFilter groupEmailFilter = new GroupEmailFilter();
        groupEmailFilter.groupSettings = group.groupSettings;
        groupEmailFilter.email = email;
        if(type != null) {
            groupEmailFilter.type = GroupEmailFilterType.valueOf(String.valueOf(type));
        }
        if(enabled != null) {
            groupEmailFilter.enabled = enabled;
        }
        groupEmailFilterRepository.save(groupEmailFilter);
        return true;
    }

    public boolean editEmailFilter(Group group, UUID groupEmailFilterId, String email, Object type, Boolean enabled) {
        if(group == null || groupEmailFilterId == null) {
            return false;
        }
        if(groupEmailFilterRepository.existsByGroupSettingsIdAndId(group.groupSettings.getId(), groupEmailFilterId)) {
            GroupEmailFilter groupEmailFilter = groupEmailFilterRepository.findFirstByGroupSettingsIdAndId(group.groupSettings.getId(), groupEmailFilterId);
            if(email != null) {
                groupEmailFilter.email = email;
            }
            if(type != null) {
                groupEmailFilter.type = GroupEmailFilterType.valueOf(String.valueOf(type));
            }
            if(enabled != null) {
                groupEmailFilter.enabled = enabled;
            }
            groupEmailFilterRepository.save(groupEmailFilter);
            return true;
        }
        return false;
    }

    public boolean editEmailFilter(Group group, String groupEmailFilterId, String email, Object type, Boolean enabled) {
        if(group == null || groupEmailFilterId == null || groupEmailFilterId.isEmpty()) {
            return false;
        }
        return editEmailFilter(group, UUID.fromString(groupEmailFilterId), email, type, enabled);
    }

    public boolean deleteEmailFilter(Group group, UUID groupEmailFilterId) {
        if(group == null || groupEmailFilterId == null) {
            return false;
        }
        if(groupEmailFilterRepository.existsByGroupSettingsIdAndId(group.groupSettings.getId(), groupEmailFilterId)) {
            GroupEmailFilter groupEmailFilter = groupEmailFilterRepository.findFirstByGroupSettingsIdAndId(group.groupSettings.getId(), groupEmailFilterId);
            groupEmailFilter.setRemoved(ConvertUtil.getDateTimeNow());
            groupEmailFilter.setDeleted(true);
            groupEmailFilterRepository.save(groupEmailFilter);
            return true;
        }
        return false;
    }

    public boolean deleteEmailFilter(Group group, String groupEmailFilterId) {
        if(group == null || groupEmailFilterId == null || groupEmailFilterId.isEmpty()) {
            return false;
        }
        return deleteEmailFilter(group, UUID.fromString(groupEmailFilterId));
    }

    public void saveGroupSettings(GroupSettings gSettings) {
        groupSettingsRepository.save(gSettings);
    }

    public boolean editTheme(Group group, String primaryColor, String secondaryColor, String tertiaryColor, String backgroundColor, String cardBackgroundColor, String cardItemColor, String fontColorV1, String fontColorV2, String fontColorV3, String fontColorV4, String fontColorV5, String fontColorV6, String fontDisabledColor, String formsColor, String skills1Color, String waveColor, String buttonYellowHoverColor, String buttonHoverColor, String alertColor, String successColor, String wrongInvalidColor, String rankColor) {
        if(group == null) {
            return false;
        }
        GroupSettings groupSettings = group.getGroupSettings();
        if(groupSettings == null) {
            return false;
        }
        GroupTheme groupTheme = groupSettings.theme;
        if(groupTheme == null) {
            groupTheme = new GroupTheme();
            groupTheme.groupSettings = groupSettings;
            groupTheme = groupThemeRepository.save(groupTheme);
        }
        if(primaryColor != null) {
            groupTheme.primaryColor = primaryColor.isEmpty() ? null : primaryColor;
        }
        if(secondaryColor != null) {
            groupTheme.secondaryColor = secondaryColor.isEmpty() ? null : secondaryColor;
        }
        if(tertiaryColor != null) {
            groupTheme.tertiaryColor = tertiaryColor.isEmpty() ? null : tertiaryColor;
        }
        if(backgroundColor != null) {
            groupTheme.backgroundColor = backgroundColor.isEmpty() ? null : backgroundColor;
        }
        if(cardBackgroundColor != null) {
            groupTheme.cardBackgroundColor = cardBackgroundColor.isEmpty() ? null : cardBackgroundColor;
        }
        if(cardItemColor != null) {
            groupTheme.cardItemColor = cardItemColor.isEmpty() ? null : cardItemColor;
        }
        if(fontColorV1 != null) {
            groupTheme.fontColorV1 = fontColorV1.isEmpty() ? null : fontColorV1;
        }
        if(fontColorV2 != null) {
            groupTheme.fontColorV2 = fontColorV2.isEmpty() ? null : fontColorV2;
        }
        if(fontColorV3 != null) {
            groupTheme.fontColorV3 = fontColorV3.isEmpty() ? null : fontColorV3;
        }
        if(fontColorV4 != null) {
            groupTheme.fontColorV4 = fontColorV4.isEmpty() ? null : fontColorV4;
        }
        if(fontColorV5 != null) {
            groupTheme.fontColorV5 = fontColorV5.isEmpty() ? null : fontColorV5;
        }
        if(fontColorV6 != null) {
            groupTheme.fontColorV6 = fontColorV6.isEmpty() ? null : fontColorV6;
        }
        if(fontDisabledColor != null) {
            groupTheme.fontDisabledColor = fontDisabledColor.isEmpty() ? null : fontDisabledColor;
        }
        if(formsColor != null) {
            groupTheme.formsColor = formsColor.isEmpty() ? null : formsColor;
        }
        if(skills1Color != null) {
            groupTheme.skills1Color = skills1Color.isEmpty() ? null : skills1Color;
        }
        if(waveColor != null) {
            groupTheme.waveColor = waveColor.isEmpty() ? null : waveColor;
        }
        if(buttonYellowHoverColor != null) {
            groupTheme.buttonYellowHoverColor = buttonYellowHoverColor.isEmpty() ? null : buttonYellowHoverColor;
        }
        if(buttonHoverColor != null) {
            groupTheme.buttonHoverColor = buttonHoverColor.isEmpty() ? null : buttonHoverColor;
        }
        if(alertColor != null) {
            groupTheme.alertColor = alertColor.isEmpty() ? null : alertColor;
        }
        if(successColor != null) {
            groupTheme.successColor = successColor.isEmpty() ? null : successColor;
        }
        if(wrongInvalidColor != null) {
            groupTheme.wrongInvalidColor = wrongInvalidColor.isEmpty() ? null : wrongInvalidColor;
        }
        if(rankColor != null) {
            groupTheme.rankColor = rankColor.isEmpty() ? null : rankColor;
        }
        groupThemeRepository.save(groupTheme);
        return true;
    }

    public boolean addFeature(Group group, String name, String description, Boolean enabled) {
        if(group == null) {
            return false;
        }
        if(name == null || name.isEmpty()) {
            throw new GroupException("Nome da feature está vazio.");
        }
        GroupSettings groupSettings = group.getGroupSettings();
        if(groupSettings == null) {
            return false;
        }
        if(groupFeaturesRepository.existsByGroupSettingsIdAndName(groupSettings.getId(), name)) {
            throw new GroupException("Feature já existe.");
        }
        GroupFeatures groupFeature = new GroupFeatures();
        groupFeature.groupSettings = groupSettings;
        groupFeature.name = name.trim();
        if(description != null) {
            groupFeature.description = description;
        }
        if(enabled != null) {
            groupFeature.enabled = enabled;
        }
        groupFeaturesRepository.save(groupFeature);
        return true;
    }

    public boolean deleteFeature(Group group, UUID groupFeatureId) {
        if(group == null || groupFeatureId == null) {
            return false;
        }
        if(groupFeaturesRepository.existsByGroupSettingsIdAndId(group.getGroupSettings().getId(), groupFeatureId)) {
            GroupFeatures groupFeature = groupFeaturesRepository.findFirstByGroupSettingsIdAndId(group.getGroupSettings().getId(), groupFeatureId);
            groupFeature.setRemoved(ConvertUtil.getDateTimeNow());
            groupFeature.setDeleted(true);
            groupFeaturesRepository.save(groupFeature);
            return true;
        }
        return false;
    }

    public boolean deleteFeature(Group group, String groupFeatureId) {
        if(group == null || groupFeatureId == null || groupFeatureId.isEmpty()) {
            return false;
        }
        return deleteFeature(group, UUID.fromString(groupFeatureId));
    }

    public boolean editFeature(Group group, UUID groupFeatureId, Boolean enabled, String description) {
        if(group == null) {
            return false;
        }
        GroupSettings groupSettings = group.getGroupSettings();
        if(groupSettings == null) {
            return false;
        }
        GroupFeatures groupFeature = groupFeaturesRepository.findFirstByGroupSettingsIdAndId(groupSettings.getId(), groupFeatureId);
        if(groupFeature == null) {
            throw new GroupException("Feature não encontrada.");
        }
        if(enabled != null) {
            groupFeature.enabled = enabled;
        }
        if(description != null) {
            groupFeature.description = description;
        }
        groupFeaturesRepository.save(groupFeature);
        return true;
    }

    public boolean editFeature(Group group, String groupFeatureId, Boolean enabled, String description) {
        if(group == null || groupFeatureId == null || groupFeatureId.isEmpty()) {
            return false;
        }
        return editFeature(group, UUID.fromString(groupFeatureId), enabled, description);
    }

    // edit group environment
    public boolean editEnvironment(Group group, Boolean signup_enabled, Boolean signup_confirm_account_enabled,
                                   Boolean login_google_enabled, String google_client_id, Boolean recaptcha_enabled,
                                   String recaptcha_api_key, String recaptcha_api_project_id, String recaptcha_site_key,
                                   Boolean keycloak_enabled, String keycloak_client_id, String keycloak_client_secret,
                                   String keycloak_realm, String keycloak_url, String keycloak_redirect_url,
                                   Boolean alert_new_content_enabled, String message_template_new_content,
                                   Boolean alert_assigned_content_enabled, String message_template_assigned_content,
                                   Boolean email_enabled, String email_host, String email_port, String email_protocol,
                                   String email_username, String email_password) {
        if(group == null) {
            return false;
        }
        if(!group.isRootGroup()) {
            throw new GroupException("Este grupo não é uma organização.");
        }
        GroupSettings groupSettings = group.getGroupSettings();
        if(groupSettings == null) {
            return false;
        }
        GroupEnvironment groupEnvironment = groupSettings.environment;
        if(groupEnvironment == null) {
            groupEnvironment = new GroupEnvironment();
            groupEnvironment.groupSettings = groupSettings;
            groupEnvironment = groupEnvironmentRepository.save(groupEnvironment);
        }
        if(signup_enabled != null) {
            groupEnvironment.signup_enabled = signup_enabled;
        }
        if(signup_confirm_account_enabled != null) {
            groupEnvironment.signup_confirm_account_enabled = signup_confirm_account_enabled;
        }
        if(login_google_enabled != null) {
            groupEnvironment.login_google_enabled = login_google_enabled;
        }
        if(google_client_id != null) {
            groupEnvironment.google_client_id = google_client_id.isEmpty() ? null : google_client_id;
        }
        if(recaptcha_enabled != null) {
            groupEnvironment.recaptcha_enabled = recaptcha_enabled;
        }
        if(recaptcha_api_key != null) {
            groupEnvironment.recaptcha_api_key = recaptcha_api_key.isEmpty() ? null : recaptcha_api_key;
        }
        if(recaptcha_api_project_id != null) {
            groupEnvironment.recaptcha_api_project_id = recaptcha_api_project_id.isEmpty() ? null : recaptcha_api_project_id;
        }
        if(recaptcha_site_key != null) {
            groupEnvironment.recaptcha_site_key = recaptcha_site_key.isEmpty() ? null : recaptcha_site_key;
        }
        if(keycloak_enabled != null) {
            groupEnvironment.keycloak_enabled = keycloak_enabled;
        }
        if(keycloak_client_id != null) {
            groupEnvironment.keycloak_client_id = keycloak_client_id.isEmpty() ? null : keycloak_client_id;
        }
        if(keycloak_client_secret != null) {
            groupEnvironment.keycloak_client_secret = keycloak_client_secret.isEmpty() ? null : keycloak_client_secret;
        }
        if(keycloak_realm != null) {
            groupEnvironment.keycloak_realm = keycloak_realm.isEmpty() ? null : keycloak_realm;
        }
        if(keycloak_url != null) {
            groupEnvironment.keycloak_url = keycloak_url.isEmpty() ? null : keycloak_url;
        }
        if(keycloak_redirect_url != null) {
            groupEnvironment.keycloak_redirect_url = keycloak_redirect_url.isEmpty() ? null : keycloak_redirect_url;
        }

        if(alert_new_content_enabled != null) {
            groupEnvironment.alert_new_content_enabled = alert_new_content_enabled;
        }
        if(message_template_new_content != null) {
            if(message_template_new_content.length() > 255) {
                throw new GroupException("O template de mensagem para novo conteúdo não pode ter mais de 255 caracteres.");
            }
            groupEnvironment.message_template_new_content = message_template_new_content.isEmpty() ? null : message_template_new_content;
        }
        if(alert_assigned_content_enabled != null) {
            groupEnvironment.alert_assigned_content_enabled = alert_assigned_content_enabled;
        }
        if(message_template_assigned_content != null) {
            if(message_template_assigned_content.length() > 255) {
                throw new GroupException("O template de mensagem para conteúdo atribuído não pode ter mais de 255 caracteres.");
            }
            groupEnvironment.message_template_assigned_content = message_template_assigned_content.isEmpty() ? null : message_template_assigned_content;
        }

        boolean needUpdateEmailConfiguration = false;
        if(email_enabled != null && email_enabled != groupEnvironment.email_enabled ||
                email_host != null && !email_host.equals(groupEnvironment.email_host) ||
                email_port != null && !email_port.equals(groupEnvironment.email_port) ||
                email_protocol != null && !email_protocol.equals(groupEnvironment.email_protocol) ||
                email_username != null && !email_username.equals(groupEnvironment.email_username) ||
                email_password != null && !email_password.equals(groupEnvironment.email_password)) {
            needUpdateEmailConfiguration = true;
        }

        if(email_enabled != null) {
            groupEnvironment.email_enabled = email_enabled;
        }
        if(email_host != null) {
            groupEnvironment.email_host = email_host.isEmpty() ? null : email_host;
        }
        if(email_port != null) {
            groupEnvironment.email_port = email_port.isEmpty() ? null : email_port;
        }
        if(email_protocol != null) {
            groupEnvironment.email_protocol = email_protocol.isEmpty() ? null : email_protocol;
        }
        if(email_username != null) {
            groupEnvironment.email_username = email_username.isEmpty() ? null : email_username;
        }
        if(email_password != null) {
            groupEnvironment.email_password = email_password.isEmpty() ? null : email_password;
        }

        groupEnvironmentRepository.save(groupEnvironment);

        if(needUpdateEmailConfiguration) {
            userService.setupEmailSender();
        }

        return true;
    }

    public GroupEnvironment getGroupEnvironment(Group group) {
        if(group == null) {
            return null;
        }
        GroupSettings groupSettings = group.getGroupSettings();
        if(groupSettings == null) {
            return null;
        }
        GroupEnvironment groupEnvironment = groupSettings.environment;
        if(groupEnvironment == null) {
            groupEnvironment = new GroupEnvironment();
            groupEnvironment.groupSettings = groupSettings;
            groupEnvironment = groupEnvironmentRepository.save(groupEnvironment);
        }
        return groupEnvironment;
    }

    // get organization environment
    public GroupEnvironment getOrganizationEnvironment() {
        return getGroupEnvironment(getOrganizationBasedInDomainIfExist());
    }


    public List<ProfileWithCompetencesDTO> filterProfilesWithCompetences(CompetenceFilterDTO competenceFilter){

        List<ProfileWithCompetencesDTO> selectedProfiles = new ArrayList<>();

        Group group = getGroupByGroupIdOrGroupPath(competenceFilter.groupId(), competenceFilter.groupPath());

        Collection<ProfileGroup> participants = group.getParticipants();
        List<Profile> profiles = participants.stream()
                .sorted(Comparator.comparing(ProfileGroup::getJoined).reversed())
                .map(ProfileGroup::getProfile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for(Profile p : profiles){
            ProfileWithCompetencesDTO profile = new ProfileWithCompetencesDTO(
                    p.getId(),
                    p.getUser(),
                    p.getFirstname(),
                    p.getLastname(),
                    p.getImage(),
                    p.getBio(),
                    p.getGender(),
                    p.getCreationDate(),
                    p.getIndicators(),
                    p.getCompetences()
            );


            boolean hasNecessaryCompetences = false;

            if(competenceFilter.matchEveryCompetence()){
                hasNecessaryCompetences = true;
                for(CompetenceFilterRequestDTO competence : competenceFilter.competences()){
                    if(!profile.hasCompetence(competence.id(), competence.level())) {
                        hasNecessaryCompetences = false;
                        break;
                    }
                }
            }
            else{
                for(CompetenceFilterRequestDTO competence : competenceFilter.competences()) {
                    if(profile.hasCompetence(competence.id(), competence.level())) {
                        hasNecessaryCompetences = true;
                        break;
                    }
                }
            }

            if(hasNecessaryCompetences)
                selectedProfiles.add(profile);
        }

        return selectedProfiles;
}

    public List<CompetenceInfoDTO> getGroupCompetences(Group group){

        List<CompetenceInfoDTO> groupCompetences = new ArrayList<>();
        List<Profile> groupProfiles = group.getParticipants().stream().map(ProfileGroup::getProfile).collect(Collectors.toList());

        for(Profile profile : groupProfiles){
            for(Competence competence : profile.getCompetences()) {
                UUID typeId = competence.getCompetenceType().getId();
                int level =  competence.getLevel();

                CompetenceInfoDTO currentGroupCompetence = null;
                for(CompetenceInfoDTO compInfo : groupCompetences){
                    if(compInfo.competenceTypeId().equals(competence.getCompetenceType().getId()))
                        currentGroupCompetence = compInfo;
                }

                if(currentGroupCompetence == null){
                    currentGroupCompetence = new CompetenceInfoDTO(competence.getCompetenceType().getName(), typeId, new HashMap<>());
                    currentGroupCompetence.levelInfo().put(level, new ArrayList<>());
                    currentGroupCompetence.levelInfo().get(level).add(profile);
                    groupCompetences.add(currentGroupCompetence);
                }
                else if(currentGroupCompetence.levelInfo().get(level) == null){
                    currentGroupCompetence.levelInfo().put(level, new ArrayList<>());
                    currentGroupCompetence.levelInfo().get(level).add(profile);
                }
                else{
                    currentGroupCompetence.levelInfo().get(level).add(profile);
                }

            }
        }

        return groupCompetences;

    }

    public void setupOrganization() {
        if(localOrganizationIdEnabled) {
            Optional<Group> organizationOpt = groupRepository.findFirstByRootGroup(true);
            if(organizationOpt.isPresent()) {
                Group organization = organizationOpt.get();
                if(organization.nickname.equals(localOrganizationId)) {
                    return;
                }
                organization.nickname = localOrganizationId;
                organization.name = localOrganizationId.toUpperCase();
                organization.setType(GroupType.INSTITUTION);
                save(organization);
            }
        }
    }

    // send email for all users in group
    public void sendEmailForAllUsersInGroup(Group group, String subject, String message) {
        if(group == null || subject == null || message == null) {
            return;
        }
        Collection<ProfileGroup> participants = group.getParticipants();
        for(ProfileGroup profileGroup : participants) {
            Profile profile = profileGroup.profile;
            if(profile != null && profile.getUser() != null) {
                userService.sendSystemEmailToUser(profile.getUser(), subject, message, true);
            }
        }
    }

    // replace placeholders in a template {{ key }} with values
    public String replacePlaceholders(String template, Map<String, String> values) {
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            // Create a pattern that ignores spaces within the curly braces
            result = result.replaceAll("\\{\\{\\s*" + entry.getKey() + "\\s*\\}\\}", entry.getValue());
        }
        return result;
    }

    public String getMessageTemplateForNewContentInGroup(Group group, Folder folder) {
        if(group == null || folder == null) {
            return null;
        }

        String groupName = group.getName();
        String contentName = folder.getName();
        String contentUrl = userService.getPublicUrl() + "/group" + group.getPath() + "#" + "contents/" + folder.getId();

        String message = getOrganizationEnvironment().groupSettings.environment.message_template_new_content;
        if(message == null || message.isEmpty()) {
            message = "Olá, {{ groupName }} tem um novo conteúdo: {{ contentName }}.<br/><br/>Acesse: {{ contentUrl }}";
        }

        return replacePlaceholders(message, Map.of("groupName", groupName, "contentName", contentName, "contentUrl", contentUrl));
    }

    public String getMessageTemplateForContentAssigned(Profile fromProfile, Profile profile, Folder folder) {
        if(profile == null || folder == null) {
            return null;
        }

        String fromUser = fromProfile.getFirstname();
        String toUser = profile.getFirstname();
        String contentName = folder.getName();
        String contentUrl = userService.getPublicUrl() + "/content/" + folder.getReference();

        String message = getOrganizationEnvironment().groupSettings.environment.message_template_assigned_content;
        if(message == null || message.isEmpty()) {
            message = "Olá {{ toUser }}, você recebeu um novo conteúdo de {{ fromUser }}: {{ contentName }}.<br/><br/>Acesse: {{ contentUrl }}";
        }

        return replacePlaceholders(message, Map.of("fromUser", fromUser, "toUser", toUser, "contentName", contentName, "contentUrl", contentUrl));
    }

    // alert all users in group for a new content in group
    public void alertAllUsersInGroupForNewContent(Group group, Folder folder) {
        if(group == null || folder == null) {
            return;
        }

        if(!getOrganizationEnvironment().groupSettings.environment.alert_new_content_enabled) {
            return;
        }

        String subject = "Novo conteúdo em " + group.getName();
        String message = getMessageTemplateForNewContentInGroup(group, folder);
        sendEmailForAllUsersInGroup(group, subject, message);
    }

    // alert user for content assigned
    public void alertUserForContentAssigned(Profile fromUser, Profile profile, Folder folder) {
        if(profile == null || folder == null) {
            return;
        }

        if(!getOrganizationEnvironment().groupSettings.environment.alert_assigned_content_enabled) {
            return;
        }

        String subject = "Conteúdo atribuído";
        String message = getMessageTemplateForContentAssigned(fromUser, profile, folder);

        userService.sendSystemEmailToUser(profile.getUser(), subject, message, true);
    }

    public Collection<ProfileGroup> getAdministrators(@NotNull Group group) {
        return group.participants.stream()
            .filter(pg -> pg.role.getRoleType() == RoleType.ADMINISTRATOR)
            .toList();
    }
}
