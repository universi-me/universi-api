package me.universi.user.services;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = { "me.universi.feed", }) // Configure here base package that use MongoDB
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${database.mongo.name}")
    public String databaseName;

    @Value("${database.mongo.uri}")
    public String mongoUri;

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(new ConnectionString(mongoUri));
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

}
