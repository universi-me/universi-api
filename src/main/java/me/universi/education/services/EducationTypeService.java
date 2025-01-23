package me.universi.education.services;

import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.education.dto.CreateEducationTypeDTO;
import me.universi.education.dto.UpdateEducationTypeDTO;
import me.universi.education.entities.EducationType;
import me.universi.education.repositories.EducationTypeRepository;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EducationTypeService {

    private final EducationTypeRepository educationTypeRepository;
    private final UserService userService;

    public EducationTypeService(EducationTypeRepository educationTypeRepository, UserService userService){
        this.educationTypeRepository = educationTypeRepository;
        this.userService = userService;
    }

    public List<EducationType> findAll(){
        return educationTypeRepository.findAll();
    }

    public Optional<EducationType> find( UUID id ) {
        return educationTypeRepository.findById( id );
    }

    public EducationType findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Educação de ID '" + id + "' não encontrado" ) );
    }

    public Optional<EducationType> findByName( String name ) {
        return educationTypeRepository.findFirstByNameIgnoreCase( name );
    }

    public EducationType findByNameOrThrow( String name ) throws EntityNotFoundException {
        return findByName( name ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Educação de nome '" + name + "' não encontrado" ) );
    }

    public Optional<EducationType> findByIdOrName( String idOrName ) {
        return educationTypeRepository.findFirstByIdOrNameIgnoreCase(
            CastingUtil.getUUID(idOrName).orElse(null),
            idOrName
        );
    }

    public EducationType findByIdOrNameOrThrow( String idOrName ) throws EntityNotFoundException {
        return findByIdOrName( idOrName ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Educação de nome ou ID '" + idOrName + "' não encontrado" ) );
    }

    public EducationType update( @NonNull String idOrName, @NonNull UpdateEducationTypeDTO updateEducationTypeDTO ) {
        if ( !userService.isUserAdminSession() )
            throw new UniversiForbiddenAccessException( "Você não tem permissão para alterar este Tipo de Educação" );

        var educationType = findByIdOrNameOrThrow( idOrName );

        if ( updateEducationTypeDTO.name() != null && !updateEducationTypeDTO.name().isBlank() )
            educationType.setName( updateEducationTypeDTO.name() );

        return educationTypeRepository.saveAndFlush( educationType );
    }

    public EducationType create( CreateEducationTypeDTO createEducationTypeDTO ) throws UniversiConflictingOperationException {
        var existingEducationType = findByName( createEducationTypeDTO.name() );
        if ( existingEducationType.isPresent() )
            throw new UniversiConflictingOperationException( "Tipo de Educação de nome '" + existingEducationType.get().getName() + "' já existe" );

        return educationTypeRepository.saveAndFlush(
            new EducationType( createEducationTypeDTO.name() )
        );
    }

    public void delete( String idOrName ) {
        if ( !userService.isUserAdminSession() )
            throw new UniversiForbiddenAccessException( "Você não tem permissão para deletar este Tipo de Educação" );

        var educationType = findByIdOrNameOrThrow( idOrName );
        educationTypeRepository.delete( educationType );
    }
}
