package me.universi.experience.services;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
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
public class ExperienceTypeService {

    private final ExperienceTypeRepository experienceTypeRepository;
    private final UserService userService;

    public ExperienceTypeService(ExperienceTypeRepository typeExperienceRepository, UserService userService){
        this.experienceTypeRepository = typeExperienceRepository;
        this.userService = userService;
    }

    public List<ExperienceType> findAll(){
        return experienceTypeRepository.findAll();
    }

    public Optional<ExperienceType> find( UUID id ) {
        return experienceTypeRepository.findById(id);
    }

    public ExperienceType findOrThrow( UUID id ) {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Experiência de ID '" + id + "' não encontrado" ) );
    }

    public Optional<ExperienceType> findByIdOrName( String idOrName ) {
        return experienceTypeRepository.findFirstByIdOrNameIgnoreCase(
            CastingUtil.getUUID( idOrName ).orElse( null ),
            idOrName
        );
    }

    public ExperienceType findByIdOrNameOrThrow( String idOrName ) {
        return findByIdOrName( idOrName ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Experiência de ID ou nome '" + idOrName + "' não encontrado" ) );
    }

    public ExperienceType update( @NonNull String idOrName, @NonNull UpdateExperienceTypeDTO dto ) throws AccessDeniedException {
        if ( !userService.isUserAdminSession() )
            throw new AccessDeniedException( "Você não tem permissão para alterar este Tipo de Experiência" );

        var experienceType = findByIdOrNameOrThrow( idOrName );
        if ( dto.name() != null && !dto.name().isBlank() )
            experienceType.setName( dto.name() );

        return experienceTypeRepository.saveAndFlush( experienceType );
    }

    public ExperienceType create( CreateExperienceTypeDTO dto ) {
        var existingExperienceType = findByIdOrName( dto.name() );
        if ( existingExperienceType.isPresent() )
            throw new IllegalStateException( "Tipo de Experiência de nome '" + existingExperienceType.get().getName() + "' já existe" );

        return experienceTypeRepository.saveAndFlush( new ExperienceType( dto.name() ) );
    }

    public void delete( String idOrName ) {
        var experienceType = findByIdOrNameOrThrow( idOrName );
        if ( !userService.isUserAdminSession() )
            throw new AccessDeniedException( "Você não tem permissão para deletar este Tipo de Experiência" );

        experienceTypeRepository.delete( experienceType );
    }
}
