package me.universi;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.entities.CompetenciaTipo;
import me.universi.competencia.repositories.CompetenciaTipoRepository;
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

import static org.junit.jupiter.api.Assertions.*;

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

    @Autowired
    CompetenciaTipoRepository competenciaTipoRepository;
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

        CompetenciaTipo compTipo1 = new CompetenciaTipo();
        compTipo1.setNome("testetipo1"+userNew.getId());
        CompetenciaTipo compTipo2 = new CompetenciaTipo();
        compTipo2.setNome("testetipo2"+userNew.getId());
        competenciaTipoRepository.save(compTipo1);
        competenciaTipoRepository.save(compTipo2);

        Competencia competencia1 = new Competencia();
        competencia1.setCompetenciaTipo(compTipo1);
        competencia1.setDescricao("Sou top em java - teste 1"+userNew.getId());
        competenciaService.save(competencia1);

        Competencia competencia2 = new Competencia();
        competencia2.setCompetenciaTipo(compTipo2);
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

        assertEquals(competencia1.getId(), competenciaService.findFirstById(competencia1.getId()).getId());
        assertEquals(competencia2.getId(), competenciaService.findFirstById(competencia2.getId()).getId());
    }
    @Test
    void update(){
        String nome = "competenciaTestUpdate";
        Usuario userNew = new Usuario(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setNome(userNew.getNome());

        CompetenciaTipo compTipo1 = new CompetenciaTipo();
        compTipo1.setNome("testeupdatetipo1"+userNew.getId());
        CompetenciaTipo compTipo2 = new CompetenciaTipo();
        compTipo2.setNome("testeupdatetipo2"+userNew.getId());
        competenciaTipoRepository.save(compTipo1);
        competenciaTipoRepository.save(compTipo2);

        Competencia competencia1 = new Competencia();
        competencia1.setCompetenciaTipo(compTipo1);
        competencia1.setDescricao("Sou top em java - update 1"+userNew.getId());
        competenciaService.save(competencia1);

        Competencia competencia2 = new Competencia();
        competencia2.setCompetenciaTipo(compTipo2);
        competencia2.setDescricao("Sou top em java - update 2"+userNew.getId());
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

        competencia1.setDescricao("alterando descrição1");
        competencia2.setDescricao("alterando descrição2");

        competenciaService.update(competencia1);
        competenciaService.update(competencia2);

        perfilService.update(admin_perfil);
        perfilService.update(comum_perfil);

        assertEquals(competencia1.getDescricao(), competenciaService.findFirstById(competencia1.getId()).getDescricao());
        assertEquals(competencia2.getDescricao(), competenciaService.findFirstById(competencia2.getId()).getDescricao());
    }
    @Test
    void delete(){
        String nome = "competenciaTestDelete";
        Usuario userNew = new Usuario(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setNome(userNew.getNome());

        CompetenciaTipo compTipo1 = new CompetenciaTipo();
        compTipo1.setNome("testedeletetipo1"+userNew.getId());
        CompetenciaTipo compTipo2 = new CompetenciaTipo();
        compTipo2.setNome("testedeletetipo2"+userNew.getId());
        competenciaTipoRepository.save(compTipo1);
        competenciaTipoRepository.save(compTipo2);

        Competencia competencia1 = new Competencia();
        competencia1.setCompetenciaTipo(compTipo1);
        competencia1.setDescricao("Sou top em java - delete 1"+userNew.getId());
        competenciaService.save(competencia1);

        Competencia competencia2 = new Competencia();
        competencia2.setCompetenciaTipo(compTipo2);
        competencia2.setDescricao("Sou top em java - delete 2"+userNew.getId());
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

        competencia1.setDescricao("alterando descrição1");
        competencia2.setDescricao("alterando descrição2");

        perfilService.update(admin_perfil);
        perfilService.update(comum_perfil);

        competenciaService.deleteAll(competencias);



        assertNull(competenciaService.findFirstById(competencia1.getId()));
        assertNull(competenciaService.findFirstById(competencia2.getId()));
    }
    @Test
    void read(){
        assertTrue(true);
    }
}
