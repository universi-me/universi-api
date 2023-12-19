package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import me.universi.Sys;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.entidades.FolderProfile;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.CategoryRepository;
import me.universi.capacity.repository.ContentRepository;
import me.universi.capacity.repository.FolderProfileRepository;
import me.universi.capacity.repository.FolderRepository;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;


@Service
public class CapacityService {
    
    private final ContentRepository contentRepository;

    private final CategoryRepository categoryRepository;

    private final FolderRepository folderRepository;

    private final GroupService groupService;

    private final ProfileService profileService;

    private final FolderProfileRepository folderProfileRepository;

    public CapacityService(GroupService groupService, ContentRepository contentRepository, CategoryRepository categoryRepository, FolderRepository folderRepository, ProfileService profileService, FolderProfileRepository folderProfileRepository) {
        this.contentRepository = contentRepository;
        this.categoryRepository = categoryRepository;
        this.folderRepository = folderRepository;
        this.groupService = groupService;
        this.profileService = profileService;
        this.folderProfileRepository = folderProfileRepository;
    }

    public static CapacityService getInstance() {
        return Sys.context.getBean("capacityService", CapacityService.class);
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

    public boolean deleteCategory(UUID id){
        categoryRepository.deleteById(id);
        return true;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
