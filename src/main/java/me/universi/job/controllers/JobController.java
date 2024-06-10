package me.universi.job.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.universi.api.entities.Response;
import me.universi.job.exceptions.JobException;
import me.universi.job.services.JobService;
import me.universi.profile.services.ProfileService;
import me.universi.util.CastingUtil;

@RestController
@RequestMapping("/api/job")
public class JobController {
    private final JobService jobService;

    public JobController (JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var jobId = CastingUtil.getUUID(body.get("jobId")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new JobException("Parâmetro 'jobId' inválido ou não informado.");
            });

            var job = jobService.findById(jobId).orElseThrow(() -> {
                response.setStatus(HttpStatus.NOT_FOUND);
                return new JobException("Vaga não encontrada.");
            });

            response.body.put("job", job);
        });
    }

    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response listAll(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("list", jobService.findAll());
        });
    }

    @PostMapping(value = "/list-open", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response listOpen(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("list", jobService.findAllOpen());
        });
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var title = CastingUtil.getString(body.get("title")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new JobException("Parâmetro 'title' inválido ou não informado.");
            });

            var shortDescription = CastingUtil.getString(body.get("shortDescription")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new JobException("Parâmetro 'shortDescription' inválido ou não informado.");
            });

            var longDescription = CastingUtil.getString(body.get("longDescription")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new JobException("Parâmetro 'longDescription' inválido ou não informado.");
            });

            var institutionId = CastingUtil.getUUID(body.get("institutionId")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new JobException("Parâmetro 'institutionId' inválido ou não informado.");
            });

            var requiredCompetencesIds = CastingUtil.getList(body.get("requiredCompetencesIds")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new JobException("Parâmetro 'requiredCompetencesIds' inválido ou não informado.");
            }).stream().map(id -> {
                return CastingUtil.getUUID(id).orElseThrow(() -> {
                    response.setStatus(HttpStatus.BAD_REQUEST);
                    return new JobException("Id de competência '" + id + "' inválido");
                });
            }).toList();

            var job = jobService.create(
                title,
                shortDescription,
                longDescription,
                institutionId,
                requiredCompetencesIds
            );

            response.setStatus(HttpStatus.CREATED);
            response.message = "Vaga criada com sucesso.";
            response.body.put("job", job);
        });
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response update(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var jobId = CastingUtil.getUUID(body.get("jobId")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new JobException("Parâmetro 'jobId' inválido ou não informado.");
            });

            var title = CastingUtil.getString(body.get("title")).orElse(null);
            var shortDescription = CastingUtil.getString(body.get("shortDescription")).orElse(null);
            var longDescription = CastingUtil.getString(body.get("longDescription")).orElse(null);

            var requiredCompetencesObjs = CastingUtil.getList(body.get("requiredCompetencesIds")).orElse(null);
            var requiredCompetencesIds = requiredCompetencesObjs == null
                ? null
                : requiredCompetencesObjs.stream().map(id -> {
                    return CastingUtil.getUUID(id).orElseThrow(() -> {
                        response.setStatus(HttpStatus.BAD_REQUEST);
                        return new JobException("Id de competência '" + id + "' inválido");
                    });
                }).toList();

            var job = jobService.edit(
                jobId,
                title,
                shortDescription,
                longDescription,
                requiredCompetencesIds
            );

            response.setStatus(HttpStatus.CREATED);
            response.message = "Vaga atualizada com sucesso.";
            response.body.put("job", job);
        });
    }

    @PostMapping(value = "/close", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response close(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            var jobId = CastingUtil.getUUID(body.get("jobId")).orElseThrow(() -> {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return new JobException("Parâmetro 'jobId' inválido ou não informado.");
            });

            var job = jobService.close(jobId, ProfileService.getInstance().getProfileInSession());

            response.body.put("job", job);
        });
    }
}
