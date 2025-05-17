package me.universi.group.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.interfaces.EntityService;
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
import me.universi.role.services.RoleService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService extends EntityService<Group> {
    private final UserService userService;
    private final GroupFeedService groupFeedService;
    private final GroupRepository groupRepository;
    private final GroupSettingsRepository groupSettingsRepository;
    private final GroupEmailFilterRepository groupEmailFilterRepository;
    private final GroupEnvironmentRepository groupEnvironmentRepository;
    private final CompetenceService competenceService;
    private final ImageMetadataService imageMetadataService;

    @Value("${LOCAL_ORGANIZATION_ID_ENABLED}")
    private boolean localOrganizationIdEnabled;

    @Value("${LOCAL_ORGANIZATION_ID}")
    private String localOrganizationId;

    @Value( "${server.servlet.context-path}" )
    private String contextPath;

    public GroupService(UserService userService, GroupFeedService groupFeedService, GroupRepository groupRepository, GroupSettingsRepository groupSettingsRepository, GroupEmailFilterRepository groupEmailFilterRepository, GroupEnvironmentRepository groupEnvironmentRepository, CompetenceService competenceService, ImageMetadataService imageMetadataService) {
        this.userService = userService;
        this.groupFeedService = groupFeedService;
        this.groupRepository = groupRepository;
        this.groupSettingsRepository = groupSettingsRepository;
        this.groupEmailFilterRepository = groupEmailFilterRepository;
        this.groupEnvironmentRepository = groupEnvironmentRepository;
        this.competenceService = competenceService;
        this.imageMetadataService = imageMetadataService;

        this.entityName = "Grupo";
    }


    public static GroupService getInstance() {
        return Sys.context.getBean("groupService", GroupService.class);
    }

    @Override
    public boolean isValid( Group group ) {
        if ( group == null || group.isDeleted() )
            return false;

        if ( group.isRootGroup() )
            return true;

        if ( !userService.userIsLoggedIn() )
            return false;

        var userInSession = userService.getUserInSession();
        var organization = group.getOrganization();

        if ( !userInSession.getOrganization().getId().equals( organization.getId() ) )
            return false;

        return group.isPublicGroup()
            || GroupParticipantService.getInstance().isParticipant( group , userInSession.getProfile() );
    }

    protected Optional<Group> findUnchecked( UUID id ) {
        return groupRepository.findById( id );
    }

    public Optional<Group> findByIdOrPath( String idOrPath ) {
        var groupId = CastingUtil.getUUID( idOrPath );
        if ( groupId.isPresent() ) {
            return find( groupId.get() );
        }

        else {
            var nicknames = Arrays.stream( idOrPath.split( Group.PATH_DIVISOR ) )
                .map( n -> n.trim().toLowerCase() )
                .filter( n -> !n.isBlank() )
                .toList();

            if ( nicknames.isEmpty() )
                return Optional.empty();

            var organization = groupRepository.findFirstByParentGroupIsNullAndNicknameIgnoreCase( nicknames.get( 0 ) );
            if ( organization.isEmpty() )
                return Optional.empty();

            var target = organization.get();
            for ( int i = 1; i < nicknames.size(); i++ ) {
                var subs = target.getSubGroups();
                if ( subs == null ) return Optional.empty();

                var nick = nicknames.get( i );
                var group = subs.stream()
                    .filter( g -> g.getNickname().toLowerCase().equals( nick ) )
                    .findFirst();

                if ( group.isEmpty() )
                    return Optional.empty();

                target = group.get();
            }

            return Optional.ofNullable( target ).filter( this::isValid );
        }
    }

    public @NotNull Group findByIdOrPathOrThrow( String idOrPath ) throws EntityNotFoundException {
        return findByIdOrPath( idOrPath ).orElseThrow( () -> makeNotFoundException( "ID ou caminho", idOrPath ) );
    }

    private Group findFirstRootGroupByNicknameIgnoreCase(String nickname, boolean checkUserOrganizationAccess) {
        if(nickname == null || nickname.isEmpty()) {
            return null;
        }
        Optional<Group> optionalGroup = groupRepository.findFirstByParentGroupIsNullAndNicknameIgnoreCase(nickname);
        Group group = optionalGroup.orElse(null);

        // check if user is logged in and if the group is from the user organization, elso return null
        if(checkUserOrganizationAccess && group != null && userService.userIsLoggedIn()) {
            User user = userService.getUserInSession();
            Group userOrg = user.getOrganization();
            if(userOrg != null && !Objects.equals(userOrg.getId(), group.getId())) {
                return null;
            }
        }

        return group;
    }

    private Group findFirstRootGroup() {
        Optional<Group> optionalGroup = groupRepository.findFirstByParentGroupIsNull();
        return optionalGroup.orElse(null);
    }

    @Override
    public boolean hasPermissionToEdit( Group group ) {
        return userService.userIsLoggedIn()
            && hasPermissionToEdit( group, userService.getUserInSession() );
    }

    @Override
    public boolean hasPermissionToDelete( Group group ) {
        return hasPermissionToEdit( group );
    }

    public void checkPermissionToEdit(Group group, User user) throws GroupException {
        if (group == null) {
            throw new GroupException("Grupo não encontrado.");
        }

        if (user == null) {
            throw new GroupException("Usuário não encontrado.");
        }

        if(userService.isUserAdmin(user)) {
            return;
        }

        Profile profile = user.getProfile();
        if (userService.userNeedAnProfile(user, true)) {
            throw new GroupException("Você precisa criar um Perfil.");
        }

        if(Objects.equals(group.getAdmin().getId(), profile.getId())) {
            return;
        }

        if ( RoleService.getInstance().isAdmin( profile, group ) )
            return;

        throw new GroupException("Você não tem permissão para editar este grupo.");
    }

    public boolean hasPermissionToEdit(Group group, User user) {
        try {
            checkPermissionToEdit(group, user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public @NotNull String checkNicknameAvailable( String nickname, @Nullable Group parentGroup ) {
        if ( nickname == null || nickname.isBlank() )
            throw new UniversiBadRequestException( "O Apelido do Grupo não pode ser vazio" );

        final var validNickname = nickname.trim().toLowerCase();
        if ( !userService.usernameRegex( nickname ) )
            throw new UniversiBadRequestException( "O Apelido do Grupo contém caracteres inválidos" );

        final var nicknameInUse = parentGroup == null
            ? groupRepository.findFirstByParentGroupIsNullAndNicknameIgnoreCase( validNickname ).isPresent()
            : parentGroup.getSubGroups().stream().anyMatch( sg -> sg.getNickname().equalsIgnoreCase( validNickname ) );

        if ( nicknameInUse ) {
            throw new UniversiConflictingOperationException(
                new StringBuilder()
                    .append( "O Apelido de Grupo '" )
                    .append( validNickname )
                    .append( "' não pode ser aplicado pois já está em uso por " )
                    .append( parentGroup == null
                        ? "outra organização"
                        : "outro subgrupo deste grupo"
                    )
                    .toString()
            );
        }

        return validNickname;
    }

    public boolean isNicknameAvailable( String nickname, @Nullable Group parentGroup ) {
        try {
            checkNicknameAvailable( nickname, parentGroup );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void save(Group group) {
        groupRepository.saveAndFlush(group);
    }

    private void delete( Group group ) {
        if ( group == null )
            return;

        if ( group.getSubGroups() != null )
            group.getSubGroups().forEach( this::delete );

        group.setDeleted( true );
        groupRepository.saveAndFlush( group );
    }

    @Override
    protected List<Group> findAllUnchecked() {
        return groupRepository.findAll();
    }

    private String getSubdomainFromDomain(String domain) {
        String subdomain = domain;
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9-]+)\\.[a-zA-Z0-9.-]+\\.[a-z]{2,}$");
        Matcher matcher = pattern.matcher(domain);
        if (matcher.find()) {
            subdomain = matcher.group(1);
        }
        return subdomain;
    }

    // calculate organization based in domain
    public Group obtainOrganizationBasedInDomain() {
        String domain = userService.getDomainFromRequest();

        String organizationId = (getSubdomainFromDomain(domain)).toLowerCase().trim();

        if(!userService.isProduction() || localOrganizationIdEnabled) {
            organizationId = localOrganizationId;
        }

        Group org = findFirstRootGroupByNicknameIgnoreCase(organizationId, false);
        if(org == null) {
            org = findFirstRootGroup();
        }

        return org;
    }

    public Group getOrganizationBasedInDomain() {
        // if logged find updated organization of user without cached from session, else calculate from domain
        Group gOrg = userService.userIsLoggedIn() && userService.getUserInSession() != null && userService.getUserInSession().getOrganization() != null ?
                find(userService.getUserInSession().getOrganization().getId()).orElse(null) : obtainOrganizationBasedInDomain();
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

        Group group = findByIdOrPathOrThrow( competenceFilter.group() );

        Collection<ProfileGroup> participants = group.getParticipants();
        List<Profile> profiles = participants.stream()
                .sorted(Comparator.comparing(ProfileGroup::getJoined).reversed())
                .map(ProfileGroup::getProfile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for(Profile p : profiles){
            ProfileWithCompetencesDTO profile = new ProfileWithCompetencesDTO(
                p,
                competenceService.findByProfile( p.getId() )
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
            var competences = competenceService.findByProfile( profile.getId() );

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
            Optional<Group> organizationOpt = groupRepository.findFirstByParentGroupIsNull();
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

    public Group createGroup( CreateGroupDTO dto ) {
        if ( dto.parentGroup().isEmpty() && !userService.isUserAdminSession() )
            throw new UniversiBadRequestException( "O Parâmetro 'parentGroup' deve ser informado" );

        var parentGroup = dto.parentGroup().map( this::findByIdOrPathOrThrow );
        var nickname = checkNicknameAvailable( dto.nickname(), parentGroup.orElse( null ) );

        var group = new Group();
        group.setAdmin( userService.getUserInSession().getProfile() );

        group.setNickname( nickname );
        group.setName( dto.name() );
        group.setDescription( dto.description() );
        group.setType(
            CastingUtil.getEnum( GroupType.class, dto.groupType() )
                .orElseThrow( () -> new UniversiBadRequestException( "Tipo de Grupo '" + dto.groupType() + "' não existe" ) )
        );

        group.setCanCreateGroup( dto.canCreateSubgroup() );
        group.setPublicGroup( dto.isPublic() );
        group.setCanEnter( dto.canJoin() );

        dto.image().ifPresent( imageId -> {
            group.setImage( imageMetadataService.findOrThrow( imageId ) );
        } );

        dto.bannerImage().ifPresent( bannerImageId -> {
            group.setBannerImage( imageMetadataService.findOrThrow( bannerImageId ) );
        } );

        dto.headerImage().ifPresent( headerImageId -> {
            group.setHeaderImage( imageMetadataService.findOrThrow( headerImageId ) );
        } );

        var settings = groupSettingsRepository.saveAndFlush( new GroupSettings() );
        group.setGroupSettings( settings );
        parentGroup.ifPresent( group::setParentGroup );

        var createdGroup = groupRepository.saveAndFlush( group );

        RoleService.getInstance().createBaseRoles( createdGroup );
        GroupParticipantService.getInstance().addParticipant( new AddGroupParticipantDTO(
            createdGroup,
            createdGroup.getAdmin(),
            RoleService.getInstance().getGroupAdminRole( createdGroup )
        ) );

        return createdGroup;
    }

    public Group updateGroup( @NotNull UpdateGroupDTO dto ) {
        var group = findByIdOrPathOrThrow( dto.group() );
        checkPermissionToEdit( group );

        dto.name().ifPresent( name -> {
            if ( !name.isBlank() ) group.setName( name.trim() );
        } );

        dto.description().ifPresent( description -> {
            if ( !description.isBlank() ) group.setDescription( description.trim() );
        } );

        dto.groupType().ifPresent( typeName -> {
            var type = CastingUtil.getEnum( GroupType.class , typeName );
            type.ifPresentOrElse(
                group::setType,
                () -> { throw new UniversiBadRequestException( "Tipo de Grupo '" + typeName + "' não existe" ); }
            );
        } );

        dto.image().ifPresent( imageId -> {
            group.setImage( imageMetadataService.findOrThrow( imageId ) );
        } );

        dto.bannerImage().ifPresent( bannerImageId -> {
            group.setBannerImage( imageMetadataService.findOrThrow( bannerImageId ) );
        } );

        dto.headerImage().ifPresent( headerImageId -> {
            group.setHeaderImage( imageMetadataService.findOrThrow( headerImageId ) );
        } );

        dto.canCreateSubgroup().ifPresent( group::setCanCreateGroup );
        dto.isPublic().ifPresent( group::setPublicGroup );
        dto.canJoin().ifPresent( group::setCanEnter );

        return this.groupRepository.saveAndFlush( group );
    }

    public List<Group> subGroups(UUID groupId) {
        Group group = findOrThrow( groupId );

        RoleService.getInstance().checkPermission(group, FeaturesTypes.GROUP, Permission.READ);

        return group.getSubGroups()
            .stream()
            .filter( this::isValid )
            .sorted( Comparator.comparing( Group::getCreatedAt ).reversed() )
            .toList();
    }

    public Collection<Folder> listFolders(UUID groupId) {
        Group group = findOrThrow( groupId );

        // check permission contents
        RoleService.getInstance().checkPermission(group, FeaturesTypes.CONTENT, Permission.READ);

        return group.getFoldersGrantedAccess();
    }

    public void deleteGroup( UUID groupId ) {
        Group group = findOrThrow( groupId );

        RoleService.getInstance().checkPermission(group, FeaturesTypes.GROUP, Permission.READ_WRITE_DELETE);
        checkPermissionToDelete( group );

        delete( group );
    }

    public Collection<Role> findRoles( UUID groupId ) {
        return RoleService.getInstance().findByGroup( groupId );
    }
}
