package me.universi.health.controller;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import me.universi.health.dto.HealthResponseDTO;
import me.universi.minioConfig.MinioConfig;

@RestController
@RequestMapping(
    path = "/api/health",
    consumes = {},
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class HealthController {
    private EntityManager entityManager;
    private MongoTemplate mongoTemplate;
    private MinioConfig minioConfig;

    public HealthController(
        EntityManager entityManager,
        MongoTemplate mongoTemplate,
        MinioConfig minioConfig
    ) {
        this.entityManager = entityManager;
        this.mongoTemplate = mongoTemplate;
        this.minioConfig = minioConfig;
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
        try {
            var db = this.mongoTemplate.getDb();
            if (db == null) throw new Exception();

            return new HealthResponseDTO(true, null);
        } catch (Exception e) {
            return new HealthResponseDTO(false, "Base de dados MongoDB inacessível");
        }
    }

    @GetMapping( "/minio" )
    public @NotNull HealthResponseDTO minIoHealth() {
        if (!this.minioConfig.enabled)
            return new HealthResponseDTO(false, "Serviço desativado");

        try {
            var response = RestClient.builder( )
                .baseUrl( minioConfig.getUrl() )
                .build( )
                .get( )
                .uri("/minio/health/cluster")
                .retrieve( )
                .toEntity(String.class);

            boolean reached = response.getStatusCode().value() == 200;

            return new HealthResponseDTO(
                reached,
                reached ? null : "Serviço inacessível"
            );
        }

        catch (Exception e) {
            return new HealthResponseDTO(false, "Serviço offline");
        }
    }
}
