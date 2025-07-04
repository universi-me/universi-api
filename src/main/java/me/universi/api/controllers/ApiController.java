package me.universi.api.controllers;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import me.universi.Sys;
import me.universi.group.services.OrganizationService;
import me.universi.user.services.EnvironmentService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {
    @GetMapping("/admin/exit")
    public void exitApp() {
        int exitCode = SpringApplication.exit(Sys.context(), (ExitCodeGenerator) () -> 0);
        System.exit(exitCode);
    }

    @GetMapping(value = {"/", "",})
    @ResponseBody
    String index() {
        EnvironmentService envService = Sys.context().getBean("environmentService", EnvironmentService.class);
        return "Universi.me API" + " – " + envService.activeProfile + " / " + envService.BUILD_HASH;
    }

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            OrganizationService.getInstance().setup();
        };
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Universi.me API").version(appVersion)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
