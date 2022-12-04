package me.universi;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.services.CompetenciaService;
import me.universi.grupo.entities.Grupo;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.usuario.entities.Usuario;
import me.universi.perfil.repositories.PerfilRepository;

import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;

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
    public GrupoService grupoService;

    public static void main(String [] args) {
        System.out.println("H2 ativo na http://localhost:8080/h2-console");
        SpringApplication.run(Sys.class, args);
    }

    @GetMapping("/")
    String index(HttpServletRequest request, HttpSession session, ModelMap map) {
        return "index";
    }


}