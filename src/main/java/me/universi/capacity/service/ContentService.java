package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.capacity.dto.CreateContentDTO;
import me.universi.capacity.dto.UpdateContentDTO;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.entidades.FolderContents;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.ContentRepository;
import me.universi.capacity.repository.ContentStatusRepository;
import me.universi.capacity.repository.FolderContentsRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

@Service
public class ContentService {
    private final CategoryService categoryService;
    private final ContentRepository contentRepository;
    private final FolderService folderService;
    private final ContentStatusRepository contentStatusRepository;
    private final ProfileService profileService;
    private final UserService userService;
    private final FolderContentsRepository folderContentsRepository;


    public ContentService(CategoryService categoryService, ContentRepository contentRepository, FolderService folderService, ContentStatusRepository contentStatusRepository, ProfileService profileService, UserService userService, FolderContentsRepository folderContentsRepository) {
        this.categoryService = categoryService;
        this.contentRepository = contentRepository;
        this.folderService = folderService;
        this.contentStatusRepository = contentStatusRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.folderContentsRepository = folderContentsRepository;
    }

    public static ContentService getInstance() {
        return Sys.context.getBean("contentService", ContentService.class);
    }

    public List<Content> findAll(){
        List<Content> contentList = new ArrayList<>();
        contentRepository.findAll().forEach(contentList::add);

        return contentList;
    }

    public Optional<Content> find( UUID id ) {
        return contentRepository.findById( id );
    }

    public Content findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Material de ID '" + id + "' não encontrado" ) );
    }

    public List<Content> findByCategory(UUID categoryId) throws CapacityException {
        Category category = categoryService.findOrThrow(categoryId);
        return contentRepository.findByCategories(category);
    }

    public List<Content> findByFolder(UUID folderId) throws CapacityException {
        return folderContentsRepository.findByFolderIdOrderByOrderNumAsc( folderId ).stream()
            .map( fc -> fc.getContent() )
            .toList();
    }

    private Content saveOrUpdate(Content content) {
        return contentRepository.saveAndFlush( content );
    }

    public Content create( CreateContentDTO createContentDTO ) {
        var content = new Content();
        content.setAuthor( profileService.getProfileInSession() );
        content.setDescription( createContentDTO.description() );
        content.setImage( createContentDTO.image() );
        content.setRating( createContentDTO.rating() );
        content.setTitle( createContentDTO.title() );
        content.setType( createContentDTO.type() );
        content.setUrl( createContentDTO.url() );

        if ( createContentDTO.categoriesIds() != null )
            content.setCategories( createContentDTO.categoriesIds().stream().map( categoryService::findOrThrow ).toList() );

        if ( createContentDTO.folders() != null ) {
            var folders = createContentDTO.folders().stream().map( folderService::findByIdOrReferenceOrThrow ).toList();
            folderService.checkPermissions( folders , true );

            var folderContents = new ArrayList<FolderContents>( folders.size() );
            var nextIndex = createContentDTO.folders().size();

            for ( var f : folders ) {
                var fc = new FolderContents();
                fc.setFolder( f );
                fc.setContent( content );
                fc.setOrderNum( nextIndex++ );

                folderContents.add( fc );
            }

            content.setFolderContents( folderContents );
        }

        return saveOrUpdate( content );
    }

    public Content update( UUID id, UpdateContentDTO updateContentDTO ) {
        var content = findOrThrow( id );
        canEditOrThrow( content, profileService.getProfileInSession() );

        if ( updateContentDTO.description() != null && !updateContentDTO.description().isBlank() )
            content.setDescription( updateContentDTO.description() );

        if ( updateContentDTO.image() != null && !updateContentDTO.image().isBlank() )
            content.setImage( updateContentDTO.image() );

        if ( updateContentDTO.rating() != null )
            content.setRating( updateContentDTO.rating() );

        if ( updateContentDTO.title() != null && !updateContentDTO.title().isBlank() )
            content.setTitle( updateContentDTO.title() );

        if ( updateContentDTO.type() != null )
            content.setType( updateContentDTO.type() );

        if ( updateContentDTO.url() != null && !updateContentDTO.url().isBlank() )
            content.setUrl( updateContentDTO.url() );

        if ( updateContentDTO.categoriesIds() != null )
            content.setCategories( updateContentDTO.categoriesIds().stream().map( categoryService::findOrThrow ).toList() );

        return saveOrUpdate( content );
    }

    public void delete( UUID id ) {
        Content content = findOrThrow( id );
        canEditOrThrow( content, profileService.getProfileInSession() );

        contentRepository.delete( content );
    }

    public ContentStatus findStatusById( UUID contentId ) {
        return findStatusById( contentId, profileService.getProfileInSession().getId() );
    }

    public ContentStatus findStatusById( UUID contentId, UUID profileId ) {
        var profile = profileService.findFirstById( profileId );
        var contentStatus = contentStatusRepository.findFirstByProfileIdAndContentId( profileId, contentId );

        if(contentStatus == null) {
            contentStatus = new ContentStatus();
            contentStatus.setContent( findOrThrow(contentId) );
            contentStatus.setProfile( profile );
            contentStatus.setStatus( ContentStatusType.NOT_VIEWED );
        }

        return contentStatus;
    }

    public ContentStatus setStatus(UUID contentId, ContentStatusType status) throws CapacityException {
        ContentStatus contentStatus = findStatusById(contentId);
        if(contentStatus.getStatus() != status) {
            contentStatus.setStatus(status);
            contentStatus.setUpdatedAt(new java.util.Date());

            contentStatus = contentStatusRepository.save(contentStatus);
            folderService.grantCompetenceBadge(
                contentStatus.getContent().getFolderContents().stream().map( fc -> fc.getFolder() ).toList(),
                contentStatus.getProfile()
            );
        }

        return contentStatus;
    }

    public void deleteStatus(UUID contentId) {
        canEditOrThrow( findOrThrow( contentId ) , profileService.getProfileInSession() );
        contentStatusRepository.deleteByContentId(contentId);
    }

    public boolean canEdit( @NotNull Content content, @NotNull Profile profile ) {
        return content.getAuthor().getId().equals( profile.getId() )
            || userService.isUserAdmin( profile.getUser() );
    }

    public void canEditOrThrow( @NotNull Content content, @NotNull Profile profile ) throws AccessDeniedException {
        if ( !canEdit( content, profile ) )
            throw new AccessDeniedException( "Você não tem permissão para editar este material" );
    }
}
