package me.universi.job.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.services.GroupFeedService;
import me.universi.group.services.GroupService;
import me.universi.institution.services.InstitutionService;
import me.universi.job.entities.Job;
import me.universi.job.exceptions.JobException;
import me.universi.job.repositories.JobRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public static JobService getInstance() { return Sys.context.getBean("jobService", JobService.class); }

    public Optional<Job> findById(@NotNull UUID id) {
        return jobRepository.findById(id);
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public List<Job> findAllOpen() {
        return jobRepository.findAll()
            .stream()
            .filter(j -> !j.isClosed())
            .toList();
    }

    public Job create(@NotNull String title, @NotNull String shortDescription, @NotNull String longDescription, @NotNull UUID institutionId, @NotNull Collection<UUID> requiredCompetencesIds) throws JobException {
        title = checkValidTitle(title);
        shortDescription = checkValidShortDescription(shortDescription);

        var institution = InstitutionService.getInstance().findById(institutionId).orElseThrow(() -> {
            return new JobException("Instituição não encontrada");
        });

        var competencesTypes = CompetenceTypeService.getInstance().findAllById(requiredCompetencesIds);

        var job = new Job();
        job.setTitle(title);
        job.setShortDescription(shortDescription);
        job.setLongDescription(longDescription);
        job.setInstitution(institution);
        job.setRequiredCompetences(competencesTypes);
        job.setClosed(false);
        job.setAuthor(ProfileService.getInstance().getProfileInSession());

        job = save(job);

        var organizationId = UserService.getInstance().getUserInSession().getOrganization().getId().toString();
        // TODO: group environment variable for message
        var postMessage = "Nova vaga cadastrada para a instituição " + job.getInstitution().getName() + ": <strong>" + job.getTitle() + "</strong>";

        var groupPostDto = new GroupPostDTO(postMessage, job.getAuthor().getId().toString());

        GroupFeedService.getInstance().createGroupPost(organizationId, groupPostDto);

        return job;
    }

    public Job edit(@NotNull UUID jobId, String title, String shortDescription, String longDescription, Collection<UUID> requiredCompetencesIds) throws JobException {
        var job = checkCanEdit(jobId, ProfileService.getInstance().getProfileInSession());

        if (title != null)
            job.setTitle( checkValidTitle(title) );

        if (shortDescription != null)
            job.setShortDescription( checkValidShortDescription(shortDescription) );

        if (longDescription != null)
            job.setLongDescription(longDescription);

        if (requiredCompetencesIds != null)
            job.setRequiredCompetences( CompetenceTypeService.getInstance().findAllById(requiredCompetencesIds) );

        return save(job);
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
        var job = findById(jobId).orElseThrow(() -> new JobException("Vaga não encontrada."));

        var canEdit = UserService.getInstance().isUserAdmin(profile.getUser())
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
}
