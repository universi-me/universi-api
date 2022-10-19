package me.universi.projeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class }) // temp desabilitar SpringSecurity login para funcionar o H2 console
public class TestProjeto {
    public static void main(String [] args)
    {
        System.out.println("H2 ativo na http://localhost:8080/h2-console");
        SpringApplication.run(TestProjeto.class, args);
    }
}
