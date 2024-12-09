package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.capacity.dto.ChangeContentPositionDTO;
import me.universi.capacity.dto.ChangeFolderAssignmentsDTO;
import me.universi.capacity.dto.ChangeFolderContentsDTO;
import me.universi.capacity.dto.CreateFolderDTO;
import me.universi.capacity.dto.DuplicateFolderDTO;
import me.universi.capacity.dto.MoveFolderDTO;
import me.universi.capacity.dto.UpdateFolderDTO;
import me.universi.capacity.dto.WatchProfileProgressDTO;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.entidades.FolderContents;
import me.universi.capacity.entidades.FolderFavorite;
import me.universi.capacity.entidades.FolderProfile;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.ContentStatusRepository;
import me.universi.capacity.repository.FolderContentsRepository;
import me.universi.capacity.repository.FolderFavoriteRepository;
import me.universi.capacity.repository.FolderProfileRepository;
import me.universi.capacity.repository.FolderRepository;
import me.universi.competence.dto.CreateCompetenceDTO;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.services.RolesService;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;
import me.universi.util.RandomUtil;

@Service
public class FolderService {
    @PersistenceContext
    private EntityManager entityManager;

    private final GroupService groupService;
    private final ProfileService profileService;
    private final CategoryService categoryService;
    private final FolderRepository folderRepository;
    private final FolderProfileRepository folderProfileRepository;
    private final FolderFavoriteRepository folderFavoriteRepository;
    private final ContentStatusRepository contentStatusRepository;
    private final CompetenceTypeService competenceTypeService;
    private final CompetenceService competenceService;
    private final UserService userService;
    private final RolesService rolesService;
    private final FolderContentsRepository folderContentsRepository;

    public FolderService(GroupService groupService, ProfileService profileService, CategoryService categoryService, FolderRepository folderRepository, FolderProfileRepository folderProfileRepository, FolderFavoriteRepository folderFavoriteRepository, ContentStatusRepository contentStatusRepository, CompetenceTypeService competenceTypeService, CompetenceService competenceService, UserService userService, RolesService rolesService, FolderContentsRepository folderContentsRepository) {
        this.groupService = groupService;
        this.profileService = profileService;
        this.categoryService = categoryService;
        this.folderRepository = folderRepository;
        this.folderProfileRepository = folderProfileRepository;
        this.folderFavoriteRepository = folderFavoriteRepository;
        this.contentStatusRepository = contentStatusRepository;
        this.competenceTypeService = competenceTypeService;
        this.competenceService = competenceService;
        this.userService = userService;
        this.rolesService = rolesService;
        this.folderContentsRepository = folderContentsRepository;
    }

    public static FolderService getInstance() {
        return Sys.context.getBean("folderService", FolderService.class);
    }

    public List<Folder> findAll() {
        return folderRepository.findAll();
    }

    public boolean hasPermissions( Folder folder, boolean forWrite ) {
        final var fetchedFolder = findOrThrow( folder.getId() );

        if ( profileService.isSessionOfProfile( fetchedFolder.getAuthor() )
            || userService.isUserAdminSession()
        ) {
            /* Folder author and admins always have access */
            return true;
        }

        if ( forWrite ) {
            /* Only admin and author have write access */
            return false;
        }

        if ( fetchedFolder.isPublicFolder() ) {
            /* Everyone has reading access to public folder */
            return true;
        }

        return profileService.getProfileInSession().getGroups().stream().anyMatch( profileGroup ->
            fetchedFolder.getGrantedAccessGroups().stream().anyMatch(
                // User is in a group with access to the folder
                folderGroup -> folderGroup.getId().equals( profileGroup.getGroup().getId() )
            )
        );
    }

    public List<Boolean> hasPermissions( @NotNull List<Folder> folders, boolean forWrite ) {
        return folders.stream().map( f -> hasPermissions( f, forWrite ) ).toList();
    }

    public void checkPermissions( Folder folder, boolean forWrite ) {
        if ( !hasPermissions( folder, forWrite ) )
            throw new AccessDeniedException(
                forWrite
                    ? "Você não tem permissão para alterar este conteúdo"
                    : "Você não tem permissão para ver este conteúdo"
            );
    }

    public void checkPermissions( @NotNull List<Folder> folders, boolean forWrite ) throws AccessDeniedException {
            List<String> deniedAccessFolders = new ArrayList<>();
            for ( var folder : folders ) {
                if ( !hasPermissions( folder , true ) )
                    deniedAccessFolders.add( "'" + folder.getId() + "'" );
            }

            if ( !deniedAccessFolders.isEmpty() )
                throw new AccessDeniedException(
                    "Você não tem permissão para alterar o(s) seguinte(s) conteúdo(s): "
                    + String.join( "," , deniedAccessFolders )
                );
    }

    public Optional<Folder> find( UUID id ) {
        return folderRepository.findById( id );
    }

    public Folder findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException("Conteúdo com ID '" + id + "' não encontrado") );
    }

    public Optional<Folder> findByReference(String reference) {
        return folderRepository.findFirstByReference(reference);
    }

    public Folder findByReferenceOrThrow( String reference ) throws EntityNotFoundException {
        return findByReference( reference ).orElseThrow( () -> new EntityNotFoundException("Conteúdo com referência '" + reference + "' não encontrado") );
    }

    public Optional<Folder> findByIdOrReference( String idOrReference ) {
        return folderRepository.findFirstByIdOrReference( CastingUtil.getUUID(idOrReference).orElse(null), idOrReference );
    }

    public Folder findByIdOrReferenceOrThrow( String idOrReference ) throws EntityNotFoundException {
        return findByIdOrReference( idOrReference ).orElseThrow( () -> new EntityNotFoundException( "Conteúdo com ID ou referência '" + idOrReference + "' não encontrado" ) );
    }

    public List<Folder> findByCategory(UUID categoryId) throws CapacityException {
        Category category = categoryService.findOrThrow(categoryId);
        return folderRepository.findByCategories(category);
    }

    public List<Folder> findByCategory(String categoryId) throws CapacityException {
        return findByCategory(UUID.fromString(categoryId));
    }

    private Folder saveOrUpdate( Folder folder ) {
        return folderRepository.saveAndFlush( folder );
    }

    public Folder create( CreateFolderDTO createFolderDTO ) {
        var folder = new Folder();
        folder.setName( createFolderDTO.name() );
        folder.setReference( generateAvailableReference() );
        folder.setAuthor( profileService.getProfileInSession() );
        folder.setImage( createFolderDTO.image() );
        folder.setDescription( createFolderDTO.description() );
        folder.setRating( createFolderDTO.rating() );
        folder.setPublicFolder( createFolderDTO.publicFolder() );
        folder.setCategories( categoryService.findOrThrow(
            createFolderDTO.categoriesIds() == null ? Arrays.asList() : createFolderDTO.categoriesIds()
        ) );

        if ( createFolderDTO.grantedAccessGroupsIds() != null ) {
            List<String> deniedAccessGroups = new ArrayList<>();
            List<Group> groupsFetched = new ArrayList<>();

            createFolderDTO.grantedAccessGroupsIds().forEach( g -> {
                // TODO: GroupService.findByIdOrPathOrElseThrow
                var group = groupService.getGroupByGroupIdOrGroupPath(
                    CastingUtil.getUUID( g ).orElse(null),
                    g
                );

                if ( !rolesService.hasPermission( group , FeaturesTypes.CONTENT, Permission.READ_WRITE ) )
                    deniedAccessGroups.add( "'" + g + "'" );

                groupsFetched.add( group );
            } );

            if ( !deniedAccessGroups.isEmpty() )
                throw new AccessDeniedException(
                    "Você não tem permissão para criar este conteúdo no(s) seguinte(s) grupo(s): "
                    + String.join( ", " , deniedAccessGroups )
                );

            else
                folder.setGrantedAccessGroups( groupsFetched );
        }

        folder.setGrantsBadgeToCompetences( competenceTypeService.findOrThrow(
            createFolderDTO.competenceTypeBadgeIds() == null ? Arrays.asList() : createFolderDTO.competenceTypeBadgeIds() )
        );

        final var savedFolder = saveOrUpdate( folder );
        savedFolder.getGrantedAccessGroups().forEach( g -> groupService.didAddNewContentToGroup( g , savedFolder ) );

        return savedFolder;
    }

    public Folder edit( String idOrReference, UpdateFolderDTO updateFolderDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissions( folder, true );

        if ( updateFolderDTO.name() != null && !updateFolderDTO.name().isBlank() ) {
            folder.setName( updateFolderDTO.name() );
        }

        if ( updateFolderDTO.image() != null && !updateFolderDTO.image().isBlank() ) {
            folder.setImage( updateFolderDTO.image() );
        }

        if ( updateFolderDTO.description() != null && !updateFolderDTO.description().isBlank() ) {
            folder.setDescription( updateFolderDTO.description() );
        }

        if ( updateFolderDTO.rating() != null ) {
            folder.setRating( updateFolderDTO.rating() );
        }

        if ( updateFolderDTO.publicFolder() != null ) {
            folder.setPublicFolder( updateFolderDTO.publicFolder() );
        }

        if ( updateFolderDTO.categoriesIds() != null ) {
            folder.setCategories( categoryService.findOrThrow( updateFolderDTO.categoriesIds() ) );
        }

        if ( updateFolderDTO.grantedAccessGroups() != null ) {
            var groups = new ArrayList<Group>( updateFolderDTO.grantedAccessGroups().size() );
            updateFolderDTO.grantedAccessGroups().forEach( g -> groups.add( groupService.findByIdOrPathOrThrow( g ) ) );

            folder.setGrantedAccessGroups( groups );
        }

        if ( updateFolderDTO.competenceTypeBadgeIds() != null ) {
            folder.setGrantsBadgeToCompetences( competenceTypeService.findOrThrow( updateFolderDTO.competenceTypeBadgeIds() ) );
        }

        return saveOrUpdate( folder );
    }

    public void delete( String idOrReference ) {
        Folder folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissions( folder, true );

        folderRepository.delete( folder );
    }

    public void changeContents( String idOrReference, ChangeFolderContentsDTO changeFolderContentsDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissions( folder, true );

        List<FolderContents> newContentList = new ArrayList<>( folder.getFolderContents() );

        if ( changeFolderContentsDTO.addContentsIds() != null ) {
            var nextIndex = folder.getFolderContents().size();

            for ( var cId : changeFolderContentsDTO.addContentsIds() ) {
                if ( folder.getFolderContents().stream().anyMatch( c -> c.getId().equals( cId ) ) )
                    // Content already in folder
                    continue;

                var fc = new FolderContents();
                fc.setFolder( folder );
                fc.setContent( ContentService.getInstance().findOrThrow( cId ) );
                fc.setOrderNum( nextIndex++ );

                newContentList.add( fc );
            }
        }

        if ( changeFolderContentsDTO.removeContentsIds() != null )
            newContentList = newContentList.stream()
                .filter( fc -> changeFolderContentsDTO.removeContentsIds().stream()
                    .noneMatch( cId -> fc.getContent().getId().equals( cId ) )
                ).toList();

        newContentList = folderContentsRepository.saveAllAndFlush( newContentList );
    }

    public void changeContentPosition( String idOrReference, UUID contentId, ChangeContentPositionDTO changeContentPositionDTO ) throws IllegalArgumentException {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissions( folder, true );

        if ( !folderContainsContent( folder.getId(), contentId ) )
            throw new IllegalArgumentException( "O conteúdo não possui o material informado" );

        var size = folder.getFolderContents().size();
        if ( changeContentPositionDTO.moveTo() > size )
            throw new IllegalArgumentException(
                "O conteúdo possui apenas '" + size + "' materiais. Informada posição '"
                + changeContentPositionDTO.moveTo() + "'"
            );

        var folderContents = folderContentsRepository.findByFolderId( folder.getId() );
        var originalPosition = getPositionOfContent( folder.getId(), contentId );

        if ( changeContentPositionDTO.moveTo().equals( originalPosition ) )
            return;

        var movingForward = originalPosition < changeContentPositionDTO.moveTo();

        for ( var fc : folderContents ) {
            if ( fc.getContent().getId().equals( contentId ) )
                fc.setOrderNum( changeContentPositionDTO.moveTo() );

            else if ( movingForward
                && fc.getOrderNum() > originalPosition
                && fc.getOrderNum() <= changeContentPositionDTO.moveTo()
            ) {
                fc.setOrderNum( fc.getOrderNum() - 1 );
            }

            else if ( !movingForward
                && fc.getOrderNum() >= changeContentPositionDTO.moveTo()
                && fc.getOrderNum() < originalPosition
            ) {
                fc.setOrderNum( fc.getOrderNum() + 1 );
            }
        }

        folderContentsRepository.saveAllAndFlush( folderContents );
    }

    public boolean folderContainsContent( UUID folderId, UUID contentId ) {
        return folderContentsRepository.findByFolderIdAndContentId( folderId, contentId ).isPresent();
    }

    public int getPositionOfContent( UUID folderId, UUID contentId ) throws CapacityException {
        return folderContentsRepository.findByFolderIdAndContentId( folderId, contentId )
            .orElseThrow( () -> new EntityNotFoundException( "O conteúdo não possui o material informado" ) )
            .getOrderNum();
    }

    public void changeAssignments( String idOrReference, ChangeFolderAssignmentsDTO changeFolderAssignmentsDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissions( folder, true );

        var profileInSession = profileService.getProfileInSession();

        folderProfileRepository.saveAllAndFlush(
            changeFolderAssignmentsDTO.addProfileIds().stream()
                .filter( p -> folder.getFolderContents().stream().noneMatch( p2 -> p2.getId().equals( p ) ) )
                .map( id -> {
                    var folderProfile = new FolderProfile();
                    folderProfile.setAssignedBy( profileInSession );
                    folderProfile.setAssignedTo( profileService.findOrThrow( id ) );
                    folderProfile.setFolder( folder );

                    return folderProfile;
                } )
                .toList()
        );

        folderProfileRepository.deleteAllById(
            changeFolderAssignmentsDTO.removeProfileIds().stream().map(
                id -> folderProfileRepository.findByFolderIdAndAssignedToIdAndAssignedById( folder.getId() , id, profileInSession.getId() )
            ).filter( Optional::isPresent )
            .map( fp -> fp.get().id )
            .toList()
        );
    }

    public List<FolderProfile> getAssignments( @Nullable String idOrReference, @Nullable String assignedBy, @Nullable String assignedTo ) throws AccessDeniedException {
        Profile assignedByProfile = null;
        Profile assignedToProfile = null;

        if ( !userService.isUserAdminSession() ) {
            // Validate search for non-admin user

            if ( assignedTo == null && assignedBy == null )
                // Must specify assignedTo and assignedBy
                throw new IllegalArgumentException( "Os parâmetros 'assignedBy' e 'assignedTo' não foram informados" );

            if ( assignedTo != null )
                assignedToProfile = profileService.findByIdOrUsernameOrThrow( assignedTo );

            if ( assignedBy != null )
                assignedByProfile = profileService.findByIdOrUsernameOrThrow( assignedBy );

            // Checking for assigned by anyone -> Can only check assigned to themselves
            if ( assignedByProfile == null
                && assignedToProfile != null
                && !profileService.isSessionOfProfile( assignedToProfile )
            )
                throw new AccessDeniedException( "Você não pode ver atribuições para outros usuários" );

            // Checking for assigned to anyone -> Can only check assigned by themselves
            else if ( assignedToProfile == null
                && assignedByProfile != null
                && !profileService.isSessionOfProfile( assignedByProfile )
            )
                throw new AccessDeniedException( "Você não pode ver atribuições de outros usuários" );

            // Checking assigned to and by someone -> Can only check if assigned by or to themselves
            else if ( assignedToProfile != null
                && assignedByProfile != null
                && !profileService.isSessionOfProfile( assignedToProfile )
                && !profileService.isSessionOfProfile( assignedByProfile )
            )
                throw new AccessDeniedException( "Você não pode ver atribuições não relacionadas a você" );
        }

        var folder = idOrReference != null
            ? findByIdOrReferenceOrThrow( idOrReference )
            : null;

        var criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FolderProfile> query = criteriaBuilder.createQuery( FolderProfile.class );
        var root = query.from( FolderProfile.class );
        query.select( root );

        if ( folder != null )
            query.where( criteriaBuilder.equal( root.get( "folder" ).get( "id" ), folder.getId() ) );

        if ( assignedByProfile != null )
            query.where( criteriaBuilder.equal( root.get( "assignedBy" ).get( "id" ), assignedByProfile.getId() ) );

        if ( assignedToProfile != null )
            query.where( criteriaBuilder.equal( root.get( "assignedTo" ).get( "id" ), assignedToProfile.getId() ) );

        return entityManager
            .createQuery( query )
            .getResultList();
    }

    public void favorite( String idOrReference ) throws AccessDeniedException {
        Folder folder = findByIdOrReferenceOrThrow( idOrReference );

        var profileInSession = profileService.getProfileInSession();
        var folderFavorite = folderFavoriteRepository
            .findByFolderIdAndProfileId( folder.getId(), profileInSession.getId() )
            .orElse( null );

        if ( folderFavorite != null )
            return;

        if ( !hasPermissions(folder, false) )
            throw new AccessDeniedException("Essa pasta não existe ou você não pode favoritá-la");

        folderFavorite = new FolderFavorite();
        folderFavorite.setFolder(folder);
        folderFavorite.setProfile( profileInSession );

        folderFavoriteRepository.saveAndFlush( folderFavorite );
    }

    public void unfavorite( String idOrReference ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );

        var profileInSession = profileService.getProfileInSession();
        var folderFavorite = folderFavoriteRepository
            .findByFolderIdAndProfileId( folder.getId(), profileInSession.getId() );

        if ( folderFavorite.isEmpty() )
            return;

        folderFavoriteRepository.delete( folderFavorite.get() );
    }

    public List<WatchProfileProgressDTO> watch( String idOrReference, String idOrUsername ) throws AccessDeniedException {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        var profile = profileService.findByIdOrUsernameOrThrow( idOrUsername );

        if ( !canCheckProfileProgress( profile, folder ) )
            throw new AccessDeniedException( "Você não pode checar o progresso deste usuário para esse conteúdo" );

        return folderContentsRepository.findByFolderIdOrderByOrderNumAsc( folder.getId() ).stream()
            .map( c -> new WatchProfileProgressDTO( profile , c.getContent() ) )
            .toList();
    }

    public Folder duplicate( String idOrReference, DuplicateFolderDTO duplicateFolderDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );

        var copy = create( new CreateFolderDTO(
            folder.getName(),
            folder.getImage(),
            folder.getDescription(),
            folder.getRating(),
            folder.isPublicFolder(),
            new ArrayList<>( 
                folder.getCategories().stream().map( Category::getId ).toList()
            ),
            duplicateFolderDTO.groups(),
            new ArrayList<>(
                folder.getGrantsBadgeToCompetences().stream().map( CompetenceType::getId ).toList()
            )
        ) );

        changeContents( copy.getId().toString(), new ChangeFolderContentsDTO(
            folder.getFolderContents().stream().map( fc -> fc.getContent().getId() ).toList(),
            null
        ) );

        return findOrThrow( copy.getId() );
    }

    public void moveFolder( String idOrReference, MoveFolderDTO moveFolderDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        var originalGroup = groupService.findByIdOrPathOrThrow( moveFolderDTO.originalGroupId() );
        rolesService.checkPermission(
            profileService.getProfileInSession(),
            originalGroup,
            FeaturesTypes.CONTENT,
            Permission.READ_WRITE_DELETE
        );

        var newGroup = groupService.findByIdOrPathOrThrow( moveFolderDTO.newGroupId() );
        rolesService.checkPermission(
            profileService.getProfileInSession(),
            newGroup,
            FeaturesTypes.CONTENT,
            Permission.READ_WRITE
        );

        if ( newGroup.getId().equals( originalGroup.getId() ) )
            return;

        if ( folder.getGrantedAccessGroups().stream().anyMatch( g -> g.getId().equals( newGroup.getId() ) ) ) {
            // Group already has this folder
            // TODO: UniversiInvalidOperationException
            throw new IllegalStateException( "O grupo já contém o conteúdo" );
        }

        var newGrantedAccessGroups = folder.getGrantedAccessGroups().stream()
            .filter( g -> !g.getId().equals( originalGroup.getId() ) )
            .toList();
        newGrantedAccessGroups.add( newGroup );

        folder.setGrantedAccessGroups( newGrantedAccessGroups );
        folder = folderRepository.saveAndFlush( folder );

        groupService.didImportContentToGroup( newGroup, folder );
    }

    public Collection<Folder> listFavorites(UUID profileId) throws CapacityException {
        Profile profile = profileService.findFirstById(profileId);
        if (profile == null)
            throw new CapacityException("Usuário não encontrado");

        return profile.getFavoriteFolders().stream()
            .map(FolderFavorite::getFolder)
            .toList();
    }

    public String generateAvailableReference() {
        Optional<Folder> folder;
        String reference = "";

        do {
            reference = RandomUtil.randomString(
                Folder.FOLDER_REFERENCE_SIZE,
                Folder.FOLDER_REFERENCE_AVAILABLE_CHARS
            );

            folder = folderRepository.findFirstByReference(reference);
        } while ( folder.isPresent() );

        return reference;
    }

    public List<ContentStatus> getStatuses(Profile profile, Folder folder) {
        return folder.getFolderContents().stream()
            .map(c -> contentStatusRepository.findFirstByProfileIdAndContentId(profile.getId(), c.getId()))
            .filter(Objects::nonNull)
            .toList();
    }

    public boolean isComplete(@NotNull Profile profile, @NotNull Folder folder) {
        var statuses = getStatuses(profile, folder);

        return !folder.getFolderContents().isEmpty()
            && statuses.size() == folder.getFolderContents().size()
            && statuses.stream()
                .allMatch(cs -> cs.getStatus() == ContentStatusType.DONE);
    }

    public boolean canCheckProfileProgress(Profile profile, Folder folder) {
        if (profile == null)
            return false;

        return userService.isUserAdminSession()
            || userService.isSessionOfUser(profile.getUser())
            // has assigned that folder to that user
            || !getAssignments(
                    folder.getId().toString(),
                    profileService.getProfileInSession().getId().toString(),
                    profile.getId().toString()
                ).isEmpty();
    }

    public void grantCompetenceBadge(@NotNull Collection<Folder> folder, @NotNull Profile profile) {
        for (var f : folder) grantCompetenceBadgeToProfile(f, profile);

        profileService.save(profile);
    }

    private void grantCompetenceBadgeToProfile(@NotNull Folder folder, @NotNull Profile profile) {
        if (!isComplete(profile, folder)) return;

        for (var competenceType : folder.getGrantsBadgeToCompetences()) {
            if (!profile.hasBadge(competenceType))
                profile.getCompetenceBadges().add(competenceType);

            var hasCompetence = !competenceService.findByProfileIdAndCompetenceTypeId(
                profile.getId(),
                competenceType.getId()
            ).isEmpty();

            if ( !hasCompetence ) {
                competenceService.create(
                    new CreateCompetenceDTO(
                        competenceType.getId(),
                        "",
                        Competence.MIN_LEVEL
                    ), profile
                );
            }

            if (!competenceTypeService.hasAccessToCompetenceType(competenceType, profile)) {
                competenceTypeService.grantAccessToProfile(competenceType, profile);
            }
        }
    }
}
