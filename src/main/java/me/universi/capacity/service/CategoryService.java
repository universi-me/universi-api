package me.universi.capacity.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.capacity.dto.CreateCategoryDTO;
import me.universi.capacity.dto.UpdateCategoryDTO;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.CategoryRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, ProfileService profileService, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.profileService = profileService;
        this.userService = userService;
    }

    public static CategoryService getInstance() {
        return Sys.context.getBean("categoryService", CategoryService.class);
    }

    public Optional<Category> find( UUID id ) {
        return categoryRepository.findById( id );
    }

    public List<Optional<Category>> find( Collection<UUID> id ) {
        return id.stream().map( this::find ).toList();
    }

    public List<Category> findOrThrow( Collection<UUID> id ) {
        return id.stream().map( this::findOrThrow ).toList();
    }

    public Category findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Categoria de ID '" + id + "' não encontrada" ) );
    }

    public @NotNull Category create( @NotNull CreateCategoryDTO createCategoryDTO ) {
        var category = new Category();

        category.setName( createCategoryDTO.name() );

        if ( createCategoryDTO.image() != null && !createCategoryDTO.image().isBlank() ) {
            category.setImage( createCategoryDTO.image() );
        }

        category.setAuthor( profileService.getProfileInSession() );
        return saveOrUpdate( category );
    }

    public @NotNull Category update( @NotNull UUID id, @NotNull UpdateCategoryDTO updateCategoryDTO ) throws AccessDeniedException {
        var category = findOrThrow( id );
        canEditOrThrow( category, profileService.getProfileInSession() );

        if ( updateCategoryDTO.name() != null && !updateCategoryDTO.name().isBlank() ) {
            category.setName( updateCategoryDTO.name() );
        }

        if ( updateCategoryDTO.image() != null && !updateCategoryDTO.image().isBlank() ) {
            category.setImage( updateCategoryDTO.image() );
        }

        return saveOrUpdate( category );
    }

    private Category saveOrUpdate(Category category) throws CapacityException {
        boolean titleExists = categoryRepository.existsByName(category.getName());
        if (titleExists) {
            throw new CapacityException("Categoria com título já existente.");
        }

        return categoryRepository.save(category);
    }

    public void delete( UUID id ) {
        Category category = findOrThrow( id );
        canEditOrThrow( category, profileService.getProfileInSession() );

        categoryRepository.delete( category );
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public boolean canEdit( @NotNull Category category, @NotNull Profile profile ) {
        return profile.getId().equals( category.getAuthor().getId() )
            || userService.isUserAdmin( profile.getUser() );
    }

    public void canEditOrThrow( @NotNull Category category, @NotNull Profile profile ) {
        if ( !canEdit(category, profile) )
            throw new AccessDeniedException( "Você não tem permissão para editar esta categoria" );
    }
}
