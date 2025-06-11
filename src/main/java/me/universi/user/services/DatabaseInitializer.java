package me.universi.user.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class initializes the database by creating it if it does not already exist.
 * Auto creation of the database is done using Flyway's configuration customizer.
 */
@Configuration
public class DatabaseInitializer {

    @Value("${database.name}")
    private String databaseName;
    @Value("${database.url}")
    private String databaseUrl;
    @Value("${database.username}")
    private String databaseUsername;
    @Value("${database.password}")
    private String databasePassword;

    @Bean
    public FlywayConfigurationCustomizer customFlywayConfig() {
        return configuration -> {
            // connect to default database for create the target database
            try (Connection conn = DriverManager.getConnection(databaseUrl.replaceAll("\\b" + "/" + databaseName + "\\b$", "/") , databaseUsername, databasePassword)) {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("CREATE DATABASE " + databaseName);
                System.out.println("+ Database created: " + databaseName);
            } catch (SQLException e) {
                if (!(e.getMessage().contains("already exists") || "42P04".equals(e.getSQLState()))) {
                    throw new RuntimeException("+ Failed to create database: " + databaseName, e);
                }
            }
        };
    }
}
