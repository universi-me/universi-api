package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.api.exceptions.UniversiNoEntityException;
import me.universi.api.exceptions.UniversiUnprocessableOperationException;
import me.universi.api.interfaces.EntityService;
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
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.image.services.ImageMetadataService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.services.RoleService;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;
import me.universi.util.RandomUtil;

@Service
public class FolderService extends EntityService<Folder> {
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
    private final UserService userService;
    private final RoleService roleService;
    private final FolderContentsRepository folderContentsRepository;
    private final ImageMetadataService imageMetadataService;

    public FolderService(GroupService groupService, ProfileService profileService, CategoryService categoryService, FolderRepository folderRepository, FolderProfileRepository folderProfileRepository, FolderFavoriteRepository folderFavoriteRepository, ContentStatusRepository contentStatusRepository, CompetenceTypeService competenceTypeService, UserService userService, RoleService roleService, FolderContentsRepository folderContentsRepository, ImageMetadataService imageMetadataService) {
        this.groupService = groupService;
        this.profileService = profileService;
        this.categoryService = categoryService;
        this.folderRepository = folderRepository;
        this.folderProfileRepository = folderProfileRepository;
        this.folderFavoriteRepository = folderFavoriteRepository;
        this.contentStatusRepository = contentStatusRepository;
        this.competenceTypeService = competenceTypeService;
        this.userService = userService;
        this.roleService = roleService;
        this.folderContentsRepository = folderContentsRepository;
        this.imageMetadataService = imageMetadataService;

        this.entityName = "Conteúdo";
    }

    public static FolderService getInstance() {
        return Sys.context.getBean("folderService", FolderService.class);
    }

    @Override
    public List<Folder> findAllUnchecked() {
        return folderRepository.findAll();
    }

    @Override
    public Optional<Folder> findUnchecked( UUID id ) {
        return folderRepository.findById( id );
    }

    public Optional<Folder> findByReference(String reference) {
        return folderRepository.findFirstByReference(reference);
    }

    public Folder findByReferenceOrThrow( String reference ) throws UniversiNoEntityException {
        return findByReference( reference ).orElseThrow( () -> makeNotFoundException( "referência", reference ) );
    }

    public Optional<Folder> findByIdOrReference( String idOrReference ) {
        return folderRepository.findFirstByIdOrReference( CastingUtil.getUUID(idOrReference).orElse(null), idOrReference );
    }

    public List<Optional<Folder>> findByIdOrReference( List<String> idsOrReferences ) {
        return new ArrayList<>( idsOrReferences.stream().map( this::findByIdOrReference ).toList() );
    }

    public Folder findByIdOrReferenceOrThrow( String idOrReference ) throws UniversiNoEntityException {
        return findByIdOrReference( idOrReference ).orElseThrow( () -> makeNotFoundException( "ID ou referência", idOrReference ) );
    }

    public List<Folder> findByIdOrReferenceOrThrow( List<String> idsOrReferences ) {
        return new ArrayList<>( idsOrReferences.stream().map( this::findByIdOrReferenceOrThrow ).toList() );
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

    public Folder create( CreateFolderDTO createFolderDTO ) throws UniversiForbiddenAccessException {
        var folder = new Folder();
        folder.setName( createFolderDTO.name() );
        folder.setReference( generateAvailableReference() );
        folder.setAuthor( profileService.getProfileInSessionOrThrow() );
        if ( createFolderDTO.image() != null )
            folder.setImage( imageMetadataService.findOrThrow( createFolderDTO.image() ) );
        folder.setDescription( createFolderDTO.description() );
        folder.setRating( createFolderDTO.rating() );
        folder.setPublicFolder( createFolderDTO.publicFolder().orElse( false ) );
        folder.setCategories( categoryService.findOrThrow(
            createFolderDTO.categoriesIds() == null ? new ArrayList<>() : createFolderDTO.categoriesIds()
        ) );

        if ( createFolderDTO.grantedAccessGroupsIds() != null ) {
            List<String> deniedAccessGroups = new ArrayList<>();
            List<Group> groupsFetched = new ArrayList<>();

            createFolderDTO.grantedAccessGroupsIds().forEach( g -> {
                var group = groupService.findByIdOrPathOrThrow( g );

                if ( !roleService.hasPermission( group , FeaturesTypes.CONTENT, Permission.READ_WRITE ) )
                    deniedAccessGroups.add( "'" + g + "'" );

                groupsFetched.add( group );
            } );

            if ( !deniedAccessGroups.isEmpty() )
                throw new UniversiForbiddenAccessException(
                    "Você não tem permissão para criar este conteúdo no(s) seguinte(s) grupo(s): "
                    + String.join( ", " , deniedAccessGroups )
                );

            else
                folder.setGrantedAccessGroups( groupsFetched );
        }

        folder.setGrantsBadgeToCompetences( competenceTypeService.findOrThrow(
            createFolderDTO.competenceTypeBadgeIds() == null ? new ArrayList<>() : createFolderDTO.competenceTypeBadgeIds() )
        );

        final var savedFolder = saveOrUpdate( folder );
        savedFolder.getGrantedAccessGroups().forEach( g -> groupService.didAddNewContentToGroup( g , savedFolder ) );

        return savedFolder;
    }

    public Folder edit( String idOrReference, UpdateFolderDTO updateFolderDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissionToEdit( folder );

        if ( updateFolderDTO.name() != null && !updateFolderDTO.name().isBlank() ) {
            folder.setName( updateFolderDTO.name() );
        }

        if ( updateFolderDTO.image() != null ) {
            folder.setImage( imageMetadataService.findOrThrow( updateFolderDTO.image() ) );
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
            folder.setCategories( new ArrayList<>(categoryService.findOrThrow( updateFolderDTO.categoriesIds() )) );
        }

        updateFolderDTO.grantedAccessGroups().ifPresent( granted -> {
            // Guarantees the list is mutable
            var groups = new ArrayList<Group>( granted.size() );
            granted.forEach( g -> groups.add( groupService.findByIdOrPathOrThrow( g ) ) );

            folder.setGrantedAccessGroups( groups );
        } );

        updateFolderDTO.removeGrantedAccessGroups().ifPresent( removed -> {
            var removedGroups = removed.stream().map( groupService::findByIdOrPathOrThrow ).toList();
            folder.getGrantedAccessGroups().removeAll( removedGroups );
        } );

        updateFolderDTO.addGrantedAccessGroups().ifPresent( added -> {
            var addedGroups = added.stream().map( groupService::findByIdOrPathOrThrow ).toList();
            folder.getGrantedAccessGroups().addAll( addedGroups );
        } );

        if ( updateFolderDTO.competenceTypeBadgeIds() != null ) {
            folder.setGrantsBadgeToCompetences( new ArrayList<>(competenceTypeService.findOrThrow( updateFolderDTO.competenceTypeBadgeIds() )) );
        }

        return saveOrUpdate( folder );
    }

    public void delete( String idOrReference ) {
        Folder folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissionToDelete( folder );

        folder.setDeleted( true );
        saveOrUpdate ( folder );
    }

    public void changeContents( String idOrReference, ChangeFolderContentsDTO changeFolderContentsDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissionToEdit( folder );

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

        folderContentsRepository.saveAllAndFlush( newContentList );
    }

    public void changeContentPosition( String idOrReference, UUID contentId, ChangeContentPositionDTO changeContentPositionDTO ) throws IllegalArgumentException {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissionToEdit( folder );

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

    public int getPositionOfContent( UUID folderId, UUID contentId ) throws UniversiUnprocessableOperationException {
        return folderContentsRepository.findByFolderIdAndContentId( folderId, contentId )
            .orElseThrow( () -> new UniversiUnprocessableOperationException( "O conteúdo não possui o material informado" ) )
            .getOrderNum();
    }

    public void changeAssignments( String idOrReference, ChangeFolderAssignmentsDTO changeFolderAssignmentsDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        checkPermissionToEdit( folder );

        var profileInSession = profileService.getProfileInSessionOrThrow();

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

    public List<FolderProfile> getAssignments( @Nullable String idOrReference, @Nullable String assignedBy, @Nullable String assignedTo ) throws UniversiForbiddenAccessException {
        Profile assignedByProfile = Optional.ofNullable( assignedBy )
            .map( profileService::findByIdOrUsernameOrThrow )
            .orElse( null );

        Profile assignedToProfile = Optional.ofNullable( assignedTo )
            .map( profileService::findByIdOrUsernameOrThrow )
            .orElse( null );

        if ( !userService.isUserAdminSession() ) {
            // Validate search for non-admin user

            if ( assignedTo == null && assignedBy == null )
                // Must specify assignedTo and assignedBy
                throw new IllegalArgumentException( "Os parâmetros 'assignedBy' e 'assignedTo' não foram informados" );

            // Checking for assigned by anyone -> Can only check assigned to themselves
            if ( assignedByProfile == null
                && assignedToProfile != null
                && !profileService.isSessionOfProfile( assignedToProfile )
            )
                throw new UniversiForbiddenAccessException( "Você não pode ver atribuições para outros usuários" );

            // Checking for assigned to anyone -> Can only check assigned by themselves
            else if ( assignedToProfile == null
                && assignedByProfile != null
                && !profileService.isSessionOfProfile( assignedByProfile )
            )
                throw new UniversiForbiddenAccessException( "Você não pode ver atribuições de outros usuários" );

            // Checking assigned to and by someone -> Can only check if assigned by or to themselves
            else if ( assignedToProfile != null
                && assignedByProfile != null
                && !profileService.isSessionOfProfile( assignedToProfile )
                && !profileService.isSessionOfProfile( assignedByProfile )
            )
                throw new UniversiForbiddenAccessException( "Você não pode ver atribuições não relacionadas a você" );
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

    public void favorite( String idOrReference ) throws UniversiForbiddenAccessException {
        Folder folder = findByIdOrReferenceOrThrow( idOrReference );

        var profileInSession = profileService.getProfileInSessionOrThrow();
        var folderFavorite = folderFavoriteRepository
            .findByFolderIdAndProfileId( folder.getId(), profileInSession.getId() )
            .orElse( null );

        if ( folderFavorite != null )
            return;

        if ( !hasPermissionToView( folder ) )
            throw new UniversiForbiddenAccessException( "Essa pasta não existe ou você não pode favoritá-la" );

        folderFavorite = new FolderFavorite();
        folderFavorite.setFolder(folder);
        folderFavorite.setProfile( profileInSession );

        folderFavoriteRepository.saveAndFlush( folderFavorite );
    }

    public void unfavorite( String idOrReference ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );

        var profileInSession = profileService.getProfileInSessionOrThrow();
        var folderFavorite = folderFavoriteRepository
            .findByFolderIdAndProfileId( folder.getId(), profileInSession.getId() );

        if ( folderFavorite.isEmpty() )
            return;

        var folderFavoriteGet = folderFavorite.get();
        folderFavoriteGet.setDeleted( true );
        folderFavoriteRepository.saveAndFlush( folderFavoriteGet );
    }

    public List<WatchProfileProgressDTO> watch( String idOrReference, String idOrUsername ) throws UniversiForbiddenAccessException {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        var profile = profileService.findByIdOrUsernameOrThrow( idOrUsername );

        if ( !canCheckProfileProgress( profile, folder ) )
            throw new UniversiForbiddenAccessException( "Você não pode checar o progresso deste usuário para esse conteúdo" );

        return folderContentsRepository.findByFolderIdOrderByOrderNumAsc( folder.getId() ).stream()
            .map( c -> new WatchProfileProgressDTO( profile , c.getContent() ) )
            .toList();
    }

    public Folder duplicate( String idOrReference, DuplicateFolderDTO duplicateFolderDTO ) {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        var image = folder.getImage();
        var imageId = image != null
            ? image.getId()
            : null;

        var copy = create( new CreateFolderDTO(
            folder.getName(),
            imageId,
            folder.getDescription(),
            folder.getRating(),
            Optional.of( folder.isPublicFolder() ),
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

    public void moveFolder( String idOrReference, MoveFolderDTO moveFolderDTO ) throws UniversiConflictingOperationException {
        var folder = findByIdOrReferenceOrThrow( idOrReference );
        var originalGroup = groupService.findByIdOrPathOrThrow( moveFolderDTO.originalGroupId() );
        var profileInSession = profileService.getProfileInSessionOrThrow();

        roleService.checkPermission(
            profileInSession,
            originalGroup,
            FeaturesTypes.CONTENT,
            Permission.READ_WRITE_DELETE
        );

        var newGroup = groupService.findByIdOrPathOrThrow( moveFolderDTO.newGroupId() );
        roleService.checkPermission(
            profileInSession,
            newGroup,
            FeaturesTypes.CONTENT,
            Permission.READ_WRITE
        );

        if ( newGroup.getId().equals( originalGroup.getId() ) )
            return;

        if ( folderIsInGroup( folder, newGroup ) ) {
            throw new UniversiConflictingOperationException( "O grupo já contém o conteúdo" );
        }

        var newGrantedAccessGroups = new ArrayList<Group>( folder.getGrantedAccessGroups() );
        newGrantedAccessGroups.removeIf( g -> g.getId().equals( originalGroup.getId() ) );
        newGrantedAccessGroups.add( newGroup );

        folder.setGrantedAccessGroups( newGrantedAccessGroups );
        folder = folderRepository.saveAndFlush( folder );

        groupService.didImportContentToGroup( newGroup, folder );
    }

    public Collection<FolderFavorite> listFavorites(UUID profileId) throws CapacityException {
        return profileService.findOrThrow( profileId )
            .getFavoriteFolders();
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
                    profileService.getProfileInSessionOrThrow().getId().toString(),
                    profile.getId().toString()
                ).isEmpty();
    }

    public boolean folderIsInGroup( @NonNull Folder folder, @NonNull Group group ) {
        return folder.getGrantedAccessGroups().stream().anyMatch( g -> g.getId().equals( group.getId() ) );
    }

    public boolean hasPermissionToView( Folder folder ) {
        if ( folder.isPublicFolder() )
            return true;

        var profileInSession = profileService.getProfileInSession();
        return profileInSession.isPresent()
            && ( profileInSession.get().getGroups().stream().anyMatch(
                profileGroup -> folderIsInGroup( folder, profileGroup.getGroup() )
            ) || profileInSession.get().getId().equals( folder.getAuthor().getId() ) );
    }

    @Override
    public boolean hasPermissionToEdit( Folder folder ) {
        return profileService.isSessionOfProfile( folder.getAuthor() )
            || userService.isUserAdminSession();
    }

    public boolean hasPermissionToEdit( Collection<Folder> folders ) {
        return folders.stream().allMatch( this::hasPermissionToEdit );
    }

    public void checkPermissionToEdit( Collection<Folder> folders ) throws UniversiForbiddenAccessException {
        var deniedAccessFoldersNames = folders.stream()
            .filter( f -> !this.hasPermissionToEdit( f ) )
            .map( Folder::getName )
            .toList();

        if ( !deniedAccessFoldersNames.isEmpty() ) {
            throw new UniversiForbiddenAccessException(
                "Você não tem permissão para alterar o(s) seguinte(s) conteúdo(s): "
                + String.join( "," , deniedAccessFoldersNames )
            );
        }
    }

    @Override
    public boolean hasPermissionToDelete( Folder folder ) {
        return hasPermissionToEdit( folder );
    }
}
