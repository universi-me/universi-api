package me.universi.activity.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.activity.dto.CreateActivityDTO;
import me.universi.activity.dto.UpdateActivityDTO;
import me.universi.activity.entities.Activity;
import me.universi.activity.repositories.ActivityRepository;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.interfaces.EntityService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

@Service
public class ActivityService extends EntityService<Activity> {
    private @Nullable ActivityRepository repository;
    private @Nullable UserService userService;
    private @Nullable ProfileService profileService;
    private @Nullable CompetenceTypeService competenceTypeService;
    private @Nullable ActivityTypeService activityTypeService;
    private @Nullable GroupService groupService;

    public ActivityService() {
        this.entityName = "Atividade";
    }

    public static @NotNull ActivityService getInstance() {
        return Sys.context.getBean( "activityService", ActivityService.class );
    }

    @Override protected Optional<Activity> findUnchecked( UUID id ) { return repository().findById( id ); }
    @Override protected List<Activity> findAllUnchecked() { return repository().findAll(); }

    public List<Activity> findByGroup( @NotNull Group group ) {
        return repository()
            .findByGroup( group )
            .stream()
            .filter( this::isValid )
            .toList();
    }

    public @NotNull Activity create( @Valid CreateActivityDTO dto ) {
        checkPermissionToCreate();
        validateDates( dto );

        var name = dto.name().trim();
        var description = dto.description().trim();
        var location = dto.location().trim();
        var badges = dto.badges().map( competenceTypeService()::findByIdOrNameOrThrow )
            .orElse( Collections.emptyList() );
        var type = activityTypeService().findByIdOrNameOrThrow( dto.type() );
        var author = profileService().getProfileInSessionOrThrow();
        var group = groupService().findByIdOrPathOrThrow( dto.group() );

        var activity = new Activity();
        activity.setName( name );
        activity.setDescription( description );
        activity.setLocation( location );
        activity.setWorkload( dto.workload() );
        activity.setStartDate( dto.startDate() );
        activity.setEndDate( dto.endDate() );
        activity.setBadges( badges );
        activity.setType( type );
        activity.setAuthor( author );
        activity.setParticipants( Collections.emptyList() );
        activity.setGroup( group );

        return repository().saveAndFlush( activity );
    }

    public @NotNull Activity update( UUID id, @Valid UpdateActivityDTO dto ) {
        var activity = findOrThrow( id );
        checkPermissionToEdit( activity );
        validateDates( activity, dto );

        dto.name().ifPresent( name -> activity.setName( name.trim() ) );
        dto.description().ifPresent( description -> activity.setDescription( description.trim() ) );
        dto.location().ifPresent( location -> activity.setLocation( location.trim() ) );
        dto.workload().ifPresent( activity::setWorkload );
        dto.startDate().ifPresent( activity::setStartDate );
        dto.endDate().ifPresent( activity::setEndDate );
        dto.badges().ifPresent( badges -> {
            activity.setBadges( competenceTypeService().findByIdOrNameOrThrow( badges ) );
        } );
        dto.type().ifPresent( type -> {
            activity.setType( activityTypeService().findByIdOrNameOrThrow( type ) );
        } );

        return repository().saveAndFlush( activity );
    }

    public void delete( UUID id ) {
        var activity = findOrThrow( id );
        checkPermissionToDelete( activity );
        repository().delete( activity );
    }

    public void validateDates( @Valid CreateActivityDTO dto ) {
        validateDates( dto.startDate() , dto.endDate() );
    }

    public void validateDates( @NotNull Activity existingActivity, @Valid UpdateActivityDTO dto ) {
        if ( dto.startDate().isEmpty() && dto.endDate().isPresent() ) return;

        var start = dto.startDate().orElse( existingActivity.getStartDate() );
        var end = dto.endDate().orElse( existingActivity.getEndDate() );
        validateDates( start , end );
    }

    public void validateDates( @NotNull Date start, @NotNull Date end ) {
        if ( start.after( end ) )
            throw new UniversiBadRequestException( "A data de início não pode ser após a data de término" );
    }

    @Override public boolean isValid( Activity activity ) {
        return activity != null
            && activity.getDeletedAt() == null;
    }

    @Override public boolean hasPermissionToEdit( Activity entity ) {
        return userService().isUserAdminSession()
            || profileService().getProfileInSessionOrThrow().getId().equals( entity.getAuthor().getId() );
    }

    @Override public boolean hasPermissionToDelete( Activity entity ) {
        return hasPermissionToEdit( entity );
    }

    public synchronized void repository( @NotNull ActivityRepository repository ) { this.repository = repository; }
    public synchronized @NotNull ActivityRepository repository() {
        if ( repository == null ) repository( Sys.context.getBean( "activityRepository", ActivityRepository.class ) );
        return repository;
    }

    public synchronized void userService( @NotNull UserService userService ) { this.userService = userService; }
    public synchronized @NotNull UserService userService() {
        if ( userService == null ) userService( UserService.getInstance() );
        return userService;
    }

    public synchronized void profileService( @NotNull ProfileService profileService ) { this.profileService = profileService; }
    public synchronized @NotNull ProfileService profileService() {
        if ( profileService == null ) profileService( ProfileService.getInstance() );
        return profileService;
    }

    public synchronized void competenceTypeService( @NotNull CompetenceTypeService competenceTypeService ) { this.competenceTypeService = competenceTypeService; }
    public synchronized @NotNull CompetenceTypeService competenceTypeService() {
        if ( competenceTypeService == null ) competenceTypeService( CompetenceTypeService.getInstance() );
        return competenceTypeService;
    }

    public synchronized void activityTypeService( @NotNull ActivityTypeService activityTypeService ) { this.activityTypeService = activityTypeService; }
    public synchronized @NotNull ActivityTypeService activityTypeService() {
        if ( activityTypeService == null ) activityTypeService( ActivityTypeService.getInstance() );
        return activityTypeService;
    }

    public synchronized void groupService( @NotNull GroupService groupService ) { this.groupService = groupService; }
    public synchronized @NotNull GroupService groupService() {
        if ( groupService == null ) groupService( GroupService.getInstance() );
        return groupService;
    }
}
