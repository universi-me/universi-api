package me.universi;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.repositories.CompetenciaRepository;
import me.universi.grupo.entities.Grupo;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
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
    public PerfilRepository perfilRepository;
    @Autowired
    public UsuarioService usuarioService;
    @Autowired
    public CompetenciaRepository competenciaRepository;
    @Autowired
    public GrupoService grupoService;

    public static void main(String [] args) {
        System.out.println("H2 ativo na http://localhost/h2-console");
        SpringApplication.run(Sys.class, args);
    }

    @GetMapping("/")
    String index(HttpServletRequest request, HttpSession session, ModelMap map) {
        return "index";
    }

    public Perfil random_perfil(String nome) {
        Usuario userNew = new Usuario(nome, "test@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        userNew.setNome(userNew.getNome()+"_"+userNew.getId());

        Competencia competenciaNew = new Competencia();
        competenciaNew.setNome("Java - admin"+userNew.getId());
        competenciaNew.setDescricao("Sou top em java - admin"+userNew.getId());
        competenciaRepository.save(competenciaNew);

        Competencia competenciaNew1 = new Competencia();
        competenciaNew1.setNome("Java - admin 1"+userNew.getId());
        competenciaNew1.setDescricao("Sou top em java - admin 1"+userNew.getId());
        competenciaRepository.save(competenciaNew1);

        Perfil admin_perfil = new Perfil();
        admin_perfil.setUsuario(userNew);
        admin_perfil.setBio("Bio - admin_perfil"+userNew.getId());

        Collection<Competencia> competencias = new ArrayList<Competencia>();
        competencias.add(competenciaNew);
        competencias.add(competenciaNew1);
        admin_perfil.setCompetencias(competencias);


        return admin_perfil;
    }

    @GetMapping("/popular")
    String popular() throws GrupoException {
        Grupo ufpb_grupo = grupoService.findFirstByNickname("ufpb");

        if(ufpb_grupo != null) {
            return "redirect:/";
        }

        Perfil admin_perfil = random_perfil("perfil_admin");
        perfilRepository.save(admin_perfil);

        Perfil perfil_1 = random_perfil("perfil_1");
        perfilRepository.save(perfil_1);

        Perfil perfil_2 = random_perfil("perfil_2");
        perfilRepository.save(perfil_2);

        Grupo novoGrupo = new Grupo();
        novoGrupo.setNickname("ufpb");
        novoGrupo.setTipo(GrupoTipo.INSTITUICAO);
        novoGrupo.setGrupoRoot(true);
        novoGrupo.setAdmin(admin_perfil);
        novoGrupo.setDescricao("Grupo da Instituição da UFPB");
        novoGrupo.setNome("UFPB");

        grupoService.adicionarParticipante(novoGrupo, perfil_1);
        grupoService.adicionarParticipante(novoGrupo, perfil_2);

        Grupo novoGrupo2 = new Grupo();
        novoGrupo2.setTipo(GrupoTipo.CAMPUS);
        novoGrupo2.setNickname("campus4");
        novoGrupo2.setAdmin(admin_perfil);
        novoGrupo2.setDescricao("Grupo do Campus IV");
        novoGrupo2.setNome("Campus IV");

        grupoService.adicionarSubgrupo(novoGrupo, novoGrupo2);

        return "redirect:/";
    }
}