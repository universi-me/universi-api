package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.entidades.FolderFavorite;
import me.universi.capacity.entidades.FolderProfile;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.ContentRepository;
import me.universi.capacity.repository.ContentStatusRepository;
import me.universi.capacity.repository.FolderFavoriteRepository;
import me.universi.capacity.repository.FolderProfileRepository;
import me.universi.capacity.repository.FolderRepository;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.RandomUtil;

@Service
public class FolderService {
    private final GroupService groupService;
    private final ProfileService profileService;
    private final CategoryService categoryService;
    private final FolderRepository folderRepository;
    private final FolderProfileRepository folderProfileRepository;
    private final ContentRepository contentRepository;
    private final FolderFavoriteRepository folderFavoriteRepository;
    private final ContentStatusRepository contentStatusRepository;
    private final CompetenceTypeService competenceTypeService;
    private final CompetenceService competenceService;

    public FolderService(GroupService groupService, ProfileService profileService, CategoryService categoryService, FolderRepository folderRepository, FolderProfileRepository folderProfileRepository, ContentRepository contentRepository, FolderFavoriteRepository folderFavoriteRepository, ContentStatusRepository contentStatusRepository, CompetenceTypeService competenceTypeService, CompetenceService competenceService) {
        this.groupService = groupService;
        this.profileService = profileService;
        this.categoryService = categoryService;
        this.folderRepository = folderRepository;
        this.folderProfileRepository = folderProfileRepository;
        this.contentRepository = contentRepository;
        this.folderFavoriteRepository = folderFavoriteRepository;
        this.contentStatusRepository = contentStatusRepository;
        this.competenceTypeService = competenceTypeService;
        this.competenceService = competenceService;
    }

    public static FolderService getInstance() {
        return Sys.context.getBean("folderService", FolderService.class);
    }

    public List<Folder> findAll() {
        return folderRepository.findAll();
    }

    public void checkPermissions(Folder folder, boolean forWrite) throws CapacityException {
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

        Collection<ProfileGroup> userGroups = userSession.getProfile().getGroups();
        Collection<Group> folderGroups = folder.getGrantedAccessGroups();

        for(ProfileGroup userGroupNow : userGroups) {
            for(Group folderGroupNow : folderGroups) {
                if(userGroupNow.group != null && Objects.equals(folderGroupNow.getId(), userGroupNow.group.getId())) {
                    /* User is in a group with access to the folder */
                    return;
                }
            }
        }

        throw new CapacityException("Você não tem permissão para ver essa pasta.");
    }

    public boolean hasPermissions(Folder folder, boolean forWrite) {
        try {
            checkPermissions(folder, forWrite);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Folder findById(UUID folderId) throws CapacityException {
        Folder folder = folderRepository.findFirstById(folderId);
        if(folder == null) {
            throw new CapacityException("Pasta não encontrada.");
        }
        return folder;
    }

    public Folder findById(String folderId) throws CapacityException {
        return findById(UUID.fromString(folderId));
    }

    public Folder findByReference(String folderReference) throws CapacityException {
        Folder folder = folderRepository.findFirstByReference(folderReference);
        if(folder == null) {
            throw new CapacityException("Pasta não encontrada.");
        }
        return folder;
    }

    public Folder findByIdOrReference(Object folderId, Object folderReference) throws CapacityException {
        String id = folderId == null ? null : String.valueOf(folderId);
        String reference = folderReference == null ? null : String.valueOf(folderReference);

        return findByIdOrReference(id, reference);
    }

    public Folder findByIdOrReference(String folderId, String folderReference) throws CapacityException {
        if (folderId == null && folderReference == null)
            throw new CapacityException("Pasta não encontrada");

        return folderId == null
            ? findByReference(folderReference)
            : findByIdOrReference(UUID.fromString(folderId), folderReference);
    }

    public Folder findByIdOrReference(UUID folderId, String folderReference) throws CapacityException {
        Folder folder = folderRepository.findFirstByIdOrReference(folderId, folderReference);
        if(folder == null) {
            throw new CapacityException("Pasta não encontrada.");
        }
        return folder;
    }

    public List<Folder> findByCategory(UUID categoryId) throws CapacityException {
        Category category = categoryService.findById(categoryId);
        return folderRepository.findByCategories(category);
    }

    public List<Folder> findByCategory(String categoryId) throws CapacityException {
        return findByCategory(UUID.fromString(categoryId));
    }

    public boolean saveOrUpdate(Folder folder) throws CapacityException {

        Folder updatedFolder = folderRepository.save(folder);

        if (folderRepository.findFirstById(updatedFolder.getId()) != null){
            return true;
        }

        return false;
    }

    public boolean delete(UUID id){
        Folder folder = findById(id);
        folder.setDeleted(true);
        saveOrUpdate(folder);
        return true;
    }

    public void addOrRemoveContent(Object folderId, Object contentId, boolean isAdding) throws CapacityException {
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
                Content content = ContentService.getInstance().findById(contentIdNow);

                addOrRemoveFromContent(content, folderId, isAdding);
                boolean result = ContentService.getInstance().saveOrUpdate(content);
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
                Category category = categoryService.findById(categoryId);

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

    public void addOrRemoveFromContent(Content content, Object foldersId, boolean isAdding) throws CapacityException {
        Object foldersIds = foldersId;
        if(foldersIds instanceof String) {
            foldersIds = new ArrayList<String>() {{ add((String) foldersId); }};
        }
        if(foldersIds instanceof ArrayList) {
            for(String folderId : (ArrayList<String>)foldersIds) {
                if(folderId==null || folderId.isEmpty()) {
                    continue;
                }
                Folder folder = findById(folderId);

                checkPermissions(folder, true);

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

    public void addOrRemoveGrantedAccessGroup(Folder folder, Object groupsId, boolean isAdding) throws CapacityException, GroupException {
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
                boolean hasAdded = false;
                if(folder.getGrantedAccessGroups() == null) {
                    folder.setGrantedAccessGroups(new ArrayList<>());
                }
                if(isAdding) {
                    if(!folder.getGrantedAccessGroups().contains(group)) {
                        folder.getGrantedAccessGroups().add(group);
                        hasAdded = true;
                    }
                } else {
                    folder.getGrantedAccessGroups().remove(group);
                }
                folderRepository.save(folder);
                if(hasAdded) {
                    groupService.didImportContentToGroup(group, folder);
                }
            }
        }
    }

    public void setPositionOfContent(UUID folderId, UUID contentId, int order) throws CapacityException {
        folderRepository.setPositionOfContentInFolder(folderId, contentId, order);
    }

    public void setPositionOfContent(Object folderId, Object contentId, int order) throws CapacityException {
        folderRepository.setPositionOfContentInFolder(UUID.fromString((String)folderId), UUID.fromString((String)contentId), order);
    }

    public int getPositionOfContent(UUID folderId, UUID contentId) throws CapacityException {
        return folderRepository.getPositionOfContentInFolder(folderId, contentId);
    }

    public int getPositionOfContent(Object folderId, Object contentId) throws CapacityException {
        return folderRepository.getPositionOfContentInFolder(UUID.fromString((String)folderId), UUID.fromString((String)contentId));
    }

    public void setNewPositionOfContent(Object folderId, Object contentId, int toIndex) throws CapacityException {
        Folder folder = findById((String)folderId);

        checkPermissions(folder, true);

        Content content = ContentService.getInstance().findById((String)contentId);

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
            if(getPositionOfContent(folder.getId(), contentNow.getId()) != newOrder) {
                setPositionOfContent(folder.getId(), contentNow.getId(), newOrder);
            }
        }
    }

    public List<Folder> getAssignedTo(UUID profileId) {
        List<FolderProfile> assignedFolders = folderProfileRepository.findByAssignedToId(profileId);

        return assignedFolders.stream()
                .sorted(Comparator.comparing(FolderProfile::getCreated).reversed())
                .map(FolderProfile::getFolder)
                .filter(Objects::nonNull)
                .toList();
    }

    // find profiles assigned to a folder
    public List<Profile> findAssignedProfiles(UUID folderId, UUID assignedBy) {
        List<FolderProfile> assignedProfiles = folderProfileRepository.findByFolderIdAndAssignedById(
            folderId, assignedBy
        );

        return assignedProfiles.stream()
                .sorted(Comparator.comparing(FolderProfile::getCreated).reversed())
                .map(FolderProfile::getAssignedTo)
                .filter(Objects::nonNull)
                .toList();
    }

    // assign one folder to a profile
    public void assignToProfile(UUID profileId, Folder folder) {
        Profile profile = profileService.findFirstById(profileId);
        if(profile == null) {
            throw new CapacityException("Perfil não encontrado.");
        }
        if(folder == null) {
            throw new CapacityException("Pasta não encontrada.");
        }

        var currentProfile = profileService.getProfileInSession();

        var existingFolderProfile = folderProfileRepository.findByFolderIdAndAssignedToIdAndAssignedById(
            folder.getId(), profileId, currentProfile.getId()
        );

        if( existingFolderProfile.isPresent() ) {
            throw new CapacityException("O conteúdo já foi atribuído ao perfil.");
        }

        FolderProfile folderProfile = new FolderProfile();
        folderProfile.setAssignedBy(currentProfile);
        folderProfile.setFolder(folder);
        folderProfile.setAssignedTo(profile);
        folderProfileRepository.save(folderProfile);

        groupService.alertUserForContentAssigned(currentProfile, profile, folder);
    }

    public void assignToMultipleProfiles(Collection<UUID> profileIds, Folder folder) {
        for ( var profileId : profileIds ) {
            assignToProfile(profileId, folder);
        }
    }

    public void unassignFromProfile(UUID profileId, Folder folder) {
        var folderProfile = folderProfileRepository.findByFolderIdAndAssignedToIdAndAssignedById(
            folder.getId(), profileId, profileService.getProfileInSession().getId()
        );

        if ( folderProfile.isPresent() )
            folderProfileRepository.delete( folderProfile.get() );
    }

    public void unassignFromMultipleProfiles(Collection<UUID> profilesIds, Folder folder) {
        for (UUID profileId : profilesIds) {
            unassignFromProfile(profileId, folder);
        }
    }

    public List<FolderProfile> getAssignedBy(Object profileId, Object username) {
        Profile profile = profileService.getProfileByUserIdOrUsername(profileId, username);
        return getAssignedBy(profile);
    }

    public List<FolderProfile> getAssignedBy(Profile profile) {
        UserService userService = UserService.getInstance();

        if (!userService.isUserAdmin(userService.getUserInSession()) && !userService.isSessionOfUser(profile.getUser())) {
            throw new CapacityException("Você não pode acessar os conteúdos atribuídos por outro usuário.");
        }

        return folderProfileRepository.findByAssignedById(profile.getId());
    }

    public void favorite(Folder folder) throws CapacityException {
        favorite(folder.getId());
    }

    public void favorite(UUID folderId) throws CapacityException {
        Folder folder = findById(folderId);

        Profile currentUser = UserService.getInstance().getUserInSession().getProfile();
        FolderFavorite folderFavorite = folderFavoriteRepository
            .findFirstByFolderIdAndProfileId(folder.getId(), currentUser.getId());

        if (folderFavorite != null)
            return;

        if (!hasPermissions(folder, false))
            throw new CapacityException("Essa pasta não existe ou você não pode favoritá-la");

        folderFavorite = new FolderFavorite();
        folderFavorite.setFolder(folder);
        folderFavorite.setProfile(currentUser);

        folderFavoriteRepository.save(folderFavorite);
    }

    public void unfavorite(Folder folder) throws CapacityException {
        unfavorite(folder.getId());
    }

    public void unfavorite(UUID folderId) throws CapacityException {
        Folder folder = findById(folderId);

        Profile currentUser = UserService.getInstance().getUserInSession().getProfile();
        FolderFavorite folderFavorite = folderFavoriteRepository
            .findFirstByFolderIdAndProfileId(folder.getId(), currentUser.getId());

        if (folderFavorite == null)
            return;

        folderFavoriteRepository.delete(folderFavorite);
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
        Folder folder = null;
        String reference = "";

        do {
            reference = RandomUtil.randomString(
                Folder.FOLDER_REFERENCE_SIZE,
                Folder.FOLDER_REFERENCE_AVAILABLE_CHARS
            );

            folder = folderRepository.findFirstByReference(reference);
        } while (folder != null);

        return reference;
    }

    public List<ContentStatus> getStatuses(Profile profile, Folder folder) {
        return folder.getContents().stream()
            .map(c -> contentStatusRepository.findByProfileIdAndContentId(profile.getId(), c.getId()))
            .filter(Objects::nonNull)
            .toList();
    }

    public boolean isComplete(@NotNull Profile profile, @NotNull Folder folder) {
        var statuses = getStatuses(profile, folder);

        return !folder.getContents().isEmpty()
            && statuses.size() == folder.getContents().size()
            && statuses.stream()
                .allMatch(cs -> cs.getStatus() == ContentStatusType.DONE);
    }

    public boolean canCheckProfileProgress(Object profileId, Object profileUsername, Object folderId, Object folderReference) {
        return canCheckProfileProgress(
            ProfileService.getInstance().getProfileByUserIdOrUsername(profileId, profileUsername),
            findByIdOrReference(folderId, folderReference)
        );
    }

    public boolean canCheckProfileProgress(Profile profile, Folder folder) {
        if (profile == null)
            return false;

        UserService userService = UserService.getInstance();

        return userService.isUserAdminSession()
            || userService.isSessionOfUser(profile.getUser())
            || getAssignedBy(userService.getUserInSession().getProfile()) // has assigned that folder to that user
            .stream()
            .filter(fp -> Objects.equals(profile.getId(), fp.getAssignedTo().getId())
                && Objects.equals(folder.getId(), fp.getFolder().getId()))
            .count() > 0;
    }

    public void moveToGroup(Folder folder, Group originalGroup, Group newGroup) {
        if (folder == null)
            throw new CapacityException("O conteúdo que deveria ser movido não foi encontrado");

        if (originalGroup == null)
            throw new CapacityException("O grupo contendo o conteúdo não foi encontrado");

        if (newGroup == null)
            throw new CapacityException("O novo grupo para o conteúdo não foi encontrado");

        RolesService.getInstance().checkPermission(originalGroup, FeaturesTypes.CONTENT, Permission.READ_WRITE_DELETE);
        RolesService.getInstance().checkPermission(newGroup, FeaturesTypes.CONTENT, Permission.READ_WRITE);

        addOrRemoveGrantedAccessGroup(folder, newGroup.getId().toString(), true);
        addOrRemoveGrantedAccessGroup(folder, originalGroup.getId().toString(), false);
    }

    public void addGrantCompetenceBadge(@NotNull Folder folder, @NotNull Collection<UUID> competenceTypesIds) {
        var competenceTypes = competenceTypesIds.stream()
            .map(competenceTypeService::findFirstById)
            .filter(ct -> ct != null && !folder.getGrantsBadgeToCompetences().contains(ct))
            .toList();

        folder.getGrantsBadgeToCompetences().addAll(competenceTypes);
        saveOrUpdate(folder);

        grantCompetenceBadge(folder, profileService.findAll());
    }

    public void removeGrantCompetenceBadge(@NotNull Folder folder, @NotNull Collection<UUID> competenceTypesIds) {
        folder.getGrantsBadgeToCompetences()
            .removeIf(ct -> competenceTypesIds.contains(ct.getId()));

        saveOrUpdate(folder);
    }

    public void grantCompetenceBadge(@NotNull Collection<Folder> folder, @NotNull Collection<@NotNull Profile> profile) {
        for (var f : folder)
            for (var p : profile)
                grantCompetenceBadgeToProfile(f, p);

        profileService.saveAll(profile);
    }

    public void grantCompetenceBadge(@NotNull Folder folder, @NotNull Collection<@NotNull Profile> profile) {
        for (var p : profile) grantCompetenceBadgeToProfile(folder, p);

        profileService.saveAll(profile);
    }

    public void grantCompetenceBadge(@NotNull Collection<Folder> folder, @NotNull Profile profile) {
        for (var f : folder) grantCompetenceBadgeToProfile(f, profile);

        profileService.save(profile);
    }

    public void grantCompetenceBadge(@NotNull Folder folder, @NotNull Profile profile) {
        grantCompetenceBadgeToProfile(folder, profile);
        profileService.save(profile);
    }

    private void grantCompetenceBadgeToProfile(@NotNull Folder folder, @NotNull Profile profile) {
        if (!isComplete(profile, folder)) return;

        for (var competenceType : folder.getGrantsBadgeToCompetences()) {
            if (!profile.hasBadge(competenceType))
                profile.getCompetenceBadges().add(competenceType);

            if (!competenceService.profileHasCompetence(profile, competenceType)) {
                competenceService.create(competenceType, "", 0, profile);
            }

            if (!competenceTypeService.hasAccessToCompetenceType(competenceType, profile)) {
                competenceTypeService.grantAccessToProfile(competenceType, profile);
            }
        }
    }
}
