package me.universi.capacity.service;

import java.util.*;

import org.springframework.stereotype.Service;

import me.universi.Sys;
import me.universi.api.interfaces.EntityService;
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
import me.universi.image.services.ImageMetadataService;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

@Service
public class ContentService extends EntityService<Content> {
    private final CategoryService categoryService;
    private final ContentRepository contentRepository;
    private final FolderService folderService;
    private final ContentStatusRepository contentStatusRepository;
    private final ProfileService profileService;
    private final UserService userService;
    private final FolderContentsRepository folderContentsRepository;
    private final ImageMetadataService imageMetadataService;


    public ContentService(CategoryService categoryService, ContentRepository contentRepository, FolderService folderService, ContentStatusRepository contentStatusRepository, ProfileService profileService, UserService userService, FolderContentsRepository folderContentsRepository, ImageMetadataService imageMetadataService) {
        this.categoryService = categoryService;
        this.contentRepository = contentRepository;
        this.folderService = folderService;
        this.contentStatusRepository = contentStatusRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.folderContentsRepository = folderContentsRepository;
        this.imageMetadataService = imageMetadataService;

        this.entityName = "Material";
    }

    public static ContentService getInstance() {
        return Sys.context.getBean("contentService", ContentService.class);
    }

    @Override
    public List<Content> findAllUnchecked(){
        return contentRepository.findAll();
    }

    @Override
    public Optional<Content> findUnchecked( UUID id ) {
        return contentRepository.findById( id );
    }

    public List<Content> findByCategory(UUID categoryId) throws CapacityException {
        Category category = categoryService.findOrThrow(categoryId);
        return contentRepository.findByCategories(category);
    }

    public List<Content> findByFolder(UUID folderId) throws CapacityException {
        return folderContentsRepository.findByFolderIdOrderByOrderNumAsc( folderId ).stream()
            .map( fc -> fc.getContent() )
            .filter(Objects::nonNull)
            .toList();
    }

    private Content saveOrUpdate(Content content) {
        return contentRepository.saveAndFlush( content );
    }

    public Content create( CreateContentDTO createContentDTO ) {
        var content = new Content();
        content.setAuthor( profileService.getProfileInSessionOrThrow() );
        content.setDescription( createContentDTO.description() );
        if ( createContentDTO.image() != null )
            content.setImage( imageMetadataService.findOrThrow( createContentDTO.image() ) );
        content.setRating( createContentDTO.rating() );
        content.setTitle( createContentDTO.title() );
        content.setType( createContentDTO.type() );
        content.setUrl( createContentDTO.url() );

        if ( createContentDTO.categoriesIds() != null )
            content.setCategories( createContentDTO.categoriesIds().stream().map( categoryService::findOrThrow ).toList() );

        if ( createContentDTO.folders() != null ) {
            var folders = createContentDTO.folders().stream().map( folderService::findByIdOrReferenceOrThrow ).toList();
            folderService.checkPermissionToEdit( folders );

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
        checkPermissionToEdit( content );

        if ( updateContentDTO.description() != null && !updateContentDTO.description().isBlank() )
            content.setDescription( updateContentDTO.description() );

        if ( updateContentDTO.image() != null )
            content.setImage( imageMetadataService.findOrThrow( updateContentDTO.image() ) );

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
        checkPermissionToDelete( content );

        content.setDeleted( true );
        saveOrUpdate( content );
    }

    public ContentStatus findStatusById( UUID contentId ) {
        return findStatusById( contentId, profileService.getProfileInSessionOrThrow().getId() );
    }

    public ContentStatus findStatusById( UUID contentId, UUID profileId ) {
        var profile = profileService.findOrThrow( profileId );
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
            profileService.grantCompetenceBadge(
                contentStatus.getContent().getFolderContents().stream().map( fc -> fc.getFolder() ).toList(),
                contentStatus.getProfile()
            );
        }

        return contentStatus;
    }

    public void deleteStatus(UUID contentId) {
        var content = findOrThrow( contentId );
        checkPermissionToEdit( content );

        contentStatusRepository.deleteByContentId( content.getId() );
    }

    @Override
    public boolean hasPermissionToEdit( Content content ) {
        return profileService.isSessionOfProfile( content.getAuthor() )
            || userService.isUserAdminSession();
    }

    @Override
    public boolean hasPermissionToDelete( Content content ) {
        return hasPermissionToEdit( content );
    }
}
