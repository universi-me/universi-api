package me.universi.activity.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.activity.dto.CreateActivityDTO;
import me.universi.activity.dto.FilterActivityDTO;
import me.universi.activity.dto.UpdateActivityDTO;
import me.universi.activity.entities.Activity;
import me.universi.activity.entities.ActivityType;
import me.universi.activity.repositories.ActivityRepository;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.exceptions.UniversiServerException;
import me.universi.api.interfaces.EntityService;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.DTO.CreateGroupDTO;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.services.RoleService;
import me.universi.user.services.UserService;

@Service
public class ActivityService extends EntityService<Activity> {
    private @Nullable ActivityRepository repository;
    private @Nullable UserService userService;
    private @Nullable ProfileService profileService;
    private @Nullable CompetenceTypeService competenceTypeService;
    private @Nullable ActivityTypeService activityTypeService;
    private @Nullable GroupService groupService;
    private @Nullable RoleService roleService;

    @PersistenceContext
    private EntityManager entityManager;

    public ActivityService() {
        this.entityName = "Atividade";
    }

    public static @NotNull ActivityService getInstance() {
        return Sys.context.getBean( "activityService", ActivityService.class );
    }

    @Override protected Optional<Activity> findUnchecked( UUID id ) { return repository().findById( id ); }
    @Override protected List<Activity> findAllUnchecked() { return repository().findAll(); }

    public List<Activity> findByGroup( @NotNull Group group ) {
        return group
            .getSubGroups()
            .stream()
            .filter( g -> g.isActivityGroup() && isValid( g.getActivity().get() ) )
            .map( g -> g.getActivity().get() )
            .toList();
    }

    public List<Activity> findByProfile( @NotNull String idOrUsername ) { return findByProfile( profileService().findByIdOrUsernameOrThrow( idOrUsername ) ); }
    public List<Activity> findByProfile( @NotNull UUID id ) { return findByProfile( profileService().findOrThrow( id ) ); }
    public List<Activity> findByProfile( @NotNull Profile profile ) {
        return profile
            .getGroups()
            .stream()
            .filter( pg -> groupService().isValid( pg.getGroup() ) && pg.getGroup().isActivityGroup() && isValid( pg.getGroup().getActivity().get() ) )
            .map( pg -> pg.getGroup().getActivity().get() )
            .toList();
    }

    public List<Activity> findByProfileAndCompetenceType( @NotNull String idOrUsername, @NotNull String competenceType ) { return findByProfileAndCompetenceType( profileService().findByIdOrUsernameOrThrow( idOrUsername ), competenceTypeService().findByIdOrNameOrThrow( competenceType ) ); }
    public List<Activity> findByProfileAndCompetenceType( @NotNull UUID id, @NotNull UUID competenceType ) { return findByProfileAndCompetenceType( profileService().findOrThrow( id ), competenceTypeService().findOrThrow( competenceType ) ); }
    public List<Activity> findByProfileAndCompetenceType( @NotNull Profile profile, @NotNull CompetenceType competenceType ) {
        return findByProfile( profile )
            .stream()
            .filter( a -> a.getBadges().contains( competenceType ) )
            .toList();
    }

    public List<Activity> filter( @Nullable FilterActivityDTO dto ) {
        if ( dto == null )
            return findAll();

        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery( Activity.class );
        var root = query.from( Activity.class );
        query.select( root );

        var filters = new ArrayList<Predicate>();

        dto.type().map( activityTypeService()::findByIdOrNameOrThrow ).ifPresent( activityType -> {
            filters.add( criteriaBuilder.equal( root.get( "type" ).get( "id" ) , activityType.getId() ) );
        } );

        dto.group().map( groupService()::findByIdOrPathOrThrow ).ifPresent( group -> {
            filters.add( criteriaBuilder.equal( root.get( "group" ).get( "parentGroup" ).get( "id" ) , group.getId() ) );
        } );

        query.where( filters.toArray( new Predicate[ filters.size() ] ) );

        return entityManager
            .createQuery( query )
            .getResultList()
            .stream()
            .filter( this::isValid )
            .toList();
    }

    public boolean existsByType( @NotNull ActivityType activityType ) {
        return repository().existsByType( activityType );
    }

    public @NotNull Activity create( @Valid CreateActivityDTO dto ) {
        var parentGroup = groupService().findByIdOrPathOrThrow( dto.group() );

        checkPermissionToCreate( parentGroup );
        validateDates( dto );

        var name = dto.name().trim();
        var nickname = dto.nickname().trim();
        var description = dto.description().trim();
        var location = dto.location().trim();
        var badges = dto.badges().map( competenceTypeService()::findByIdOrNameOrThrow )
            .orElse( Collections.emptyList() );
        var type = activityTypeService().findByIdOrNameOrThrow( dto.type() );

        var activityGroup = groupService().createGroup( new CreateGroupDTO(
            Optional.of( parentGroup.getId().toString() ),
            nickname,
            name,
            dto.image(),
            dto.bannerImage(),
            Optional.empty(),
            description,
            dto.groupType(),
            false,
            true,
            false
        ) );

        var activity = new Activity();
        activity.setLocation( location );
        activity.setWorkload( dto.workload() );
        activity.setStartDate( dto.startDate() );
        activity.setEndDate( dto.endDate() );
        activity.setBadges( badges );
        activity.setType( type );
        activity.setGroup( activityGroup );

        activity = repository().saveAndFlush( activity );

        activityGroup.setActivity( activity );
        GroupService.getRepository().save( activityGroup );

        return activity;
    }

    public @NotNull Activity update( UUID id, @Valid UpdateActivityDTO dto ) {
        var activity = findOrThrow( id );
        checkPermissionToEdit( activity );
        validateDates( activity, dto );

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

        groupService().deleteGroup( activity.getGroup().getId() );

        activity.setDeletedAt( new Date() );
        repository().saveAndFlush( activity );
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
            && activity.getDeletedAt() == null
            && groupService().isValid( activity.getGroup() )
            && roleService().hasPermission(
                activity.getGroup(),
                FeaturesTypes.ACTIVITY,
                Permission.READ
            );
    }

    public boolean hasPermissionToCreate( @NotNull Group group ) {
        return roleService().hasPermission(
            group,
            FeaturesTypes.ACTIVITY,
            Permission.READ_WRITE
        );
    }

    public void checkPermissionToCreate( @NotNull Group group ) {
        if ( !hasPermissionToCreate( group ) ) throw makeDeniedException( "criar" );
    }

    @Override public boolean hasPermissionToEdit( Activity entity ) {
        return roleService().hasPermission(
            entity.getGroup(),
            FeaturesTypes.ACTIVITY,
            Permission.READ_WRITE
        );
    }

    @Override public boolean hasPermissionToDelete( Activity entity ) {
        return roleService().hasPermission(
            entity.getGroup(),
            FeaturesTypes.ACTIVITY,
            Permission.READ_WRITE_DELETE
        );
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

    public synchronized void roleService( @NotNull RoleService roleService ) { this.roleService = roleService; }
    public synchronized @NotNull RoleService roleService() {
        if ( roleService == null ) roleService( RoleService.getInstance() );
        return roleService;
    }

    /**
     * @deprecated Use {@link #hasPermissionToCreate( Group )} instead
     * @return Never returns. Always throws {@link UniversiServerException}
     * @throws UniversiServerException
     */
    @Deprecated( forRemoval = false ) @Override public boolean hasPermissionToCreate() { throw new UniversiServerException( "Erro no servidor" ); }

    /**
     * @deprecated Use {@link #checkPermissionToCreate( Group )} instead
     * @return Never successfully returns. Always throws {@link UniversiServerException}
     * @throws UniversiServerException
     */
    @Deprecated( forRemoval = false ) @Override public void checkPermissionToCreate() { hasPermissionToCreate(); }
}
