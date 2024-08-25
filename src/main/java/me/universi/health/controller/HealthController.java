package me.universi.health.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import me.universi.health.dto.HealthResponseDTO;

@RestController
@RequestMapping(
    path = "/api/health",
    consumes = {},
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class HealthController {
    private EntityManager entityManager;

    public HealthController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GetMapping( "/api" )
    public @NotNull HealthResponseDTO apiHealth() {
        // If this is running then API is up
        return new HealthResponseDTO(true, null);
    }

    @GetMapping( "/database" )
    public @NotNull HealthResponseDTO databaseHealth() {
        try {
            boolean open = this.entityManager.isOpen();
            return new HealthResponseDTO(
                open,
                open ? null : "Sessão não está aberta"
            );
        }

        catch (Exception err) {
            return new HealthResponseDTO( false, "Erro ao buscar sessão" );
        }
    }

    @GetMapping( "/mongodb" )
    public @NotNull HealthResponseDTO mongoDbHealth() {
        // todo
        return new HealthResponseDTO(true, null);
    }

    @GetMapping( "/minio" )
    public @NotNull HealthResponseDTO minIoHealth() {
        // todo
        return new HealthResponseDTO(true, null);
    }
}
