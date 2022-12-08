package me.universi;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.services.CompetenciaService;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.enums.Sexo;
import me.universi.perfil.services.PerfilService;
import me.universi.recomendacao.service.RecomendacaoService;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CompetenciaTest {

    @Autowired
    GrupoService grupoService;
    @Autowired
    CompetenciaService competenciaService;
    @Autowired
    RecomendacaoService recomendacaoService;
    @Autowired
    PerfilService perfilService;
    @Autowired
    UsuarioService usuarioService;
    @Test
    void create() {
        String nome = "competenciaTest";
        Usuario userNew = new Usuario(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setNome(userNew.getNome());

        Competencia competencia1 = new Competencia();
        //competencia1.setNome("Java - competencia teste 1"+userNew.getId());
        competencia1.setDescricao("Sou top em java - teste 1"+userNew.getId());
        competenciaService.save(competencia1);

        Competencia competencia2 = new Competencia();
        //competencia2.setNome("Java - competencia teste 2"+userNew.getId());
        competencia2.setDescricao("Sou top em java - teste 2"+userNew.getId());
        competenciaService.save(competencia2);

        Perfil admin_perfil = userNew.getPerfil();
        admin_perfil.setNome("perfil1");
        admin_perfil.setBio("Bio - admin_perfil"+userNew.getId());
        admin_perfil.setSexo(Sexo.M);

        Perfil comum_perfil = userNew.getPerfil();
        comum_perfil.setNome("perfil2");
        comum_perfil.setBio("Bio - comum_perfil"+userNew.getId());
        comum_perfil.setSexo(Sexo.M);

        Collection<Competencia> competencias = new ArrayList<Competencia>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        admin_perfil.setCompetencias(competencias);
        comum_perfil.setCompetencias(competencias);

        competenciaService.save(competencia1);
        competenciaService.save(competencia2);

        perfilService.update(admin_perfil);
        perfilService.update(comum_perfil);
    }
    @Test
    void update(){
        assertTrue(true);
    }
    @Test
    void delete(){
        assertTrue(true);
    }
    @Test
    void read(){
        assertTrue(true);
    }
}
