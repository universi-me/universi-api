package me.universi.group.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.universi.Sys;
import me.universi.capacity.entidades.Folder;
import me.universi.competence.entities.Competence;
import me.universi.competence.services.CompetenceService;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.services.GroupFeedService;
import me.universi.group.DTO.*;

import me.universi.group.entities.*;
import me.universi.group.entities.GroupSettings.*;
import me.universi.group.enums.GroupType;
import me.universi.group.exceptions.GroupException;
import me.universi.group.repositories.*;
import me.universi.image.services.ImageMetadataService;
import me.universi.profile.entities.Profile;
import me.universi.role.entities.Role;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.enums.RoleType;
import me.universi.role.services.RoleService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final UserService userService;
    private final GroupFeedService groupFeedService;
    private final GroupRepository groupRepository;
    private final ProfileGroupRepository profileGroupRepository;
    private final SubgroupRepository subgroupRepository;
    private final GroupSettingsRepository groupSettingsRepository;
    private final GroupEmailFilterRepository groupEmailFilterRepository;
    private final GroupThemeRepository groupThemeRepository;
    private final GroupFeaturesRepository groupFeaturesRepository;
    private final GroupEnvironmentRepository groupEnvironmentRepository;
    private final CompetenceService competenceService;
    private final ImageMetadataService imageMetadataService;

    @Value("${LOCAL_ORGANIZATION_ID_ENABLED}")
    private boolean localOrganizationIdEnabled;

    @Value("${LOCAL_ORGANIZATION_ID}")
    private String localOrganizationId;

    @Value( "${server.servlet.context-path}" )
    private String contextPath;

    public GroupService(UserService userService, GroupFeedService groupFeedService, GroupRepository groupRepository, ProfileGroupRepository profileGroupRepository, SubgroupRepository subgroupRepository, GroupSettingsRepository groupSettingsRepository, GroupEmailFilterRepository groupEmailFilterRepository, GroupThemeRepository groupThemeRepository, GroupFeaturesRepository groupFeaturesRepository, GroupEnvironmentRepository groupEnvironmentRepository, CompetenceService competenceService, ImageMetadataService imageMetadataService) {
        this.userService = userService;
        this.groupFeedService = groupFeedService;
        this.groupRepository = groupRepository;
        this.profileGroupRepository = profileGroupRepository;
        this.subgroupRepository = subgroupRepository;
        this.groupSettingsRepository = groupSettingsRepository;
        this.groupEmailFilterRepository = groupEmailFilterRepository;
        this.groupThemeRepository = groupThemeRepository;
        this.groupFeaturesRepository = groupFeaturesRepository;
        this.groupEnvironmentRepository = groupEnvironmentRepository;
        this.competenceService = competenceService;
        this.imageMetadataService = imageMetadataService;
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

    public Optional<Group> find( UUID id ) {
        return groupRepository.findById( id );
    }

    public List<Optional<Group>> find( Collection<UUID> id ) {
        return id.stream().map( this::find ).toList();
    }

    public Optional<Group> findByIdOrPath( String idOrPath ) {
        var groupId = CastingUtil.getUUID( idOrPath );
        if ( groupId.isPresent() ) {
            return find( groupId.get() );
        }

        else {
            return Optional.ofNullable( getGroupFromPath( idOrPath ) );
        }
    }

    public @NotNull Group findByIdOrPathOrThrow( String idOrPath ) throws EntityNotFoundException {
        return findByIdOrPath( idOrPath ).orElseThrow( () -> new EntityNotFoundException( "Grupo com ID ou caminho '" + idOrPath + "' não encontrado" ) );
    }

    public Group findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Grupo de ID '" + id + "' não encontrado" ) );
    }

    public List<Group> findOrThrow( Collection<UUID> id ) {
        return id.stream().map( this::findOrThrow ).toList();
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

    public Group findFirstByRootGroup(boolean rootGroup) {
        Optional<Group> optionalGroup = groupRepository.findFirstByRootGroup(rootGroup);
        return optionalGroup.orElse(null);
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

    public boolean isParticipantInGroup(Group group, Profile profile) {
        try {
            return profileGroupRepository.existsByGroupIdAndProfileId(group.getId(), profile.getId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addParticipantToGroup(@NotNull Group group, @NotNull Profile profile) throws GroupException {
        return addParticipantToGroup(
            group,
            profile,
            RoleService.getInstance().getGroupMemberRole(group)
        );
    }

    public boolean addParticipantToGroup(@NotNull Group group, @NotNull Profile profile, Role role) throws GroupException {
        if(!isParticipantInGroup(group, profile)) {
            ProfileGroup profileGroup = new ProfileGroup();
            profileGroup.profile = profile;
            profileGroup.group = group;
            profileGroup.role = role;
            profileGroupRepository.save(profileGroup);
            return true;
        }

        return false;
    }

    public boolean removeParticipantFromGroup(Group group, Profile profile) throws GroupException {
        if(profile == null) {
            throw new GroupException("Parametro Perfil é nulo.");
        }
        if(isParticipantInGroup(group, profile)) {
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
        try {
            return isNicknameAvailableForGroup(group, nickname, false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNicknameAvailableForGroup(Group group, String nickname, boolean thwrowException) throws RuntimeException {
        boolean available = true;

        // check if nickname is empty
        if(nickname == null || nickname.isEmpty()) {
            available = false;
            if(thwrowException) {
                throw new GroupException("Apelido do grupo não pode estar vazio.");
            }
        }

        String nicknameLower = nickname.toLowerCase();

        // check nickname format valid
        if(available) {
            available = nicknameRegex(nickname);
            if(!available) {
                if(thwrowException) {
                    throw new GroupException("Apelido do Grupo está com formato inválido, não pode conter caracteres especiais.");
                }
            }
        }

        // check if nickname is already in use subgroup
        if(available && group != null) {
            for (Subgroup groupNow : group.getSubGroups()) {
                if (groupNow.subgroup != null && groupNow.subgroup.nickname.toLowerCase().equals(nicknameLower)) {
                    available = false;
                    if(thwrowException) {
                        throw new GroupException("Apelido de Grupo indisponível, já existe um grupo com apelido informado.");
                    }
                    break;
                }
            }
        }

        // check if nickname is already in use in the group organization
        if(available && (group==null || group.rootGroup)) {
            Group groupRoot = findFirstByRootGroupAndNicknameIgnoreCase(true, nicknameLower, false);
            if(groupRoot != null) {
                available = false;
                if(thwrowException) {
                    throw new GroupException("Apelido de Grupo indisponível, já está em uso por uma organização.");
                }
            }
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

        Group org = findFirstByRootGroupAndNicknameIgnoreCase(true, organizationId, false);
        if(org == null) {
            org = findFirstByRootGroup(true);
        }

        return org;
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

    public void saveGroupSettings(GroupSettings gSettings) {
        groupSettingsRepository.save(gSettings);
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
                p,
                competenceService.findByProfileId( p.getId() )
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
            var competences = competenceService.findByProfileId( profile.getId() );

            for ( Competence competence : competences ) {
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

    public void didImportContentToGroup(Group group, Folder folder) {
        didAddNewContentToGroup(group, folder);
    }

    public void didAddNewContentToGroup(Group group, Folder folder) {
        if(group == null || folder == null) {
            return;
        }

        try {
            alertAllUserInGroupForNewContent(group, folder);
        } catch (Exception ignored) {
        }

        try {
            postFeedMessageInGroupForNewContent(group, folder);
        } catch (Exception ignored) {
        }
    }

    public void postFeedMessageInGroupForNewContent(Group group, Folder folder) {
        if(group == null || folder == null) {
            return;
        }

        String link = "/group" + group.getPath() + "#" + "contents/" + folder.getId();
        String message = "<p>Conteúdo adicionado: <a href=\"" + link + "\">" + folder.getName() + "</a><p/>";

        GroupPostDTO groupPostDTO = new GroupPostDTO();
        groupPostDTO.setContent(message);
        groupPostDTO.setAuthorId(userService.getUserInSession().getId().toString());

        groupFeedService.createGroupPost(group.getId().toString(), groupPostDTO);
    }

    // alert all users in group for a new content in group
    public void alertAllUserInGroupForNewContent(Group group, Folder folder) {
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
            .filter(Objects::nonNull)
            .toList();
    }

    public Group getGroupByGroupSettingsId(UUID groupSettingsId) {
        return groupRepository.findFirstByGroupSettingsId(groupSettingsId);
    }

    // determining group by groupEmailFilterId
    public Group getGroupByGroupEmailFilterId(UUID groupEmailFilterId) {
        GroupEmailFilter groupEmailFilter = groupEmailFilterRepository.findFirstById(groupEmailFilterId).orElseThrow(() -> new GroupException("Filtro não existe."));
        UUID groupSettingId = groupEmailFilter.groupSettings.getId();
        Group group = getGroupByGroupSettingsId(groupSettingId);
        if(group != null) {
            return group;
        }
        throw new GroupException("Falha ao determinar grupo por filtro de email.");
    }

    public Group createGroup(CreateGroupDTO createGroupDTO) {
        User user = userService.getUserInSession();

        Boolean groupRoot = (Boolean)createGroupDTO.groupRoot();

        UUID groupIdParent = (UUID)createGroupDTO.parentGroupId();
        String groupPathParent = (String)createGroupDTO.groupPath();

        boolean hasGroupParent = groupIdParent != null || groupPathParent != null;

        if(!hasGroupParent) {
            if(!(groupRoot != null && userService.isUserAdmin(user))) {
                throw new GroupException("Parâmetro groupId é nulo.");
            }
        } else if(((groupIdParent != null) || (groupPathParent != null && !groupPathParent.isEmpty())) && (groupRoot!=null && groupRoot)) {
            throw new GroupException("Você não pode criar Grupo Master em Subgrupos.");
        }

        String nickname = (String)createGroupDTO.nickname();
        if(nickname == null) {
            throw new GroupException("Parâmetro nickname é nulo.");
        }

        String name = (String)createGroupDTO.name();
        if(name == null) {
            throw new GroupException("Parâmetro nome é nulo.");
        }

        var image = createGroupDTO.image();
        var bannerImage = createGroupDTO.bannerImage();
        var headerImage = createGroupDTO.headerImage();

        String description = (String)createGroupDTO.description();
        if(description == null) {
            throw new GroupException("Parâmetro description é nulo.");
        }

        String groupType = createGroupDTO.groupType();
        if(groupType == null) {
            throw new GroupException("Parâmetro groupType é nulo.");
        }

        Boolean canCreateGroup = (Boolean)createGroupDTO.canCreateGroup();
        Boolean publicGroup = (Boolean)createGroupDTO.isPublic();
        Boolean canEnter = (Boolean)createGroupDTO.canJoin();
        Boolean everyoneCanPost = (Boolean)createGroupDTO.everyoneCanPost();

        Group parentGroup = (groupIdParent==null && groupPathParent==null)?null:getGroupByGroupIdOrGroupPath(groupIdParent, groupPathParent);

        // support only lowercase nickname
        nickname = nickname.toLowerCase();

        if(!isNicknameAvailableForGroup(parentGroup, nickname)) {
            throw new GroupException("Este Nickname não está disponível para este grupo.");
        }

        if((groupRoot != null && groupRoot && userService.isUserAdmin(user)) || ((parentGroup !=null && parentGroup.canCreateGroup) || verifyPermissionToEditGroup(parentGroup, user))) {
            Group groupNew = new Group();
            groupNew.setNickname(nickname);
            groupNew.setName(name);
            if(image != null) {
                groupNew.setImage( imageMetadataService.findOrThrow( image ) );
            }
            if(bannerImage != null) {
                groupNew.setBannerImage( imageMetadataService.findOrThrow( bannerImage ) );
            }
            if(headerImage != null) {
                groupNew.setHeaderImage( imageMetadataService.findOrThrow( headerImage ) );
            }
            groupNew.setDescription(description);
            groupNew.setType(GroupType.valueOf(groupType));
            groupNew.setAdmin(user.getProfile());
            GroupSettings gSettings = new GroupSettings();
            saveGroupSettings(gSettings);
            groupNew.setGroupSettings(gSettings);
            if(canCreateGroup != null) {
                groupNew.setCanCreateGroup(canCreateGroup);
            }
            if(publicGroup != null) {
                groupNew.setPublicGroup(publicGroup);
            }
            if(canEnter != null) {
                groupNew.setCanEnter(canEnter);
            }
            if(everyoneCanPost != null) {
                groupNew.setEveryoneCanPost(everyoneCanPost);
            }
            if((groupRoot != null && groupRoot) && userService.isUserAdmin(user)) {
                groupNew.setRootGroup(true);
                save(groupNew);
            } else {
                save(groupNew);
                addSubGroup(parentGroup, groupNew);
            }

            // add creator to group participants
            RoleService.getInstance().createBaseRoles(groupNew);
            addParticipantToGroup(groupNew, groupNew.getAdmin(), RoleService.getInstance().getGroupAdminRole(groupNew));

            return groupNew;
        }

        throw new GroupException("Apenas Administradores podem criar subgrupos.");
    }

    public Group updateGroup(UpdateGroupDTO updateGroupDTO) {
        UUID groupId = (UUID)updateGroupDTO.groupId();
        String groupPath = (String)updateGroupDTO.groupPath();

        String name = (String)updateGroupDTO.name();
        String description = (String)updateGroupDTO.description();
        String groupType = (String)updateGroupDTO.groupType();
        var image = updateGroupDTO.image();
        var bannerImage = updateGroupDTO.bannerImage();
        var headerImage = updateGroupDTO.headerImage();

        Boolean canCreateGroup = (Boolean)updateGroupDTO.canHaveSubgroup();
        Boolean publicGroup = (Boolean)updateGroupDTO.isPublic();
        Boolean canEnter = (Boolean)updateGroupDTO.canJoin();
        Boolean everyoneCanPost = (Boolean)updateGroupDTO.everyoneCanPost();

        Group groupEdit = getGroupByGroupIdOrGroupPath(groupId, groupPath);

        RoleService.getInstance().checkPermission(groupEdit, FeaturesTypes.GROUP, Permission.READ_WRITE);

        User user = userService.getUserInSession();

        if(verifyPermissionToEditGroup(groupEdit, user)) {
            if(name != null && !name.isEmpty()) {
                groupEdit.setName(name);
            }
            if(description != null && !description.isEmpty()) {
                groupEdit.setDescription(description);
            }
            if(groupType != null && !groupType.isEmpty()) {
                groupEdit.setType(GroupType.valueOf(groupType));
            }
            if(image != null) {
                groupEdit.setImage( imageMetadataService.findOrThrow( image ) );
            }
            if(bannerImage != null) {
                groupEdit.setBannerImage( imageMetadataService.findOrThrow( bannerImage ) );
            }
            if(headerImage != null) {
                groupEdit.setHeaderImage( imageMetadataService.findOrThrow( headerImage ) );
            }
            if(canCreateGroup != null) {
                groupEdit.setCanCreateGroup(canCreateGroup);
            }
            if(publicGroup != null) {
                groupEdit.setPublicGroup(publicGroup);
            }
            if(canEnter != null) {
                groupEdit.setCanEnter(canEnter);
            }
            if(everyoneCanPost != null){
                groupEdit.setEveryoneCanPost(everyoneCanPost);
            }

            save(groupEdit);

            return groupEdit;
        }

        throw new GroupException("Falha ao editar grupo");
    }

    public List<Group> subGroups(UUID groupId) {
        Group group = getGroupByGroupIdOrGroupPath(groupId, null);

        RoleService.getInstance().checkPermission(group, FeaturesTypes.GROUP, Permission.READ);

        if(group != null) {
            Collection<Subgroup> subgroupList = group.getSubGroups();
            if(subgroupList != null) {
                return subgroupList.stream()
                        .sorted(Comparator.comparing(Subgroup::getAdded).reversed())
                        .map(Subgroup::getSubgroup)
                        .filter(Objects::nonNull)
                        .filter((g -> isParticipantInGroup(g, userService.getUserInSession().getProfile()) || g.isPublicGroup() )) // public group flag, available for see in list public groups
                        .collect(Collectors.toList());
            }
        }
        throw new GroupException("Falha ao listar grupo.");
    }

    public Collection<Folder> listFolders(UUID groupId) {
        Group group = getGroupByGroupIdOrGroupPath(groupId, null);

        // check permission contents
        RoleService.getInstance().checkPermission(group, FeaturesTypes.CONTENT, Permission.READ);

        return group.getFoldersGrantedAccess();
    }

    public boolean deleteGroup(UUID groupId) {
        Group group = getGroupByGroupIdOrGroupPath(groupId, null);

        RoleService.getInstance().checkPermission(group, FeaturesTypes.GROUP, Permission.READ_WRITE_DELETE);

        User user = userService.getUserInSession();

        if(verifyPermissionToEditGroup(group, user)) {
            delete(group);
            return true;
        }

        throw new GroupException("Erro ao executar operação.");
    }

    public boolean removeSubgroup(UUID groupId, UUID subGroupId) {
        Group group = getGroupByGroupIdOrGroupPath(groupId, null);

        RoleService.getInstance().checkPermission(group, FeaturesTypes.GROUP, Permission.READ_WRITE_DELETE);

        if(subGroupId == null) {
            throw new GroupException("Parâmetro groupIdRemove é nulo.");
        }

        if(group == null) {
            throw new GroupException("Grupo não encontrado.");
        }

        Group groupRemove = findFirstById(subGroupId);
        if(groupRemove == null) {
            throw new GroupException("Subgrupo não encontrado.");
        }

        User user = userService.getUserInSession();

        if(verifyPermissionToEditGroup(group, user)) {
            removeSubGroup(group, groupRemove);
            return true;
        }

        throw new GroupException("Erro ao executar operação.");
    }

    public Collection<Role> findRoles( UUID groupId ) {
        return RoleService.getInstance().findByGroup( groupId );
    }
 }
