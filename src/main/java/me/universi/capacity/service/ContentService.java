package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import me.universi.Sys;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.ContentRepository;
import me.universi.capacity.repository.ContentStatusRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

@Service
public class ContentService {
    private final CategoryService categoryService;
    private final ContentRepository contentRepository;
    private final FolderService folderService;
    private final ContentStatusRepository contentStatusRepository;

    public ContentService(CategoryService categoryService, ContentRepository contentRepository, FolderService folderService, ContentStatusRepository contentStatusRepository) {
        this.categoryService = categoryService;
        this.contentRepository = contentRepository;
        this.folderService = folderService;
        this.contentStatusRepository = contentStatusRepository;
    }

    public static ContentService getInstance() {
        return Sys.context.getBean("contentService", ContentService.class);
    }

    public List<Content> findAll(){
        List<Content> contentList = new ArrayList<>();
        contentRepository.findAll().forEach(contentList::add);

        return contentList;
    }

    public Content findById(UUID contentId) throws CapacityException {
        Content content = contentRepository.findFirstById(contentId);
        if(content == null) {
            throw new CapacityException("Conteúdo não encontrada.");
        }
        return content;
    }

    public Content findById(Object contentId) throws CapacityException {
        if (contentId == null)
            throw new CapacityException("Conteúdo não encontrado.");

        return findById(String.valueOf(contentId));
    }

    public Content findById(String contentId) throws CapacityException {
        return findById(UUID.fromString(contentId));
    }

    public List<Content> findByCategory(UUID categoryId) throws CapacityException {
        Category category = categoryService.findById(categoryId);
        return contentRepository.findByCategories(category);
    }

    public List<Content> findByCategory(String categoryId) throws CapacityException {
        return findByCategory(UUID.fromString(categoryId));
    }

    public List<Content> findByFolder(UUID folderId) throws CapacityException {
        Folder folder = folderService.findById(folderId);

        List<Content> contents = contentRepository.findContentsInFolderByOrderPosition(folder.getId());

        for(Content content : contents) {
            ContentStatus contentStatus = findStatusById(content.getId());
            if(contentStatus != null) {
                content.contentStatus = contentStatus;
            }
        }

        return contents;
    }

    public List<Content> findByFolder(String folderId) throws CapacityException {
        return findByFolder(UUID.fromString(folderId));
    }

    public boolean saveOrUpdate(Content content) throws CapacityException {
        Content updatedContent = contentRepository.save(content);
        return findById(updatedContent.getId()) != null;
    }

    public boolean delete(UUID id) throws CapacityException {
        Content content = findById(id);

        // remove from linked folders
        content.getFolders().forEach(folder -> {
            folder.getContents().remove(content);
            folderService.saveOrUpdate(folder);
        });

        // remove from linked watch`s
        deleteStatus(content.getId());

        content.setDeleted(true);
        saveOrUpdate(content);

        return true;
    }

    public ContentStatus findStatusById(UUID contentId) throws CapacityException {
        Profile userProfile = UserService.getInstance().getUserInSession().getProfile();
        ContentStatus contentStatus = contentStatusRepository.findFirstByProfileIdAndContentId(userProfile.getId(), contentId);
        if(contentStatus == null) {
            contentStatus = new ContentStatus();
            contentStatus.setContent(findById(contentId));
            contentStatus.setProfile(UserService.getInstance().getUserInSession().getProfile());
            contentStatus.setStatus(ContentStatusType.NOT_VIEWED);
            return contentStatus;
        }
        return contentStatus;
    }

    public ContentStatus findStatusById(String contentId) throws CapacityException {
        return findStatusById(UUID.fromString(contentId));
    }

    public ContentStatus setStatus(UUID contentId, ContentStatusType status) throws CapacityException {
        ContentStatus contentStatus = findStatusById(contentId);
        if(contentStatus.getStatus() != status) {
            contentStatus.setStatus(status);
            contentStatus.setUpdatedAt(new java.util.Date());
            return contentStatusRepository.save(contentStatus);
        }
        return contentStatus;
    }

    public ContentStatus setStatus(String contentId, ContentStatusType status) throws CapacityException {
        return setStatus(UUID.fromString(contentId), status);
    }

    public void deleteStatus(UUID contentId) {
        contentStatusRepository.deleteByContentId(contentId);
    }

    public ContentStatusType getProfileProgress(Object contentId, Object profileId, Object profileUsername) throws CapacityException {
        Profile profile = ProfileService.getInstance().getProfileByUserIdOrUsername(profileId, profileUsername);
        Content content = findById(contentId);

        ContentStatus status = contentStatusRepository.findByProfileIdAndContentId(profile.getId(), content.getId());

        return status != null
            ? status.getStatus()
            : ContentStatusType.NOT_VIEWED;
    }
}
