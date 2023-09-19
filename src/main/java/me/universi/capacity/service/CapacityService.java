package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.repository.CategoryRepository;
import me.universi.capacity.repository.FolderRepository;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.ContentRepository;


@Service
public class CapacityService implements CapacityServiceInterface {
    
    private final ContentRepository contentRepository;

    private final CategoryRepository categoryRepository;

    private final FolderRepository folderRepository;

    private final GroupService groupService;

    public CapacityService(GroupService groupService, ContentRepository contentRepository, CategoryRepository categoryRepository, FolderRepository folderRepository) {
        this.contentRepository = contentRepository;
        this.categoryRepository = categoryRepository;
        this.folderRepository = folderRepository;
        this.groupService = groupService;
    }

    public void checkFolderPermissions(Folder folder, boolean forWrite) throws CapacityException {
        if(folder == null) {
            throw new CapacityException("Pasta não encontrada.");
        }
        if(!UserService.getInstance().isSessionOfUser(folder.getAuthor().getUser())) {
            if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                if(forWrite) {
                    throw new CapacityException("Você não tem permissão para alterar essa pasta.");
                } else {
                    if(!folder.isPublicFolder()) {
                        Profile userProfile = UserService.getInstance().getUserInSession().getProfile();
                        Collection<Group> userGroups = userProfile.getGroups();
                        Collection<Group> folderGroups = folder.getGrantedAccessGroups();
                        boolean hasPermission = false;
                        for(Group folderGroupNow : folderGroups) {
                            for(Group userGroupNow : userGroups) {
                                if(Objects.equals(folderGroupNow.getId(), userGroupNow.getId())) {
                                    hasPermission = true;
                                    break;
                                }
                            }
                        }
                        if(!hasPermission) {
                            throw new CapacityException("Você não tem permissão para ver essa pasta.");
                        }
                    }
                }
            }
        }
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
    } //Lista todos os vídeos existentes


    public Content findFirstById(UUID id){
        return contentRepository.findFirstById(id);
    } //Lista os vídeos pelo ID

    public Content findFirstById(String id){
        return contentRepository.findFirstById(UUID.fromString(id));
    }

    public boolean saveOrUpdateContent(Content content) throws CapacityException {

        Content updatedContent = contentRepository.save(content);

        if (findFirstById(updatedContent.getId()) != null){
            return true;
        }

        return false;
    } //Salvar o vídeo, ou se o ID já existir atualizar um vídeo

    public boolean deleteContent(UUID id){
        contentRepository.deleteById(id);

        if (contentRepository.findById(id) != null){
            return true;
        }

        return false;
    } //Deleta o vídeo pelo ID

    public boolean deleteCategory(UUID id){
        categoryRepository.deleteById(id);

        if (categoryRepository.findById(id) != null){
            return true;
        }

        return false;
    }

    public boolean deleteFolder(UUID id){
        folderRepository.deleteById(id);

        if (folderRepository.findById(id) != null){
            return true;
        }

        return false;
    }

    public List<Content> getContentsByCategory(UUID categoryId) throws CapacityException {
        Category category = categoryRepository.findFirstById(categoryId);
        if(category == null) {
            throw new CapacityException("Categoria não encontrada.");
        }
        return contentRepository.findByCategories(category);
    }

    public List<Content> getContentsByCategory(String categoryId) throws CapacityException {
        return getContentsByCategory(UUID.fromString(categoryId));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Collection<Content> getContentsByFolder(UUID folderId) throws CapacityException {
        Folder folder = folderRepository.findFirstById(folderId);
        if(folder == null) {
            throw new CapacityException("Pasta não encontrada.");
        }
        return folder.getContents();
    }

    public Collection<Content> getContentsByFolder(String folderId) throws CapacityException {
        return getContentsByFolder(UUID.fromString(folderId));
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

        Folder updatedVideoCategory = folderRepository.save(folder);

        if (folderRepository.findFirstById(updatedVideoCategory.getId()) != null){
            return true;
        }

        return false;
    }

    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }

    public Folder findFirstFolderById(String folderId) {
        return folderRepository.findFirstById(UUID.fromString(folderId));
    }

    public void addOrRemoveContentFromFolder(Object folderId, Object contentId, boolean isAdding) throws CapacityException {
        if(folderId == null) {
            throw new CapacityException("Parametro folderId é nulo.");
        }
        if(contentId == null) {
            throw new CapacityException("Parametro contentId é nulo.");
        }

        Object videoIds = contentId;
        if(videoIds instanceof String) {
            videoIds = new ArrayList<String>() {{ add((String) contentId); }};
        }
        if(videoIds instanceof ArrayList) {
            for (String videoIdNow : (ArrayList<String>) videoIds) {
                if (videoIdNow == null || videoIdNow.isEmpty()) {
                    continue;
                }
                Content content = findFirstById(videoIdNow);
                if (content == null) {
                    throw new CapacityException("Conteúdo não encontrado.");
                }
                addOrRemoveFoldersFromContent(content, folderId, isAdding);
                boolean result = saveOrUpdateContent(content);
                if (!result) {
                    throw new CapacityException("Erro ao adicionar conteúdo a pasta.");
                }
            }
        }
    }

    public Category getCategoryById(UUID uuid) throws CapacityException {
        Category category = categoryRepository.findFirstById(uuid);
        if(category == null) {
            throw new CapacityException("Categoria não encontrada.");
        }
        return category;
    }

    public Category getCategoryById(String uuid) throws CapacityException {
        return getCategoryById(UUID.fromString(uuid));
    }

    public Folder getFolderById(UUID uuid) throws CapacityException {
        Folder folder = folderRepository.findFirstById(uuid);
        if(folder == null) {
            throw new CapacityException("Playlist não encontrada.");
        }
        return folder;
    }

    public Folder getFolderById(String uuid) throws CapacityException {
        return getFolderById(UUID.fromString(uuid));
    }

    public Collection<Folder> getFoldersByCategory(UUID categoryId) throws CapacityException {
        Category category = categoryRepository.findFirstById(categoryId);
        if(category == null) {
            throw new CapacityException("Categoria não encontrada.");
        }
        return folderRepository.findByCategories(category);
    }

    public Collection<Folder> getFoldersByCategory(String categoryId) throws CapacityException {
        return getFoldersByCategory(UUID.fromString(categoryId));
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
                Category category = getCategoryById(categoryId);
                if(category == null) {
                    throw new CapacityException("Categoria não encontrada.");
                }
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
        Object playlistsIds = foldersId;
        if(playlistsIds instanceof String) {
            playlistsIds = new ArrayList<String>() {{ add((String) foldersId); }};
        }
        if(playlistsIds instanceof ArrayList) {
            for(String playlistId : (ArrayList<String>)playlistsIds) {
                if(playlistId==null || playlistId.isEmpty()) {
                    continue;
                }
                Folder folder = getFolderById(playlistId);
                if(folder == null) {
                    throw new CapacityException("Pasta não encontrada.");
                }

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

}