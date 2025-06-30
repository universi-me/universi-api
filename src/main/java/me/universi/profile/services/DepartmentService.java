package me.universi.profile.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.interfaces.UniqueNameEntityService;
import me.universi.profile.dto.CreateDepartmentDTO;
import me.universi.profile.dto.UpdateDepartmentDTO;
import me.universi.profile.entities.Department;
import me.universi.profile.repositories.DepartmentRepository;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

@Service
public class DepartmentService extends UniqueNameEntityService<Department> {
    private final DepartmentRepository repository;
    private final UserService userService;

    public DepartmentService(DepartmentRepository departmentRepository, UserService userService) {
        this.repository = departmentRepository;
        this.userService = userService;
        this.entityName = "Departamento";
        this.fieldName = "sigla";
    }

    public static DepartmentService getInstance() {
        return Sys.context().getBean( "departmentService", DepartmentService.class );
    }

    @Override
    public Optional<Department> findUnchecked( UUID id ) {
        return repository.findById( id );
    }

    @Override
    public Optional<Department> findByNameUnchecked( String acronym ) {
        return repository.findFirstByAcronymIgnoreCase( acronym );
    }

    @Override
    public Optional<Department> findByIdOrNameUnchecked( String idOrAcronym ) {
        return repository.findFirstByIdOrAcronymIgnoreCase(
            CastingUtil.getUUID( idOrAcronym ).orElse( null ),
            idOrAcronym
        );
    }

    @Override
    public List<Department> findAllUnchecked() {
        return repository.findAll();
    }

    public Department create( @Valid CreateDepartmentDTO dto ) {
        checkNameAvailable( dto.acronym() );
        checkPermissionToCreate();

        var department = new Department( dto.acronym(), dto.name() );
        return repository.saveAndFlush( department );
    }

    public Department update( @NotBlank String id, @Valid UpdateDepartmentDTO dto ) {
        var department = findByIdOrNameOrThrow( id );
        checkPermissionToEdit( department );

        dto.acronym().ifPresent( acronym -> {
            if ( acronym.isBlank() )
                throw new UniversiBadRequestException( "A sigla não pode estar em branco" );

            checkNameAvailableIgnoreIf( acronym, d -> d.getId().equals( department.getId() ) );
            department.setAcronym( acronym );
        } );

        dto.name().ifPresent( name -> {
            if ( name.isBlank() )
                throw new UniversiBadRequestException( "O nome não pode estar em branco" );

            department.setName( name );
        } );

        return repository.saveAndFlush( department );
    }

    public void delete( @NotBlank String id ) {
        var department = findByIdOrNameOrThrow( id );
        checkPermissionToDelete( department );

        repository.delete( department );
    }

    @Override
    public boolean hasPermissionToCreate() {
        return userService.isUserAdminSession();
    }

    @Override
    public boolean hasPermissionToEdit( Department entity ) {
        return userService.isUserAdminSession();
    }

    @Override
    public boolean hasPermissionToDelete( Department entity ) {
        return hasPermissionToEdit( entity );
    }
}
