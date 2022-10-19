package br.ufpb.universiapi;

import br.ufpb.universiapi.entities.Usuario;
import br.ufpb.universiapi.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class }) // temp desabilitar SpringSecurity login para funcionar o H2 console
@RestController
public class Sys{
    @Autowired
    public UsuarioRepository usuarioRepository;

    public static void main(String [] args)
    {
        System.out.println("H2 ativo na http://localhost:8080/h2-console");
        SpringApplication.run(Sys.class, args);
    }

    @GetMapping("/")
    String hello() {

        Usuario userNew = new Usuario("User name", "test@email.com", "senha");
        usuarioRepository.save(userNew);

        return "Ola Mundo!";
    }
}
