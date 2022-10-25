package me.universi;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.repositories.CompetenciaRepository;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.repositories.UsuarioRepository;
import me.universi.perfil.repositories.PerfilRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ImportResource({"classpath:spring-security.xml"})
@RestController
public class Sys{

    @Autowired
    public PerfilRepository perfilRepository;
    @Autowired
    public UsuarioRepository usuarioRepository;
    @Autowired
    public CompetenciaRepository competenciaRepository;


    public static void main(String [] args)
    {
        System.out.println("H2 ativo na http://localhost:8080/h2-console");
        SpringApplication.run(Sys.class, args);
    }

    @GetMapping("/")
    String hello() {

        Usuario userNew = new Usuario("User name", "test@email.com", "senha");
        usuarioRepository.save(userNew);
        Competencia competenciaNew = new Competencia("Java","Sou top em java");
        competenciaRepository.save(competenciaNew);



        return "Ola Mundo!";
    }
}