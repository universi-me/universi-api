package me.universi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import me.universi.user.entities.User;

import me.universi.user.enums.Autoridade;
import me.universi.user.services.UsuarioService;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@SpringBootApplication
@ImportResource({"classpath:spring-security.xml"})
@Controller
public class Sys {

    @Autowired
    public UsuarioService usuarioService;
    @Autowired
    private ApplicationContext context;


    public static void main(String [] args) {
        SpringApplication.run(Sys.class, args);
    }

    @GetMapping("/exit")
    public void exitApp() {
        int exitCode = SpringApplication.exit(context, (ExitCodeGenerator) () -> 0);
        System.exit(exitCode);
    }

    @GetMapping("/")
    String index(HttpServletRequest request, HttpSession session) {
        return "Universi.me API";
    }

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            // Criar usuario Admin padrão, obs: alterar senha depois.
            if(!usuarioService.usernameExiste("admin")) {
                System.out.println("Criando usuário: admin:admin");
                User userAdmin = new User("admin", null, usuarioService.codificarSenha("admin"));
                try {
                    usuarioService.createUser(userAdmin);
                    userAdmin.setAutoridade(Autoridade.ROLE_ADMIN);
                    usuarioService.save(userAdmin);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
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