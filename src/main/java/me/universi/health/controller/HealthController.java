package me.universi.health.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.minio.MinioClient;
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
    private MongoTemplate mongoTemplate;
    private MinioClient minioClient;

    public HealthController(
        EntityManager entityManager,
        MongoTemplate mongoTemplate,
        @Autowired( required = false ) MinioClient minioClient
    ) {
        this.entityManager = entityManager;
        this.mongoTemplate = mongoTemplate;
        this.minioClient = minioClient;
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
        boolean clientExists = this.minioClient != null;

        return new HealthResponseDTO(
            clientExists,
            clientExists ? null : "Servidor inativo"
        );
    }
}
