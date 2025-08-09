package me.universi.api.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition( info = @Info(
    title = "Universi.me",
    description = "Universi.me API documentation, with all available endpoints and return types"
) )
public class OpenAPIConfig {
}
