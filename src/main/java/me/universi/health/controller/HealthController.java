package me.universi.health.controller;

import java.sql.Connection;
import java.sql.SQLException;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import io.jsonwebtoken.lang.Arrays;
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

    @GetMapping( "/all" )
    public @NotNull HealthResponseDTO allHealth() {
        HealthResponseDTO[] healthArray = {
            apiHealth(),
            databaseHealth(),
            mongoDbHealth(),
            minIoHealth(),
        };

        var healths = Arrays.asList(healthArray);
        var servicesDown = healths.stream().filter(h -> !h.isUp()).toList();

        var allOk = servicesDown.isEmpty();
        String message = null;

        if (!allOk) {
            message = String.join( "; ",
                servicesDown
                .stream()
                .map(h -> h.getName() + ": " + h.getMessage())
                .toList()
            );
        }

        return new HealthResponseDTO( allOk, "Todos os Serviços", message );
    }

    @GetMapping( "/api" )
    public @NotNull HealthResponseDTO apiHealth() {
        // If this is running then API is up
        return new HealthResponseDTO(true, "API", null);
    }

    private static final String DATABASE_SERVICE_NAME = "Base de Dados";
    @GetMapping( "/database" )
    public @NotNull HealthResponseDTO databaseHealth() {
        try {
            boolean open = this.entityManager.isOpen();
            if (!open)
                return new HealthResponseDTO( false, DATABASE_SERVICE_NAME, "Nenhuma sessão aberta" );

            var session = entityManager.unwrap(Session.class);
            session.doWork( new Work() {
                @Override public void execute( Connection connection ) throws SQLException {
                    connection.createStatement().execute("SELECT 1");
                }
            });

            return new HealthResponseDTO( true, DATABASE_SERVICE_NAME, null );
        }

        catch (Exception err) {
            return new HealthResponseDTO( false, DATABASE_SERVICE_NAME, "Erro ao buscar sessão" );
        }
    }
    
    private static final String MONGODB_SERVICE_NAME = "MongoDB";
    @GetMapping( "/mongodb" )
    public @NotNull HealthResponseDTO mongoDbHealth() {
        try {
            var db = this.mongoTemplate.getDb();
            db.runCommand( new Document().append("ping", 1) );

            return new HealthResponseDTO(true, MONGODB_SERVICE_NAME, null);
        } catch (Exception e) {
            return new HealthResponseDTO(false, MONGODB_SERVICE_NAME, "Base de dados MongoDB inacessível");
        }
    }

    private static final String MINIO_SERVICE_NAME = "MinIO";
    @GetMapping( "/minio" )
    public @NotNull HealthResponseDTO minIoHealth() {
        if (!this.minioConfig.enabled)
            return new HealthResponseDTO(false, MINIO_SERVICE_NAME, "Serviço desativado");

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
                reached, MINIO_SERVICE_NAME,
                reached ? null : "Serviço inacessível"
            );
        }

        catch (Exception e) {
            return new HealthResponseDTO(false, MINIO_SERVICE_NAME, "Serviço offline");
        }
    }
}
