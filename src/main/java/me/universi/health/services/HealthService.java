package me.universi.health.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import io.jsonwebtoken.lang.Arrays;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import me.universi.health.dto.HealthResponseDTO;
import me.universi.minioConfig.MinioConfig;

@Service
public class HealthService {
    private EntityManager entityManager;
    private MongoTemplate mongoTemplate;
    private MinioConfig minioConfig;

    public HealthService(
        EntityManager entityManager,
        MongoTemplate mongoTemplate,
        MinioConfig minioConfig
    ) {
        this.entityManager = entityManager;
        this.mongoTemplate = mongoTemplate;
        this.minioConfig = minioConfig;
    }

    public @NotNull List<@NotNull HealthResponseDTO> allHealth() {
        HealthResponseDTO[] healthArray = {
            apiHealth(),
            databaseHealth(),
            mongoDbHealth(),
            minIoHealth(),
        };

        return Arrays.asList(healthArray);
    }

    public @NotNull HealthResponseDTO apiHealth() {
        // If this is running then API is up
        return new HealthResponseDTO(true, "API", null);
    }

    private static final String DATABASE_SERVICE_ID = "DATABASE";
    public @NotNull HealthResponseDTO databaseHealth() {
        try {
            boolean open = this.entityManager.isOpen();
            if (!open)
                return new HealthResponseDTO( false, DATABASE_SERVICE_ID, "Nenhuma sessão aberta" );

            var session = entityManager.unwrap(Session.class);
            session.doWork( new Work() {
                @Override public void execute( Connection connection ) throws SQLException {
                    connection.createStatement().execute("SELECT 1");
                }
            });

            return new HealthResponseDTO( true, DATABASE_SERVICE_ID, null );
        }

        catch (Exception err) {
            return new HealthResponseDTO( false, DATABASE_SERVICE_ID, "Erro ao buscar sessão" );
        }
    }

    private static final String MONGODB_SERVICE_ID = "MONGODB";
    public @NotNull HealthResponseDTO mongoDbHealth() {
        try {
            var db = this.mongoTemplate.getDb();
            db.runCommand( new Document().append("ping", 1) );

            return new HealthResponseDTO(true, MONGODB_SERVICE_ID, null);
        } catch (Exception e) {
            return new HealthResponseDTO(false, MONGODB_SERVICE_ID, "Base de dados MongoDB inacessível");
        }
    }

    private static final String MINIO_SERVICE_ID = "MINIO";
    public @NotNull HealthResponseDTO minIoHealth() {
        if (!this.minioConfig.enabled)
            return new HealthResponseDTO(false, MINIO_SERVICE_ID, "Serviço desativado");

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
                reached, MINIO_SERVICE_ID,
                reached ? null : "Serviço inacessível"
            );
        }

        catch (Exception e) {
            return new HealthResponseDTO(false, MINIO_SERVICE_ID, "Serviço offline");
        }
    }
}
