package me.universi.api.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition( info = @Info(
    title = "Universi.me",
    description = "Universi.me API documentation, with all available endpoints and return types"
) )
@SecurityScheme(
    name = OpenAPIConfig.AUTHORIZATION,
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenAPIConfig {
    public static final String AUTHORIZATION = "BearerAuth";
}
