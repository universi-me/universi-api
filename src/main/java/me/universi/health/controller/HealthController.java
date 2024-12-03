package me.universi.health.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;
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

    @GetMapping( "" )
    public @NotNull ResponseEntity<Map<String, HealthResponseDTO>> allHealth() {
        var healths = healthService.allHealth();

        return new ResponseEntity<>(
            mapMultipleResponses( healths ),
            healths.stream().allMatch( h -> healthService.isUp( h ) )
                ? HttpStatus.OK
                : HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @GetMapping( "/api" )
    public @NotNull ResponseEntity<HealthResponseDTO> apiHealth() {
        return this.responseFromHealthDTO(this.healthService.apiHealth());
    }

    @GetMapping( "/database" )
    public @NotNull ResponseEntity<HealthResponseDTO> databaseHealth() {
        return this.responseFromHealthDTO(this.healthService.databaseHealth());
    }

    @GetMapping( "/mongodb" )
    public @NotNull ResponseEntity<HealthResponseDTO> mongoDbHealth() {
        return this.responseFromHealthDTO(this.healthService.mongoDbHealth());
    }

    @GetMapping( "/minio" )
    public @NotNull ResponseEntity<HealthResponseDTO> minIoHealth() {
        return this.responseFromHealthDTO(this.healthService.minIoHealth());
    }

    private @NotNull ResponseEntity<HealthResponseDTO> responseFromHealthDTO(@NotNull HealthResponseDTO health) {
        return new ResponseEntity<>(
            health,
            healthService.isUp( health )
                ? HttpStatus.OK
                : HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    private Map<String, HealthResponseDTO> mapMultipleResponses( @NotNull List<HealthResponseDTO> healths ) {
        return healths.stream().collect(
            Collectors.toMap( h -> h.getName(), h -> h )
        );
    }
}
