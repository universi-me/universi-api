package me.universi.experience.services;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import me.universi.api.interfaces.UniqueNameEntityService;
import me.universi.experience.dto.CreateExperienceTypeDTO;
import me.universi.experience.dto.UpdateExperienceTypeDTO;
import me.universi.experience.entities.ExperienceType;
import me.universi.experience.repositories.ExperienceTypeRepository;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExperienceTypeService extends UniqueNameEntityService<ExperienceType> {
    private final ExperienceTypeRepository experienceTypeRepository;
    private final UserService userService;

    public ExperienceTypeService(ExperienceTypeRepository typeExperienceRepository, UserService userService){
        this.experienceTypeRepository = typeExperienceRepository;
        this.userService = userService;

        this.entityName = "Tipo de Experiência";
    }

    @Override
    public List<ExperienceType> findAllUnchecked(){
        return experienceTypeRepository.findAll();
    }

    @Override
    public Optional<ExperienceType> findUnchecked( UUID id ) {
        return experienceTypeRepository.findById(id);
    }

    @Override
    public Optional<ExperienceType> findByNameUnchecked( String name ) {
        return experienceTypeRepository.findFirstByNameIgnoreCase( name );
    }

    @Override
    public Optional<ExperienceType> findByIdOrNameUnchecked( String idOrName ) {
        return experienceTypeRepository.findFirstByIdOrNameIgnoreCase(
            CastingUtil.getUUID( idOrName ).orElse( null ),
            idOrName
        );
    }

    public ExperienceType update( @NonNull String idOrName, @NonNull UpdateExperienceTypeDTO dto ) throws AccessDeniedException {
        var experienceType = findByIdOrNameOrThrow( idOrName );
        checkPermissionToEdit( experienceType );

        if ( dto.name() != null && !dto.name().isBlank() )
            experienceType.setName( dto.name() );

        return experienceTypeRepository.saveAndFlush( experienceType );
    }

    public ExperienceType create( CreateExperienceTypeDTO dto ) {
        checkNameAvailable( dto.name() );

        var experienceType = new ExperienceType( dto.name() );
        return experienceTypeRepository.saveAndFlush( experienceType );
    }

    public void delete( String idOrName ) {
        var experienceType = findByIdOrNameOrThrow( idOrName );
        checkPermissionToDelete( experienceType );

        experienceType.setDeleted( true );
        experienceTypeRepository.save( experienceType );
    }

    @Override
    public boolean hasPermissionToEdit( ExperienceType entity ) {
        return userService.isUserAdminSession();
    }

    @Override
    public boolean hasPermissionToDelete( ExperienceType entity ) {
        return userService.isUserAdminSession();
    }
}
