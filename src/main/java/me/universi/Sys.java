package me.universi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import me.universi.group.services.OrganizationService;

import me.universi.user.services.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@ImportResource({"classpath:spring-security.xml"})
@Controller
public class Sys {

    @Value("${spring.profiles.active}")
    private String PROFILE_ACTIVE;
    @Autowired
    public UserService userService;
    @Autowired
    private OrganizationService organizationService;
    public static ApplicationContext context;


    public static void main(String [] args) {
        context = SpringApplication.run(Sys.class, args);
    }

    @GetMapping("/admin/exit")
    public void exitApp() {
        int exitCode = SpringApplication.exit(context, (ExitCodeGenerator) () -> 0);
        System.exit(exitCode);
    }

    @GetMapping(value = {"/", "",})
    @ResponseBody
    String index() {
        return "Universi.me API – " + PROFILE_ACTIVE.toUpperCase();
    }

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            organizationService.setup();
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