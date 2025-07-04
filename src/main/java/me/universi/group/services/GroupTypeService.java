package me.universi.group.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.interfaces.UniqueNameEntityService;
import me.universi.group.DTO.CreateGroupTypeDTO;
import me.universi.group.DTO.UpdateGroupTypeDTO;
import me.universi.group.entities.GroupType;
import me.universi.group.repositories.GroupTypeRepository;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;


@Service
public class GroupTypeService extends UniqueNameEntityService<GroupType> {
    private @Nullable GroupTypeRepository groupTypeRepository;
    private @Nullable UserService userService;
    private @Nullable GroupService groupService;

    public GroupTypeService() { this.entityName = "Tipo de Grupo"; }
    public static @NotNull GroupTypeService getInstance() {
        return Sys.context().getBean( "groupTypeService", GroupTypeService.class );
    }

    @Override protected Optional<GroupType> findUnchecked( UUID id ) { return repository().findById( id ); }
    @Override protected Optional<GroupType> findByNameUnchecked( String name ) { return repository().findFirstByLabelIgnoreCase( name ); }
    @Override protected Optional<GroupType> findByIdOrNameUnchecked( String idOrName ) { return repository().findFirstByIdOrLabelIgnoreCase( CastingUtil.getUUID( idOrName ).orElse( null ), idOrName ); }
    @Override protected List<GroupType> findAllUnchecked() { return repository().findAll(); }

    public @NotNull GroupType create( @Valid @NotNull CreateGroupTypeDTO dto ) {
        checkPermissionToCreate();

        var groupType = new GroupType();
        groupType.setLabel( dto.label().trim() );

        return repository().saveAndFlush( groupType );
    }

    public @NotNull GroupType update( @NotNull String id, @Valid @NotNull UpdateGroupTypeDTO dto ) {
        var groupType = findByIdOrNameOrThrow( id );
        checkPermissionToEdit( groupType );

        dto.label().ifPresent( label -> groupType.setLabel( label.trim() ) );
        return repository().saveAndFlush( groupType );
    }

    public void delete( String id ) {
        var groupType = findByIdOrNameOrThrow( id );
        checkPermissionToDelete( groupType );

        if ( groupService().existsByType( groupType ) )
            throw new UniversiConflictingOperationException( "O " + entityName + " não pode ser excluído pois está em uso" );

        repository().delete( groupType );
    }

    @Override public boolean hasPermissionToCreate() {
        return userService().isUserAdminSession();
    }

    @Override public boolean hasPermissionToEdit( GroupType groupType ) {
        return userService().isUserAdminSession();
    }

    @Override
    public boolean hasPermissionToDelete( GroupType groupType ) {
        return userService().isUserAdminSession();
    }

    public synchronized void repository( @NotNull GroupTypeRepository groupTypeRepository ) { this.groupTypeRepository = groupTypeRepository; }
    public synchronized @NotNull GroupTypeRepository repository() {
        if ( groupTypeRepository == null ) repository( Sys.context().getBean( "groupTypeRepository", GroupTypeRepository.class ) );
        return groupTypeRepository;
    }

    public synchronized void userService( @NotNull UserService userService ) { this.userService = userService; }
    public synchronized @NotNull UserService userService() {
        if ( userService == null ) userService( UserService.getInstance() );
        return userService;
    }

    public synchronized void groupService( @NotNull GroupService groupService ) { this.groupService = groupService; }
    public synchronized @NotNull GroupService groupService() {
        if ( groupService == null ) groupService( GroupService.getInstance() );
        return groupService;
    }
}
