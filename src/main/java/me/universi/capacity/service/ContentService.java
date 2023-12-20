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
import me.universi.capacity.enums.ContentType;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.ContentRepository;
import me.universi.capacity.repository.ContentStatusRepository;
import me.universi.profile.entities.Profile;
import me.universi.user.entities.User;
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

    public boolean handleCreate(Object title, Object url, Object image, Object description, Object rating, Object type, Object addCategoriesByIds, Object addFoldersByIds) throws CapacityException {
        Content content = new Content();
        content.setAuthor(UserService.getInstance().getUserInSession().getProfile());

        if (title == null || String.valueOf(title).isEmpty()) {
            throw new CapacityException("Título do conteúdo não informado.");
        }
        content.setTitle(String.valueOf(title));

        if(url == null || String.valueOf(url).isEmpty()) {
            throw new CapacityException("URL do conteúdo não informado.");
        }
        content.setUrl(String.valueOf(url));

        if(type == null || String.valueOf(type).isEmpty()) {
            throw new CapacityException("Tipo do conteúdo não informado.");
        }

        try {
            ContentType typeValue = ContentType.valueOf(String.valueOf(type));
            content.setType(typeValue);
        } catch (Exception e) {
            // todo: add available types on the message
            throw new CapacityException("Tipo do conteúdo não suportado.");
        }

        if(image != null) {
            String imageStr = String.valueOf(image);
            if(!imageStr.isEmpty())
                content.setImage(imageStr);
        }

        if(description != null) {
            String descriptionStr = String.valueOf(description);
            if(!descriptionStr.isEmpty())
                content.setDescription(descriptionStr);
        }

        if(rating != null) {
            String ratingStr = String.valueOf(rating);
            if(!ratingStr.isEmpty())
                content.setRating(Integer.parseInt(ratingStr));
        }

        if(addCategoriesByIds != null) {
            folderService.addOrRemoveCategoriesFromContentOrFolder(content, addCategoriesByIds, true, false);
        }

        if(addFoldersByIds != null) {
            folderService.addOrRemoveFromContent(content, addFoldersByIds, true);
        }

        return saveOrUpdate(content);
    }

    public boolean handleEdit(Object id, Object url, Object title, Object image, Object description, Object rating, Object type, Object addCategoriesByIds, Object removeCategoriesByIds, Object addFoldersByIds, Object removeFoldersByIds) throws CapacityException {
        if(id == null || String.valueOf(id).isEmpty()) {
            throw new CapacityException("ID do conteúdo não informado.");
        }

        Content content = findById(String.valueOf(id));
        if(content == null)
            throw new CapacityException("Conteúdo não encontrado.");

        if (!hasWritePermission(content)) {
            throw new CapacityException("Você não tem permissão para editar este conteúdo.");
        }

        if(url != null) {
            String urlStr = String.valueOf(url);
            if(!urlStr.isEmpty())
                content.setUrl(urlStr);
        }

        if(title != null) {
            String titleStr = String.valueOf(title);
            if(!titleStr.isEmpty())
                content.setTitle(titleStr);
        }

        if(image != null) {
            String imageStr = String.valueOf(image);
            if(!imageStr.isEmpty())
                content.setImage(imageStr);
        }

        if(description != null) {
            String descriptionStr = String.valueOf(description);
            if(!descriptionStr.isEmpty())
                content.setDescription(descriptionStr);
        }

        if(rating != null) {
            String ratingStr = String.valueOf(rating);
            if(!ratingStr.isEmpty())
                content.setRating(Integer.parseInt(ratingStr));
        }

        if(type != null) {
            try {
                ContentType typeValue = ContentType.valueOf(String.valueOf(type));
                content.setType(typeValue);
            } catch (Exception e) {
                // todo: add available types on the message
                throw new CapacityException("Tipo do conteúdo não suportado.");
            }
        }

        if(addCategoriesByIds != null) {
            folderService.addOrRemoveCategoriesFromContentOrFolder(content, addCategoriesByIds, true, false);
        }
        if(removeCategoriesByIds != null) {
            folderService.addOrRemoveCategoriesFromContentOrFolder(content, removeCategoriesByIds, false, false);
        }

        if(addFoldersByIds != null) {
            folderService.addOrRemoveFromContent(content, addFoldersByIds, true);
        }
        if(removeFoldersByIds != null) {
            folderService.addOrRemoveFromContent(content, removeFoldersByIds, false);
        }

        return saveOrUpdate(content);
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

        contentRepository.deleteById(id);

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

    /**
     * Checks if the current logged user has write access to the content.
     */
    public boolean hasWritePermission(Content content) {
        return hasWritePermission(content, UserService.getInstance().getUserInSession());
    }
    /**
     * Checks if `user` has write access to the content.
     */
    public boolean hasWritePermission(Content content, User user) {
        if (user == null)
            return false;

        UserService userService = UserService.getInstance();
        User author = content.getAuthor().getUser();

        return userService.isSessionOfUser(author)
            || userService.isUserAdmin(user);
    }
}
