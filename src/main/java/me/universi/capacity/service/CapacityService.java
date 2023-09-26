package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

import me.universi.Sys;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.repository.CategoryRepository;
import me.universi.capacity.repository.FolderRepository;
import me.universi.capacity.repository.ContentStatusRepository;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.ContentRepository;
import me.universi.capacity.enums.ContentStatusType;


@Service
public class CapacityService implements CapacityServiceInterface {
    
    private final ContentRepository contentRepository;

    private final CategoryRepository categoryRepository;

    private final FolderRepository folderRepository;

    private final ContentStatusRepository contentStatusRepository;

    private final GroupService groupService;

    public CapacityService(GroupService groupService, ContentRepository contentRepository, CategoryRepository categoryRepository, FolderRepository folderRepository, ContentStatusRepository contentStatusRepository) {
        this.contentRepository = contentRepository;
        this.categoryRepository = categoryRepository;
        this.folderRepository = folderRepository;
        this.groupService = groupService;
        this.contentStatusRepository = contentStatusRepository;
    }

    public static CapacityService getInstance() {
        return Sys.context.getBean("capacityService", CapacityService.class);
    }

    public void checkFolderPermissions(Folder folder, boolean forWrite) throws CapacityException {
        if(folder == null) {
            /* Folder doesn't exist */
            throw new CapacityException("Pasta não encontrada.");
        }

        if (UserService.getInstance().isSessionOfUser(folder.getAuthor().getUser())) {
            /* Folder author always have access */
            return;
        }

        User userSession = UserService.getInstance().getUserInSession();

        if (UserService.getInstance().isUserAdmin(userSession)) {
            /* Admins always have access */
            return;
        }

        if(forWrite) {
            /* Only admin and author have write access */
            throw new CapacityException("Você não tem permissão para alterar essa pasta.");
        }

        if (folder.isPublicFolder()) {
            /* Everyone has reading access to public folder */
            return;
        }

        Collection<Group> userGroups = userSession.getProfile().getGroups();
        Collection<Group> folderGroups = folder.getGrantedAccessGroups();
        UUID folderOwnerId = folder.getOwnerGroup().getId();

        for(Group userGroupNow : userGroups) {
            if (Objects.equals(folderOwnerId, userGroupNow.getId())) {
                /* User is in the owner group of the folder */
                return;
            }

            for(Group folderGroupNow : folderGroups) {
                if(Objects.equals(folderGroupNow.getId(), userGroupNow.getId())) {
                    /* User is in a group with access to the folder */
                    return;
                }
            }
        }

        throw new CapacityException("Você não tem permissão para ver essa pasta.");
    }

    public boolean hasFolderPermissions(Folder folder, boolean forWrite) {
        try {
            checkFolderPermissions(folder, forWrite);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Content> getAllContents(){
        List<Content> contentList = new ArrayList<>();
        contentRepository.findAll().forEach(content -> contentList.add(content));

        return contentList;
    } //Lista todos os conteúdos existentes

    public Content findContentById(UUID contentId) throws CapacityException {
        Content content = contentRepository.findFirstById(contentId);
        if(content == null) {
            throw new CapacityException("Conteúdo não encontrada.");
        }
        return content;
    }

    public Content findContentById(String contentId) throws CapacityException {
        return findContentById(UUID.fromString(contentId));
    }

    public Folder findFolderById(UUID folderId) throws CapacityException {
        Folder folder = folderRepository.findFirstById(folderId);
        if(folder == null) {
            throw new CapacityException("Pasta não encontrada.");
        }
        return folder;
    }

    public Folder findFolderById(String folderId) throws CapacityException {
        return findFolderById(UUID.fromString(folderId));
    }

    public Category findCategoryById(UUID uuid) throws CapacityException {
        Category category = categoryRepository.findFirstById(uuid);
        if(category == null) {
            throw new CapacityException("Categoria não encontrada.");
        }
        return category;
    }

    public Category findCategoryById(String uuid) throws CapacityException {
        return findCategoryById(UUID.fromString(uuid));
    }

    public List<Content> findContentsByCategory(UUID categoryId) throws CapacityException {
        Category category = findCategoryById(categoryId);
        return contentRepository.findByCategories(category);
    }

    public List<Content> findContentsByCategory(String categoryId) throws CapacityException {
        return findContentsByCategory(UUID.fromString(categoryId));
    }

    public List<Content> findContentsByFolder(UUID folderId) throws CapacityException {
        Folder folder = findFolderById(folderId);

        List<Content> contents = contentRepository.findContentsInFolderByOrderPosition(folder.getId());

        for(Content content : contents) {
            ContentStatus contentStatus = findStatusByContentId(content.getId());
            if(contentStatus != null) {
                content.contentStatus = contentStatus;
            }
        }

        return contents;
    }

    public Collection<Content> findContentsByFolder(String folderId) throws CapacityException {
        return findContentsByFolder(UUID.fromString(folderId));
    }

    public Collection<Folder> findFoldersByCategory(UUID categoryId) throws CapacityException {
        Category category = findCategoryById(categoryId);
        return folderRepository.findByCategories(category);
    }

    public Collection<Folder> findFoldersByCategory(String categoryId) throws CapacityException {
        return findFoldersByCategory(UUID.fromString(categoryId));
    }

    public boolean saveOrUpdateContent(Content content) throws CapacityException {

        Content updatedContent = contentRepository.save(content);

        if (findContentById(updatedContent.getId()) != null){
            return true;
        }

        return false;
    }

    public boolean saveOrUpdateCategory(Category category) throws CapacityException {
        boolean titleExists = categoryRepository.existsByName(category.getName());
        if (titleExists) {
            throw new CapacityException("Categoria com título já existente.");
        }

        Category updatedCategory = categoryRepository.save(category);

        if (categoryRepository.findFirstById(updatedCategory.getId()) != null){
            return true;
        }

        return false;
    }

    public boolean saveOrUpdateFolder(Folder folder) throws CapacityException {

        Folder updatedFolder = folderRepository.save(folder);

        if (folderRepository.findFirstById(updatedFolder.getId()) != null){
            return true;
        }

        return false;
    }

    public boolean deleteContent(UUID id) throws CapacityException {

        Content content = findContentById(id);

        // remove from linked folders
        content.getFolders().forEach(folder -> {
            folder.getContents().remove(content);
            folderRepository.save(folder);
        });

        // remove from linked watch`s
        deleteStatusForContent(content.getId());

        contentRepository.deleteById(id);

        return true;
    }

    public boolean deleteCategory(UUID id){
        categoryRepository.deleteById(id);
        return true;
    }

    public boolean deleteFolder(UUID id){
        folderRepository.deleteById(id);
        return true;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }

    public void addOrRemoveContentFromFolder(Object folderId, Object contentId, boolean isAdding) throws CapacityException {
        if(folderId == null) {
            throw new CapacityException("Parametro folderId é nulo.");
        }
        if(contentId == null) {
            throw new CapacityException("Parametro contentId é nulo.");
        }

        Object contentIds = contentId;
        if(contentIds instanceof String) {
            contentIds = new ArrayList<String>() {{ add((String) contentId); }};
        }
        if(contentIds instanceof ArrayList) {
            for (String contentIdNow : (ArrayList<String>) contentIds) {
                if (contentIdNow == null || contentIdNow.isEmpty()) {
                    continue;
                }
                Content content = findContentById(contentIdNow);

                addOrRemoveFoldersFromContent(content, folderId, isAdding);
                boolean result = saveOrUpdateContent(content);
                if (!result) {
                    throw new CapacityException("Erro ao adicionar conteúdo a pasta.");
                }
            }
        }
    }

    public void addOrRemoveCategoriesFromContentOrFolder(Object contentOrFolder, Object categoriesId, boolean isAdding, boolean removeAllBefore) throws CapacityException {
        Object categoriesIds = categoriesId;
        if(categoriesId instanceof String) {
            categoriesIds = new ArrayList<String>() {{ add((String) categoriesId); }};
        }
        if(categoriesIds instanceof ArrayList) {
            for(String categoryId : (ArrayList<String>) categoriesIds) {
                if(categoryId==null || categoryId.isEmpty()) {
                    continue;
                }
                Category category = findCategoryById(categoryId);

                if(contentOrFolder instanceof Content) {
                    if(((Content)contentOrFolder).getCategories() == null) {
                        ((Content)contentOrFolder).setCategories(new ArrayList<>());
                    }
                    if(removeAllBefore) {
                        ((Content)contentOrFolder).getCategories().clear();
                    }
                    if (isAdding) {
                        if (!((Content)contentOrFolder).getCategories().contains(category)) {
                            ((Content)contentOrFolder).getCategories().add(category);
                        }
                    } else {
                        ((Content)contentOrFolder).getCategories().remove(category);
                    }
                } else if(contentOrFolder instanceof Folder) {
                    if(((Folder)contentOrFolder).getCategories() == null) {
                        ((Folder)contentOrFolder).setCategories(new ArrayList<>());
                    }
                    if(removeAllBefore) {
                        ((Folder)contentOrFolder).getCategories().clear();
                    }
                    if (isAdding) {
                        if (!((Folder)contentOrFolder).getCategories().contains(category)) {
                            ((Folder)contentOrFolder).getCategories().add(category);
                        }
                    } else {
                        ((Folder)contentOrFolder).getCategories().remove(category);
                    }
                }
            }
        }
    }

    public void addOrRemoveFoldersFromContent(Content content, Object foldersId, boolean isAdding) throws CapacityException {
        Object foldersIds = foldersId;
        if(foldersIds instanceof String) {
            foldersIds = new ArrayList<String>() {{ add((String) foldersId); }};
        }
        if(foldersIds instanceof ArrayList) {
            for(String folderId : (ArrayList<String>)foldersIds) {
                if(folderId==null || folderId.isEmpty()) {
                    continue;
                }
                Folder folder = findFolderById(folderId);

                checkFolderPermissions(folder, true);

                if(folder.getContents() == null) {
                    folder.setContents(new ArrayList<>());
                }
                if(isAdding) {
                    if(!folder.getContents().contains(content)) {
                        folder.getContents().add(content);
                    }
                } else {
                    folder.getContents().remove(content);
                }
            }
        }
    }

    public void addOrRemoveGrantedAccessGroupFromFolder(Folder folder, Object groupsId, boolean isAdding) throws CapacityException, GroupException {
        Object groupById = groupsId;
        if(groupById instanceof String) {
            groupById = new ArrayList<String>() {{ add((String) groupsId); }};
        }
        if(groupById instanceof ArrayList) {
            for(String addGrantedAccessGroupId : (ArrayList<String>)groupById) {
                if(addGrantedAccessGroupId==null || addGrantedAccessGroupId.isEmpty()) {
                    continue;
                }
                Group group = groupService.getGroupByGroupIdOrGroupPath(addGrantedAccessGroupId, null);
                if(group == null) {
                    continue;
                }
                if(folder.getGrantedAccessGroups() == null) {
                    folder.setGrantedAccessGroups(new ArrayList<>());
                }
                if(isAdding) {
                    if(!folder.getGrantedAccessGroups().contains(group)) {
                        folder.getGrantedAccessGroups().add(group);
                    }
                } else {
                    folder.getGrantedAccessGroups().remove(group);
                }
            }
        }
    }

    public void setPositionOfContentInFolder(UUID folderId, UUID contentId, int order) throws CapacityException {
        folderRepository.setPositionOfContentInFolder(folderId, contentId, order);
    }

    public void setPositionOfContentInFolder(Object folderId, Object contentId, int order) throws CapacityException {
        folderRepository.setPositionOfContentInFolder(UUID.fromString((String)folderId), UUID.fromString((String)contentId), order);
    }

    public int getPositionOfContentInFolder(UUID folderId, UUID contentId) throws CapacityException {
        return folderRepository.getPositionOfContentInFolder(folderId, contentId);
    }

    public int getPositionOfContentInFolder(Object folderId, Object contentId) throws CapacityException {
        return folderRepository.getPositionOfContentInFolder(UUID.fromString((String)folderId), UUID.fromString((String)contentId));
    }

    public void setNewPositionOfContentInFolder(Object folderId, Object contentId, int toIndex) throws CapacityException {
        Folder folder = findFolderById((String)folderId);

        checkFolderPermissions(folder, true);

        Content content = findContentById((String)contentId);

        // mount ordered list
        List<Content> contentsOrdered = new ArrayList<>();
        for(Content contentNow : contentRepository.findContentsInFolderByOrderPosition(folder.getId())) {
            if(!Objects.equals(contentNow.getId(), content.getId())) {
                contentsOrdered.add(contentNow);
            }
        }
        // set in toIndex to move
        contentsOrdered.add(toIndex, content);
        // update order in db
        for(Content contentNow : contentsOrdered) {
            int newOrder = contentsOrdered.indexOf(contentNow);
            if(getPositionOfContentInFolder(folder.getId(), contentNow.getId()) != newOrder) {
                setPositionOfContentInFolder(folder.getId(), contentNow.getId(), newOrder);
            }
        }

    }

    public ContentStatus findStatusByContentId(UUID contentId) throws CapacityException {
        Profile userProfile = UserService.getInstance().getUserInSession().getProfile();
        ContentStatus contentStatus = contentStatusRepository.findFirstByProfileIdAndContentId(userProfile.getId(), contentId);
        if(contentStatus == null) {
            contentStatus = new ContentStatus();
            contentStatus.setContent(findContentById(contentId));
            contentStatus.setProfile(UserService.getInstance().getUserInSession().getProfile());
            contentStatus.setStatus(ContentStatusType.NOT_VIEWED);
            return contentStatus;
        }
        return contentStatus;
    }

    public ContentStatus findStatusByContentId(String contentId) throws CapacityException {
        return findStatusByContentId(UUID.fromString(contentId));
    }

    public ContentStatus setContentStatus(UUID contentId, ContentStatusType status) throws CapacityException {
        ContentStatus contentStatus = findStatusByContentId(contentId);
        if(contentStatus.getStatus() != status) {
            contentStatus.setStatus(status);
            contentStatus.setUpdatedAt(new java.util.Date());
            return contentStatusRepository.save(contentStatus);
        }
        return contentStatus;
    }

    public ContentStatus setContentStatus(String contentId, ContentStatusType status) throws CapacityException {
        return setContentStatus(UUID.fromString(contentId), status);
    }

    public void deleteStatusForContent(UUID contentId) {
        contentStatusRepository.deleteByContentId(contentId);
    }
}