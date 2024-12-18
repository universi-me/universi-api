package me.universi.education.services;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import me.universi.education.dto.CreateEducationDTO;
import me.universi.education.dto.UpdateEducationDTO;
import me.universi.education.entities.Education;
import me.universi.education.repositories.EducationRepository;
import me.universi.institution.services.InstitutionService;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EducationService {
    @PersistenceContext
    private EntityManager entityManager;
    private final EducationRepository educationRepository;
    private final UserService userService;
    private final ProfileService profileService;
    private final InstitutionService institutionService;
    private final EducationTypeService typeEducationService;

    public EducationService(EducationRepository educationRepository, UserService userService, ProfileService profileService, EducationTypeService typeEducationService, InstitutionService institutionService){
        this.educationRepository = educationRepository;
        this.userService = userService;
        this.profileService = profileService;
        this.typeEducationService = typeEducationService;
        this.institutionService =institutionService;
    }

    public List<Education> findAll(){
        return educationRepository.findAll();
    }

    public Optional<Education> find( UUID id ) {
        return educationRepository.findFirstById(id);
    }

    public Education findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Educação de ID '" + id + "' não encontrada" ) );
    }

    public Education update( @NonNull UUID id, @NonNull UpdateEducationDTO dto ) {
        var education = findOrThrow( id );
        checkPermissionForEdit( education );

        if ( dto.educationType() != null )
            education.setEducationType( typeEducationService.findByIdOrNameOrThrow( dto.educationType() ) );

        if ( dto.institution() != null ) {
            education.setInstitution( institutionService.findOrThrow( dto.institution() ) );
        }

        if ( dto.startDate() != null ) {
            education.setStartDate( dto.startDate() );
        }

        if ( dto.removeEndDate() ) {
            education.setEndDate( null );
        } else if ( dto.endDate() != null ) {
            education.setEndDate( dto.endDate() );
        }

        return educationRepository.saveAndFlush( education );
    }

    public void delete( @NonNull UUID id ) {
        var education = findOrThrow( id );
        checkPermissionForDelete( education );

       educationRepository.delete( education );
    }

    private void checkPermissionForEdit( @NonNull Education education ) throws AccessDeniedException {
        if ( !profileService.isSessionOfProfile( education.getProfile() ) ) {
            throw new AccessDeniedException( "Você não tem permissão para editar esta Educação" );
        }
    }

    private void checkPermissionForDelete( @NonNull Education education ) throws AccessDeniedException {
        if ( !profileService.isSessionOfProfile( education.getProfile() )
            && !userService.isUserAdminSession()
        ) {
            throw new AccessDeniedException( "Você não tem permissão para deletar esta Educação" );
        }
    }

    public Education create( @NonNull CreateEducationDTO dto ) {
        var educationType = typeEducationService.findByIdOrNameOrThrow( dto.educationType() );
        var institution = institutionService.findOrThrow( dto.institution() );

        var education = new Education(
            educationType,
            institution,
            dto.startDate(),
            dto.endDate(),
            profileService.getProfileInSession()
        );

        return educationRepository.saveAndFlush( education );
    }
}
