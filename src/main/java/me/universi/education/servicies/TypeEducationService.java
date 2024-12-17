package me.universi.education.servicies;

import me.universi.education.dto.CreateTypeEducationDTO;
import me.universi.education.dto.UpdateTypeEducationDTO;
import me.universi.education.entities.TypeEducation;
import me.universi.education.repositories.TypeEducationRepository;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TypeEducationService {

    private final TypeEducationRepository typeEducationRepository;
    private final UserService userService;

    public TypeEducationService(TypeEducationRepository typeEducationRepository, UserService userService){
        this.typeEducationRepository = typeEducationRepository;
        this.userService = userService;
    }

    public List<TypeEducation> findAll(){
        return typeEducationRepository.findAll();
    }

    public Optional<TypeEducation> find( UUID id ) {
        return typeEducationRepository.findById( id );
    }

    public TypeEducation findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Educação de ID '" + id + "' não encontrado" ) );
    }

    public Optional<TypeEducation> findByName( String name ) {
        return typeEducationRepository.findFirstByNameIgnoreCase( name );
    }

    public TypeEducation findByNameOrThrow( String name ) throws EntityNotFoundException {
        return findByName( name ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Educação de nome '" + name + "' não encontrado" ) );
    }

    public Optional<TypeEducation> findByIdOrName( String idOrName ) {
        return typeEducationRepository.findFirstByIdOrNameIgnoreCase(
            CastingUtil.getUUID(idOrName).orElse(null),
            idOrName
        );
    }

    public TypeEducation findByIdOrNameOrThrow( String idOrName ) throws EntityNotFoundException {
        return findByIdOrName( idOrName ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Educação de nome ou ID '" + idOrName + "' não encontrado" ) );
    }

    public TypeEducation update( @NonNull String idOrName, @NonNull UpdateTypeEducationDTO updateTypeEducationDTO ) {
        if ( !userService.isUserAdminSession() )
            throw new AccessDeniedException( "Você não tem permissão para alterar este Tipo de Educação" );

        var typeEducation = findByIdOrNameOrThrow( idOrName );

        if ( updateTypeEducationDTO.name() != null && !updateTypeEducationDTO.name().isBlank() )
            typeEducation.setName( updateTypeEducationDTO.name() );

        return typeEducationRepository.saveAndFlush( typeEducation );
    }

    public TypeEducation create( CreateTypeEducationDTO createTypeEducationDTO ) throws IllegalStateException {
        var existingTypeEducation = findByName( createTypeEducationDTO.name() );
        if ( existingTypeEducation.isPresent() )
            throw new IllegalStateException( "Tipo de Educação de nome '" + existingTypeEducation.get().getName() + "' já existe" );

        return typeEducationRepository.saveAndFlush(
            new TypeEducation( createTypeEducationDTO.name() )
        );
    }

    public void delete( String idOrName ) {
        if ( !userService.isUserAdminSession() )
            throw new AccessDeniedException( "Você não tem permissão para deletar este Tipo de Educação" );

        var typeEducation = findByIdOrNameOrThrow( idOrName );
        typeEducationRepository.delete( typeEducation );
    }
}
