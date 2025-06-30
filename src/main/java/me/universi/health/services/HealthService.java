package me.universi.health.services;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import java.util.Map;
import me.universi.health.dto.UsageResponseDTO;
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

    private static final String DATABASE_SERVICE_ID = "DATABASE";
    private static final String MONGODB_SERVICE_ID = "MONGODB";
    private static final String MINIO_SERVICE_ID = "MINIO";


    public HealthService(
        EntityManager entityManager,
        MongoTemplate mongoTemplate
    ) {
        this.entityManager = entityManager;
        this.mongoTemplate = mongoTemplate;
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
        return new HealthResponseDTO(true, false, "API", null, null);
    }

    public @NotNull HealthResponseDTO databaseHealth() {
        try {
            boolean open = this.entityManager.isOpen();
            if (!open)
                return new HealthResponseDTO( false, false, DATABASE_SERVICE_ID, "Nenhuma sessão aberta", null);

            var session = entityManager.unwrap(Session.class);
            session.doWork( new Work() {
                @Override public void execute( Connection connection ) throws SQLException {
                    connection.createStatement().execute("SELECT 1");
                }
            });

            return new HealthResponseDTO( true, false, DATABASE_SERVICE_ID, null , null);
        }

        catch (Exception err) {
            return new HealthResponseDTO( false, false, DATABASE_SERVICE_ID, "Erro ao buscar sessão" , err.getMessage());
        }
    }

    public @NotNull HealthResponseDTO mongoDbHealth() {
        try {
            var db = this.mongoTemplate.getDb();
            db.runCommand( new Document().append("ping", 1) );

            return new HealthResponseDTO(true, false, MONGODB_SERVICE_ID, null, null);
        } catch (Exception e) {
            return new HealthResponseDTO(false, false, MONGODB_SERVICE_ID, "Base de dados MongoDB inacessível", e.getMessage());
        }
    }

    public @NotNull HealthResponseDTO minIoHealth() {
        if (!MinioConfig.isMinioEnabled())
            return new HealthResponseDTO(false, true, MINIO_SERVICE_ID, "Serviço desativado", null);

        try {
            var response = RestClient.builder( )
                .baseUrl( MinioConfig.getInstance().getUrl() )
                .build( )
                .get( )
                .uri("/minio/health/cluster")
                .retrieve( )
                .toEntity(String.class);

            boolean reached = response.getStatusCode().value() == 200;

            return new HealthResponseDTO(
                reached, false, MINIO_SERVICE_ID,
                reached ? null : "Serviço inacessível",
                    null
            );
        }

        catch (Exception e) {
            return new HealthResponseDTO(false, false, MINIO_SERVICE_ID, "Serviço offline", e.getMessage());
        }
    }

    public boolean isUp( HealthResponseDTO health ) {
        return health.isUp() || health.isDisabled();
    }

    public UsageResponseDTO getProcessUsage() {

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        UsageResponseDTO usageResponseDTO = new UsageResponseDTO();
        usageResponseDTO.systemCpuLoad = osBean.getSystemCpuLoad();
        usageResponseDTO.memoryMax = Runtime.getRuntime().maxMemory();
        usageResponseDTO.totalMemory = Runtime.getRuntime().totalMemory();
        usageResponseDTO.freeMemory = Runtime.getRuntime().freeMemory();

        return usageResponseDTO;
    }
}
