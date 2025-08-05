package me.universi.group.services;

import jakarta.transaction.Transactional;
import me.universi.Sys;
import me.universi.activity.entities.Activity;
import me.universi.activity.services.ActivityService;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.api.exceptions.UniversiNoEntityException;
import me.universi.api.interfaces.EntityService;
import me.universi.capacity.entidades.Folder;
import me.universi.competence.entities.Competence;
import me.universi.competence.services.CompetenceService;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.services.GroupFeedService;
import me.universi.group.DTO.*;

import me.universi.group.entities.*;
import me.universi.group.repositories.*;
import me.universi.image.services.ImageMetadataService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.role.entities.Role;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.services.RoleService;
import me.universi.user.entities.User;
import me.universi.user.services.*;
import me.universi.util.CastingUtil;
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
    private final GroupEnvironmentRepository groupEnvironmentRepository;
    private final CompetenceService competenceService;
    private final ImageMetadataService imageMetadataService;
    private final EnvironmentService environmentService;
    private final LoginService loginService;
    private final AccountService accountService;
    private final EmailService emailService;
    private final ActivityService activityService;
    private final GroupTypeService groupTypeService;

    public GroupService(UserService userService, GroupFeedService groupFeedService, GroupRepository groupRepository, GroupSettingsRepository groupSettingsRepository, GroupEnvironmentRepository groupEnvironmentRepository, CompetenceService competenceService, ImageMetadataService imageMetadataService, EnvironmentService environmentService, LoginService loginService, AccountService accountService, EmailService emailService, ActivityService activityService, GroupTypeService groupTypeService) {
        this.userService = userService;
        this.groupFeedService = groupFeedService;
        this.groupRepository = groupRepository;
        this.groupSettingsRepository = groupSettingsRepository;
        this.groupEnvironmentRepository = groupEnvironmentRepository;
        this.competenceService = competenceService;
        this.imageMetadataService = imageMetadataService;
        this.activityService = activityService;
        this.groupTypeService = groupTypeService;

        this.entityName = "Grupo";
        this.environmentService = environmentService;
        this.loginService = loginService;
        this.accountService = accountService;
        this.emailService = emailService;
    }


    public static GroupService getInstance() {
        return Sys.context().getBean("groupService", GroupService.class);
    }

    public static @NotNull GroupRepository getRepository() {
        return Sys.context().getBean( "groupRepository", GroupRepository.class );
    }

    @Override
    public boolean isValid( Group group ) {
        if ( group == null || !groupRepository.existsByIdAndDeletedFalse(group.getId()) )
            return false;

        if ( group.isRootGroup() )
            return true;

        if ( !loginService.userIsLoggedIn() )
            return false;

        var userInSession = loginService.getUserInSession();
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

    public List<Group> findByType( GroupType type ) {
        return groupRepository.findByType( type ).stream().filter( this::isValid ).toList();
    }

    public boolean existsByType( GroupType type ) {
        return groupRepository.existsByType( type );
    }

    @Override
    public boolean hasPermissionToEdit( Group group ) {
        return loginService.userIsLoggedIn()
            && hasPermissionToEdit( group, loginService.getUserInSession() );
    }

    @Override
    public boolean hasPermissionToDelete( Group group ) {
        return !group.isRootGroup() && hasPermissionToEdit( group );
    }

    public void checkPermissionToEdit(Group group, User user) {
        if ( group == null )
            throw new UniversiNoEntityException( "Grupo não encontrado" );

        if (user == null)
            throw new UniversiNoEntityException("Usuário não encontrado.");

        if(userService.isUserAdmin(user)) {
            return;
        }

        Profile profile = user.getProfile();
        if (userService.userNeedAnProfile(user, true)) {
            throw new UniversiForbiddenAccessException("Você precisa criar um Perfil.");
        }

        if(Objects.equals(group.getAdmin().getId(), profile.getId())) {
            return;
        }

        if ( RoleService.getInstance().isAdmin( profile, group ) )
            return;

        throw new UniversiForbiddenAccessException("Você não tem permissão para editar este grupo.");
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
        if ( !accountService.usernameRegex( nickname ) )
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

    public GroupEnvironment getGroupEnvironment(Group group) {
        if(group == null) {
            return null;
        }
        GroupSettings groupSettings = group.getGroupSettings();
        if(groupSettings == null) {
            return null;
        }
        GroupEnvironment groupEnvironment = groupSettings.getEnvironment();
        if(groupEnvironment == null) {
            groupEnvironment = new GroupEnvironment();
            groupEnvironment.setGroupSettings(groupSettings);
            groupEnvironment = groupEnvironmentRepository.save(groupEnvironment);
        }
        return groupEnvironment;
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

    // send email for all users in group
    public void sendEmailForAllUsersInGroup(Group group, String subject, String message) {
        if(group == null || subject == null || message == null) {
            return;
        }
        Collection<ProfileGroup> participants = group.getParticipants();
        for(ProfileGroup profileGroup : participants) {
            Profile profile = profileGroup.getProfile();
            if(profile != null && profile.getUser() != null) {
                emailService.sendSystemEmailToUser(profile.getUser(), subject, message, true);
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
        String contentUrl = environmentService.getPublicUrl() + "/group" + group.getPath() + "#" + "contents/" + folder.getId();

        String message = OrganizationService.getInstance().getEnvironment().message_template_new_content;
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
        String contentUrl = environmentService.getPublicUrl() + "/content/" + folder.getReference();

        String message = OrganizationService.getInstance().getEnvironment().message_template_assigned_content;
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
        groupPostDTO.setAuthorId(loginService.getUserInSession().getId().toString());

        groupFeedService.createGroupPost(group.getId().toString(), groupPostDTO);
    }

    // alert all users in group for a new content in group
    public void alertAllUserInGroupForNewContent(Group group, Folder folder) {
        if(group == null || folder == null) {
            return;
        }

        if(!OrganizationService.getInstance().getEnvironment().message_new_content_enabled) {
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

        if(!OrganizationService.getInstance().getEnvironment().message_assigned_content_enabled) {
            return;
        }

        String subject = "Conteúdo atribuído";
        String message = getMessageTemplateForContentAssigned(fromUser, profile, folder);

        emailService.sendSystemEmailToUser(profile.getUser(), subject, message, true);
    }

    public Group createGroup( CreateGroupDTO dto ) { return createGroup( dto, true ); }
    public Group createGroup( CreateGroupDTO dto, boolean checkTypeAssignment ) {
        if ( dto.parentGroup().isEmpty() && !userService.isUserAdminSession() )
            throw new UniversiBadRequestException( "O Parâmetro 'parentGroup' deve ser informado" );

        var parentGroup = dto.parentGroup().map( this::findByIdOrPathOrThrow );
        var nickname = checkNicknameAvailable( dto.nickname(), parentGroup.orElse( null ) );

        var group = new Group();
        group.setAdmin( loginService.getUserInSession().getProfile() );
        group.setSubGroups( Arrays.asList() );

        group.setNickname( nickname );
        group.setName( dto.name() );
        group.setDescription( dto.description() );

        var groupType = groupTypeService.findByIdOrNameOrThrow( dto.type() );

        if ( checkTypeAssignment )
            groupTypeService.checkCanBeAssigned( groupType );

        group.setType( groupType );

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

        dto.type().ifPresent( typeName -> {
            var type = groupTypeService.findByIdOrNameOrThrow( typeName );

            // Prevents next checks from causing issues
            if ( group.getType().getId().equals( type.getId() ) ) return;

            groupTypeService.checkCanBeAssigned( type );
            if ( group.isActivityGroup() )
                throw new UniversiConflictingOperationException( "Um grupo de Atividade não pode ter seu tipo alterado" );

            group.setType( type );
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

        dto.canCreateSubgroup().ifPresent( canCreateSubgroup -> {
            if ( group.isActivityGroup() && canCreateSubgroup )
                throw new UniversiConflictingOperationException( "Um grupo de Atividade não pode ter subgrupos" );
            group.setCanCreateGroup( canCreateSubgroup );
        } );

        dto.isPublic().ifPresent( isPublic -> {
            if ( group.isActivityGroup() && !isPublic )
                throw new UniversiConflictingOperationException( "Um grupo de Atividade deve ser sempre público" );
            group.setPublicGroup( isPublic );
        } );

        dto.canJoin().ifPresent( canJoin -> {
            if ( group.isActivityGroup() && canJoin )
                throw new UniversiConflictingOperationException( "Pessoas não podem entrar livremente em um grupo de Atividade" );
            group.setCanEnter( canJoin );
        } );

        return this.groupRepository.saveAndFlush( group );
    }

    public List<Group> subGroups(UUID groupId) {
        Group group = findOrThrow( groupId );

        RoleService.getInstance().checkPermission(group, FeaturesTypes.GROUP, Permission.READ);

        return group.getSubGroups()
            .stream()
            .filter( g -> this.isValid( g ) && g.isRegularGroup() )
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

    public List<Profile> listAdministrators( UUID groupId ) { return listAdministrators( findOrThrow( groupId ) ); }
    public List<Profile> listAdministrators( String groupId ) { return listAdministrators( findByIdOrPathOrThrow( groupId ) ); }
    public List<Profile> listAdministrators( @NotNull Group group ) {
        if ( !hasPermissionToEdit( group ) )
            throw new UniversiForbiddenAccessException( "Você não tem permissão para gerenciar este grupo" );

        return group.getAdministrators().stream()
            .sorted( Comparator.comparing( ProfileGroup::getJoined ).reversed() )
            .map( ProfileGroup::getProfile )
            .filter( ProfileService.getInstance()::isValid )
            .toList();
    }

    public List<Activity> listActivities( UUID groupId ) { return listActivities( findOrThrow( groupId ) ); }
    public List<Activity> listActivities( String groupId ) { return listActivities( findByIdOrPathOrThrow( groupId ) ); }
    public List<Activity> listActivities( @NotNull Group group ) {
        RoleService.getInstance().checkPermission( group, FeaturesTypes.GROUP, Permission.READ );

        return activityService
            .findByGroup( group )
            .stream()
            .sorted( Comparator.comparing( Activity::getStartDate ) )
            .toList();
    }
}
