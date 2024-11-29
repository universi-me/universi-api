package me.universi.health.controller;

import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;
import me.universi.api.entities.Response;
import me.universi.health.dto.HealthResponseDTO;
import me.universi.health.services.HealthService;

@RestController
@RequestMapping(
    path = "/api/health",
    consumes = {},
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class HealthController {
    private HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping( "/all" )
    public @NotNull Response allHealth() {
        return Response.buildResponse(response -> {
            var healths = healthService.allHealth();
            var servicesDown = healths.stream().filter(h -> !(h.isUp() || (!h.isUp() && h.isDisabled()))).toList();
            response.success = servicesDown.isEmpty();

            response.status = response.success
                ? HttpStatus.SC_OK
                : HttpStatus.SC_SERVICE_UNAVAILABLE;

            response.body.put(
                "status",
                healths.stream().collect(
                    Collectors.toMap( h -> h.getName(), h -> h )
                )
            );
        });
    }

    @GetMapping( "/api" )
    public @NotNull Response apiHealth() {
        return this.responseFromHealthDTO(this.healthService.apiHealth());
    }

    @GetMapping( "/database" )
    public @NotNull Response databaseHealth() {
        return this.responseFromHealthDTO(this.healthService.databaseHealth());
    }

    @GetMapping( "/mongodb" )
    public @NotNull Response mongoDbHealth() {
        return this.responseFromHealthDTO(this.healthService.mongoDbHealth());
    }

    @GetMapping( "/minio" )
    public @NotNull Response minIoHealth() {
        return this.responseFromHealthDTO(this.healthService.minIoHealth());
    }

    private @NotNull Response responseFromHealthDTO(@NotNull HealthResponseDTO health) {
        return Response.buildResponse(response -> {
            response.success = health.isUp();

            response.status = response.success
                ? HttpStatus.SC_OK
                : HttpStatus.SC_SERVICE_UNAVAILABLE;

            response.body.put( "status", health );
        });
    }
}
