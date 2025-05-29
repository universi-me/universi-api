package me.universi.activity.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.activity.dto.CreateActivityTypeDTO;
import me.universi.activity.dto.UpdateActivityTypeDTO;
import me.universi.activity.entities.ActivityType;
import me.universi.activity.repositories.ActivityTypeRepository;
import me.universi.api.interfaces.UniqueNameEntityService;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

@Service
public class ActivityTypeService extends UniqueNameEntityService<ActivityType> {
    private @Nullable UserService userService;
    private @Nullable ActivityTypeRepository activityTypeRepository;

    public ActivityTypeService() {
        this.entityName = "Tipo de Atividade";
    }

    public static @NotNull ActivityTypeService getInstance() {
        return Sys.context.getBean( "activityTypeService", ActivityTypeService.class );
    }

    public @NotNull ActivityType create( @Valid CreateActivityTypeDTO dto ) {
        checkPermissionToCreate();
        var activityType = new ActivityType();
        activityType.setName( dto.name().trim() );

        return repository().saveAndFlush( activityType );
    }

    public @NotNull ActivityType update( @NotNull String idOrName, @Valid UpdateActivityTypeDTO dto ) {
        var activityType = findByIdOrNameOrThrow( idOrName );
        checkPermissionToEdit( activityType );

        dto.name().ifPresent( name -> activityType.setName( name.trim() ) );
        return repository().saveAndFlush( activityType );
    }

    public void delete( @NotNull String idOrName ) {
        var activityType = findByIdOrNameOrThrow( idOrName );
        checkPermissionToDelete( activityType );
        repository().delete( activityType );
    }

    @Override protected Optional<ActivityType> findUnchecked( UUID id ) { return repository().findById( id ); }
    @Override protected List<ActivityType> findAllUnchecked() { return repository().findAll(); }
    @Override protected Optional<ActivityType> findByNameUnchecked(String name) { return repository().findFirstByNameIgnoreCase( name ); }
    @Override protected Optional<ActivityType> findByIdOrNameUnchecked(String idOrName) {
        return repository().findFirstByIdOrNameIgnoreCase( CastingUtil.getUUID( idOrName ).orElse( null ), idOrName );
    }

    @Override public boolean hasPermissionToCreate() { return true; }
    @Override public boolean hasPermissionToEdit( ActivityType activityType ) { return userService().isUserAdminSession(); }
    @Override public boolean hasPermissionToDelete(ActivityType entity) { return hasPermissionToEdit( entity ); }

    public synchronized void repository( @NotNull ActivityTypeRepository activityTypeRepository ) { this.activityTypeRepository = activityTypeRepository; }
    public synchronized @NotNull ActivityTypeRepository repository() {
        if ( activityTypeRepository == null ) repository( Sys.context.getBean( "activityTypeRepository", ActivityTypeRepository.class ) );
        return activityTypeRepository;
    }

    public synchronized void userService( @NotNull UserService userService ) { this.userService = userService; }
    public synchronized @NotNull UserService userService() {
        if ( userService == null ) userService( UserService.getInstance() );
        return userService;
    }
}
