package me.universi.job.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import me.universi.user.services.LoginService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.interfaces.EntityService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.services.GroupFeedService;
import me.universi.institution.services.InstitutionService;
import me.universi.job.dto.CreateJobDTO;
import me.universi.job.dto.UpdateJobDTO;
import me.universi.job.entities.Job;
import me.universi.job.repositories.JobRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.services.RoleService;
import me.universi.user.services.UserService;

@Service
public class JobService extends EntityService<Job> {
    private final JobRepository jobRepository;
    private final CompetenceTypeService competenceTypeService;
    private final GroupFeedService groupFeedService;
    private final InstitutionService institutionService;
    private final ProfileService profileService;
    private final RoleService roleService;
    private final UserService userService;
    private final LoginService loginService;

    public JobService(JobRepository jobRepository, CompetenceTypeService competenceTypeService, GroupFeedService groupFeedService, InstitutionService institutionService, ProfileService profileService, RoleService roleService, UserService userService, LoginService loginService) {
        this.jobRepository = jobRepository;
        this.competenceTypeService = competenceTypeService;
        this.groupFeedService = groupFeedService;
        this.institutionService = institutionService;
        this.profileService = profileService;
        this.roleService = roleService;
        this.userService = userService;
        this.loginService = loginService;
    }

    public static JobService getInstance() { return Sys.context().getBean("jobService", JobService.class); }

    @Override
    protected Optional<Job> findUnchecked(UUID id) {
        return jobRepository.findById(id);
    }

    @Override
    protected List<Job> findAllUnchecked() {
        return jobRepository.findAll();
    }

    public Job create( CreateJobDTO createJobDTO ) {
        checkPermissionToCreate();

        var title = checkValidTitle( createJobDTO.title() );
        var shortDescription = checkValidShortDescription( createJobDTO.shortDescription() );

        var institution = institutionService.findOrThrow( createJobDTO.institutionId() );

        var competencesTypes = competenceTypeService.findByIdOrNameOrThrow( createJobDTO.requiredCompetencesIds() );

        var profileInSession = profileService.getProfileInSessionOrThrow();

        var job = new Job();
        job.setTitle(title);
        job.setShortDescription(shortDescription);
        job.setLongDescription( createJobDTO.longDescription() );
        job.setInstitution(institution);
        job.setRequiredCompetences(competencesTypes);
        job.setClosed(false);
        job.setAuthor( profileInSession );

        job = save(job);

        var organizationId = profileInSession.getUser().getOrganization().getId().toString();

        if ( roleService.hasPermission( organizationId , FeaturesTypes.FEED, Permission.READ_WRITE) ) {
            // TODO: group environment variable for message
            var postMessage = "Nova vaga cadastrada para a instituição " + job.getInstitution().getName()
                + ": <a href=\"/job/" + job.getId() + "\"><strong>" + job.getTitle() + "</strong></a>";

            var groupPostDto = new GroupPostDTO(postMessage, job.getAuthor().getId().toString());
            groupFeedService.createGroupPost(organizationId, groupPostDto);
        }

        return job;
    }

    public Job edit( UUID id, UpdateJobDTO updateJobDTO ) {
        var job = findOrThrow( id );
        checkPermissionToEdit( job );

        updateJobDTO.title().ifPresent( title -> {
            job.setTitle( checkValidTitle( title ) );
        } );

        updateJobDTO.shortDescription().ifPresent( shortDescription -> {
            job.setShortDescription( checkValidShortDescription( shortDescription ) );
        } );

        updateJobDTO.longDescription().ifPresent( job::setLongDescription );

        updateJobDTO.requiredCompetencesIds().ifPresent( competenceTypesIds -> {
            job.setRequiredCompetences( competenceTypeService.findByIdOrNameOrThrow( competenceTypesIds ) );
        } );

        return save(job);
    }

    public Job close( @NotNull UUID id ) {
        return close( id, profileService.getProfileInSessionOrThrow() );
    }

    public Job close(@NotNull UUID jobId, @NotNull Profile profile) {
        var job = findOrThrow( jobId );
        checkPermissionToEdit( job );
        job.setClosed(true);

        return save(job);
    }

    private Job save(@NotNull Job job) {
        return jobRepository.saveAndFlush(job);
    }

    @Override
    public boolean hasPermissionToCreate() {
        var user = loginService.getUserInSession();
        return roleService.hasPermission( user.getOrganization(), FeaturesTypes.JOBS, Permission.READ_WRITE );
    }

    @Override
    public boolean hasPermissionToEdit( Job job ) {
        if ( job.isClosed() )
            return false;

        var user = loginService.getUserInSession();
        return job.getAuthor().getId().equals( user.getProfile().getId() )
            || roleService.hasPermission( user.getOrganization(), FeaturesTypes.JOBS, Permission.READ_WRITE );
    }

    @Override
    public boolean hasPermissionToDelete( Job job ) {
        var user = loginService.getUserInSession();
        return job.getAuthor().getId().equals( user.getProfile().getId() )
            || roleService.hasPermission( user.getOrganization(), FeaturesTypes.JOBS, Permission.READ_WRITE_DELETE );
    }

    private String checkValidTitle(@Nullable String title) {
        if (title == null)
            throw new UniversiBadRequestException("Título da vaga não informado.");

        title = title.trim();

        if (title.isBlank())
            throw new UniversiBadRequestException("O título da vaga não pode ser vazio.");

        return title;
    }

    private String checkValidShortDescription(@Nullable String shortDescription) {
        if (shortDescription == null)
            throw new UniversiBadRequestException("Resumo da vaga não informado.");

        shortDescription = shortDescription.trim();

        if (shortDescription.isBlank())
            throw new UniversiBadRequestException("O resumo da vaga não pode ser vazio.");

        if (shortDescription.length() > Job.SHORT_DESCRIPTION_MAX_LENGTH)
            throw new UniversiBadRequestException("O resumo da vaga não pode ter mais de " + Job.SHORT_DESCRIPTION_MAX_LENGTH + " caracteres.");

        return shortDescription;
    }

    public List<Job> findFiltered(boolean onlyOpen, @Nullable List<@NotNull UUID> competenceTypesIds) {
        var filterCompetences = competenceTypesIds != null && !competenceTypesIds.isEmpty();

        return findAll()
            .stream()
            .filter(job -> (!onlyOpen || job.isOpen())
                && ( !filterCompetences || competenceTypesIds
                    .stream()
                    .allMatch(ct -> job.getRequiredCompetences()
                        .stream()
                        .anyMatch(jct -> jct.getId().equals(ct)))
                ))
            .toList();
    }
}
