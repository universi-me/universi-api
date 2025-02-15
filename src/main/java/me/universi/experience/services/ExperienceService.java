package me.universi.experience.services;

import me.universi.experience.dto.CreateExperienceDTO;
import me.universi.experience.dto.UpdateExperienceDTO;
import me.universi.experience.entities.Experience;
import me.universi.experience.repositories.ExperienceRepository;
import me.universi.institution.services.InstitutionService;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final InstitutionService institutionService;
    private final UserService userService;
    private final ProfileService profileService;
    private final ExperienceTypeService experienceTypeService;


    public ExperienceService(ExperienceRepository experienceRepository, UserService userService, ProfileService profileService, ExperienceTypeService typeExperienceService, InstitutionService institutionService){
        this.experienceRepository = experienceRepository;
        this.userService = userService;
        this.profileService = profileService;
        this.experienceTypeService = typeExperienceService;
        this.institutionService = institutionService;
    }

    public List<Experience> findAll(){
        return experienceRepository.findAll();
    }

    public Optional<Experience> find( UUID id ){
        return experienceRepository.findById( id );
    }

    public Experience findOrThrow( UUID id ) {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Experiência de ID '" + id + "' não encontrada" ) );
    }

    public Experience create( CreateExperienceDTO dto ) {
        var experienceType = experienceTypeService.findByIdOrNameOrThrow( dto.experienceType() );
        var institution = institutionService.findOrThrow( dto.institution() );

        var experience = new Experience(
            experienceType,
            institution,
            dto.description(),
            dto.startDate(),
            dto.endDate(),
            profileService.getProfileInSessionOrThrow()
        );

        return experienceRepository.saveAndFlush( experience );
    }

    public Experience update( UUID id, UpdateExperienceDTO dto ) {
        var experience = findOrThrow( id );
        checkPermissionForEdit( experience );

        if ( dto.experienceType() != null )
            experience.setExperienceType( experienceTypeService.findByIdOrNameOrThrow( dto.experienceType() ) );

        if ( dto.institution() != null ) {
            experience.setInstitution( institutionService.findOrThrow( dto.institution() ) );
        }

        if ( dto.description() != null && !dto.description().isBlank() ) {
            experience.setDescription( dto.description() );
        }

        if ( dto.startDate() != null ) {
            experience.setStartDate( dto.startDate() );
        }

        if ( dto.removeEndDate() ) {
            experience.setEndDate( null );
        } else if ( dto.endDate() != null ) {
            experience.setEndDate( dto.endDate() );
        }

        return experienceRepository.saveAndFlush( experience );
    }

    private void checkPermissionForEdit( @NonNull Experience experience ) throws AccessDeniedException {
        if ( !profileService.isSessionOfProfile( experience.getProfile() ) )
            throw new AccessDeniedException( "Você não tem permissão para alterar esta Experiência" );
    }

    private void checkPermissionForDelete( @NonNull Experience experience ) throws AccessDeniedException {
        if ( !profileService.isSessionOfProfile( experience.getProfile() )
            || !userService.isUserAdminSession()
        ) {
            throw new AccessDeniedException( "Você não tem permissão para deletar esta Experiência" );
        }
    }

    public void delete( UUID id ) {
        var experience = findOrThrow( id );
        checkPermissionForDelete( experience );

        experience.setDeleted( true );
        experienceRepository.save( experience );
    }
}
