package me.universi.activity.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import me.universi.Sys;
import me.universi.activity.dto.ChangeActivityParticipantsDTO;
import me.universi.activity.entities.*;
import me.universi.activity.repositories.ActivityParticipantRepository;
import me.universi.api.interfaces.EntityService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

@Service
public class ActivityParticipantService extends EntityService<ActivityParticipant> {
    private @Nullable ActivityParticipantRepository repository;
    private @Nullable ActivityService activityService;
    private @Nullable UserService userService;
    private @Nullable ProfileService profileService;

    public ActivityParticipantService() {
        this.entityName = "Participante de Atividade";
    }

    public static @NotNull ActivityParticipantService getInstance() {
        return Sys.context.getBean( "activityParticipantService", ActivityParticipantService.class );
    }

    public List<Profile> getParticipants( UUID id ) {
        return activityService()
            .findOrThrow( id )
            .getParticipants()
            .stream()
            .sorted( Comparator.comparing( ActivityParticipant::getJoinedAt ) )
            .map( ActivityParticipant::getProfile )
            .filter( profileService()::isValid )
            .toList();
    }

    public List<ActivityParticipant> findByActivity( @NotNull Activity activity ) {
        return repository().findByActivity( activity );
    }

    public void changeParticipants( UUID id, @Valid ChangeActivityParticipantsDTO dto ) {
        var activity = activityService().findOrThrow( id );
        checkPermissionToChangeParticipants( activity );

        dto.remove().ifPresent( remove -> {
            var removedParticipants = profileService()
                .findByIdOrUsernameOrThrow( remove )
                .stream()
                .map( p -> this.findByActivityAndProfile( activity, p ) )
                .filter( Optional::isPresent )
                .map( Optional::get )
                .toList();

            repository().deleteAll( removedParticipants );
        } );

        dto.add().ifPresent( add -> {
            var participants = profileService().findByIdOrUsernameOrThrow( add )
                .stream()
                .filter( p -> !isParticipant( activity, p ) )
                .map( profile -> {
                    var participant = new ActivityParticipant();
                    participant.setActivity( activity );
                    participant.setProfile( profile );
                    return participant;
                } )
                .toList();

            repository().saveAllAndFlush( participants );
        } );

        activity.setParticipants( findByActivity( activity ) );
    }

    public Optional<ActivityParticipant> findByActivityAndProfile( @NotNull Activity activity, @NotNull Profile profile ) {
        return repository().findFirstByActivityAndProfile( activity, profile ).filter( this::isValid );
    }

    public boolean isParticipant( @NotNull Activity activity, @NotNull Profile profile ) {
        return findByActivityAndProfile( activity, profile ).isPresent();
    }

    public void checkPermissionToChangeParticipants( @NotNull Activity activity ) {
        activityService().checkPermissionToEdit( activity );
    }

    @Override public boolean isValid( ActivityParticipant entity ) {
        return entity != null
            && entity.getRemovedAt() == null
            && activityService().isValid( entity.getActivity() )
            && profileService().isValid( entity.getProfile() );
    }

    @Override protected Optional<ActivityParticipant> findUnchecked( UUID id ) { return repository().findById( id ); }
    @Override protected List<ActivityParticipant> findAllUnchecked() { return repository().findAll(); }

    @Override public boolean hasPermissionToEdit( ActivityParticipant entity ) {
        return activityService().hasPermissionToEdit( entity.getActivity() )
            || profileService().getProfileInSessionOrThrow().getId().equals( entity.getProfile().getId() );
    }

    @Override public boolean hasPermissionToDelete( ActivityParticipant entity ) { return hasPermissionToEdit( entity ); }

    public synchronized void repository( @NotNull ActivityParticipantRepository repository ) { this.repository = repository; }
    public synchronized @NotNull ActivityParticipantRepository repository() {
        if ( repository == null ) repository( Sys.context.getBean( "activityParticipantRepository", ActivityParticipantRepository.class ) );
        return repository;
    }

    public synchronized void activityService( @NotNull ActivityService activityService ) { this.activityService = activityService; }
    public synchronized @NotNull ActivityService activityService() {
        if ( activityService == null ) activityService( ActivityService.getInstance() );
        return activityService;
    }

    public synchronized void profileService( @NotNull ProfileService profileService ) { this.profileService = profileService; }
    public synchronized @NotNull ProfileService profileService() {
        if ( profileService == null ) profileService( ProfileService.getInstance() );
        return profileService;
    }

    public synchronized void userService( @NotNull UserService userService ) { this.userService = userService; }
    public synchronized @NotNull UserService userService() {
        if ( userService == null ) userService( UserService.getInstance() );
        return userService;
    }
}
