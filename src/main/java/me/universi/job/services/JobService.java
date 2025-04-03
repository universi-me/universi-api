package me.universi.job.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.services.GroupFeedService;
import me.universi.institution.services.InstitutionService;
import me.universi.job.dto.CreateJobDTO;
import me.universi.job.dto.UpdateJobDTO;
import me.universi.job.entities.Job;
import me.universi.job.exceptions.JobException;
import me.universi.job.repositories.JobRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.services.RoleService;
import me.universi.user.services.UserService;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final CompetenceTypeService competenceTypeService;
    private final GroupFeedService groupFeedService;
    private final InstitutionService institutionService;
    private final ProfileService profileService;
    private final RoleService roleService;
    private final UserService userService;

    public JobService( JobRepository jobRepository, CompetenceTypeService competenceTypeService, GroupFeedService groupFeedService, InstitutionService institutionService, ProfileService profileService, RoleService roleService, UserService userService ) {
        this.jobRepository = jobRepository;
        this.competenceTypeService = competenceTypeService;
        this.groupFeedService = groupFeedService;
        this.institutionService = institutionService;
        this.profileService = profileService;
        this.roleService = roleService;
        this.userService = userService;
    }

    public static JobService getInstance() { return Sys.context.getBean("jobService", JobService.class); }

    public Optional<Job> find( @NotNull UUID id ) {
        return jobRepository.findById(id);
    }

    public @NotNull Job findOrThrow( @NotNull UUID id ) throws EntityNotFoundException {
        // TODO: UniversiEntityNotFoundException
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Vaga de ID '" + id + "' não encontrada" ) );
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public Job create( CreateJobDTO createJobDTO ) throws JobException {
        var title = checkValidTitle( createJobDTO.title() );
        var shortDescription = checkValidShortDescription( createJobDTO.shortDescription() );

        var institution = institutionService.findOrThrow( createJobDTO.institutionId() );

        var competencesTypes = competenceTypeService.findOrThrow( createJobDTO.requiredCompetencesIds() );

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

    public Job edit( UUID id, UpdateJobDTO updateJobDTO ) throws JobException {
        var job = checkCanEdit( id, profileService.getProfileInSessionOrThrow() );

        if ( updateJobDTO.title() != null )
            job.setTitle( checkValidTitle( updateJobDTO.title() ) );

        if ( updateJobDTO.shortDescription() != null )
            job.setShortDescription( checkValidShortDescription( updateJobDTO.shortDescription() ) );

        if ( updateJobDTO.longDescription() != null)
            job.setLongDescription( updateJobDTO.longDescription() );

        if ( updateJobDTO.requiredCompetencesIds() != null)
            job.setRequiredCompetences( competenceTypeService.findOrThrow( updateJobDTO.requiredCompetencesIds() ) );

        return save(job);
    }

    public Job close( @NotNull UUID id ) {
        return close( id, profileService.getProfileInSessionOrThrow() );
    }

    public Job close(@NotNull UUID jobId, @NotNull Profile profile) throws JobException {
        var job = checkCanEdit(jobId, profile);
        job.setClosed(true);

        return save(job);
    }

    private Job save(@NotNull Job job) {
        return jobRepository.saveAndFlush(job);
    }

    private Job checkCanEdit(@NotNull UUID jobId, @NotNull Profile profile) throws JobException {
        var job = findOrThrow(jobId);

        var canEdit = userService.isUserAdmin(profile.getUser())
            || job.getAuthor().getId().equals(profile.getId());

        if (!canEdit)
            throw new JobException("O perfil '" + profile.getFirstname() + " " + profile.getLastname() + "' não tem permissão para alterar esta vaga.");

        if (job.isClosed())
            throw new JobException("Uma vaga fechada não pode ser alterada.");

        return job;
    }

    private String checkValidTitle(@Nullable String title) throws JobException {
        if (title == null)
            throw new JobException("Título da vaga não informado.");

        title = title.trim();

        if (title.isBlank())
            throw new JobException("O título da vaga não pode ser vazio.");

        return title;
    }

    private String checkValidShortDescription(@Nullable String shortDescription) throws JobException {
        if (shortDescription == null)
            throw new JobException("Resumo da vaga não informado.");

        shortDescription = shortDescription.trim();

        if (shortDescription.isBlank())
            throw new JobException("O resumo da vaga não pode ser vazio.");

        if (shortDescription.length() > Job.SHORT_DESCRIPTION_MAX_LENGTH)
            throw new JobException("O resumo da vaga não pode ter mais de " + Job.SHORT_DESCRIPTION_MAX_LENGTH + " caracteres.");

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
