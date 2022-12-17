package me.universi;

import me.universi.competencia.services.CompetenciaService;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.grupo.services.GrupoService;
import me.universi.link.services.LinkService;
import me.universi.perfil.services.PerfilService;
import me.universi.recomendacao.service.RecomendacaoService;
import me.universi.usuario.entities.Usuario;

import me.universi.usuario.enums.Autoridade;
import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@SpringBootApplication
@ImportResource({"classpath:spring-security.xml"})
@Controller
public class Sys {

    @Autowired
    public PerfilService perfilService;
    @Autowired
    public UsuarioService usuarioService;
    @Autowired
    public CompetenciaService competenciaService;
    @Autowired
    public CompetenciaTipoService competenciaTipoService;
    @Autowired
    public GrupoService grupoService;
    @Autowired
    public RecomendacaoService recomendacaoService;
    @Autowired
    public LinkService linkService;
    @Autowired
    private ApplicationContext context;


    public static void main(String [] args) {
        System.out.println("H2 ativo na http://localhost:8080/h2-console");
        SpringApplication.run(Sys.class, args);
    }

    @GetMapping("/exit")
    public void exitApp() {
        int exitCode = SpringApplication.exit(context, (ExitCodeGenerator) () -> 0);
        System.exit(exitCode);
    }

    @GetMapping("/")
    String index(HttpServletRequest request, HttpSession session, ModelMap map) {
        return "landing/landing";
    }

    @GetMapping("/equipe")
    String equipe(HttpServletRequest request, HttpSession session, ModelMap map) {
        return "landing/equipe";
    }

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            // Criar usuario Admin padrão, obs: alterar senha depois.
            if(!usuarioService.usernameExiste("admin")) {
                System.out.println("Criando usuário: admin:admin");
                Usuario userAdmin = new Usuario("admin", null, usuarioService.codificarSenha("admin"));
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
}