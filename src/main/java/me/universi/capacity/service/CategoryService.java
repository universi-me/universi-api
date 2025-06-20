package me.universi.capacity.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.api.interfaces.UniqueNameEntityService;
import me.universi.capacity.dto.CreateCategoryDTO;
import me.universi.capacity.dto.UpdateCategoryDTO;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.repository.CategoryRepository;
import me.universi.image.services.ImageMetadataService;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

@Service
public class CategoryService extends UniqueNameEntityService<Category> {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final UserService userService;
    private final ImageMetadataService imageMetadataService;

    public CategoryService(CategoryRepository categoryRepository, ProfileService profileService, UserService userService, ImageMetadataService imageMetadataService) {
        this.categoryRepository = categoryRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.imageMetadataService = imageMetadataService;

        this.entityName = "Categoria";
    }

    public static CategoryService getInstance() {
        return Sys.context.getBean("categoryService", CategoryService.class);
    }

    @Override
    protected Optional<Category> findUnchecked( UUID id ) {
        return categoryRepository.findById( id );
    }

    public List<Category> findOrThrow( Collection<UUID> ids ) {
        return ids.stream().map( this::findOrThrow ).toList();
    }

    @Override
    protected Optional<Category> findByNameUnchecked( String name ) {
        return categoryRepository.findFirstByNameIgnoreCase( name );
    }

    @Override
    protected Optional<Category> findByIdOrNameUnchecked( String idOrName ) {
        return categoryRepository.findFirstByIdOrNameIgnoreCase(
            CastingUtil.getUUID( idOrName ).orElse( null ),
            idOrName
        );
    }

    public @NotNull Category create( @NotNull CreateCategoryDTO createCategoryDTO ) {
        checkNameAvailable( createCategoryDTO.name() );

        var category = new Category();
        category.setName( createCategoryDTO.name() );
        category.setAuthor( profileService.getProfileInSessionOrThrow() );

        if ( createCategoryDTO.image() != null ) {
            category.setImage( imageMetadataService.findOrThrow( createCategoryDTO.image() ) );
        }

        return categoryRepository.saveAndFlush( category );
    }

    public @NotNull Category update( @NotNull UUID id, @NotNull UpdateCategoryDTO updateCategoryDTO ) {
        var category = findOrThrow( id );
        checkPermissionToEdit( category );

        if ( updateCategoryDTO.name() != null && !updateCategoryDTO.name().isBlank() ) {
            checkNameAvailable( updateCategoryDTO.name() );
            category.setName( updateCategoryDTO.name() );
        }

        if ( updateCategoryDTO.image() != null ) {
            category.setImage( imageMetadataService.findOrThrow( updateCategoryDTO.image() ) );
        }

        return categoryRepository.saveAndFlush( category );
    }

    public void delete( UUID id ) {
        Category category = findOrThrow( id );
        checkPermissionToDelete( category );

        category.setDeleted( true );
        categoryRepository.saveAndFlush( category );
    }

    @Override
    protected List<Category> findAllUnchecked() {
        return categoryRepository.findAll();
    }

    @Override
    public boolean hasPermissionToEdit( Category category ) {
        return profileService.isSessionOfProfile( category.getAuthor() )
            || userService.isUserAdminSession();
    }

    @Override
    public boolean hasPermissionToDelete( Category category ) {
        return hasPermissionToEdit( category );
    }
}
