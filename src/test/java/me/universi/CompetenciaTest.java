package me.universi;

import me.universi.competencia.entities.Competence;
import me.universi.competencia.entities.CompetenceType;
import me.universi.competencia.repositories.CompetenceTypeRepository;
import me.universi.competencia.services.CompetenceService;
import me.universi.grupo.services.GroupService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.enums.Sexo;
import me.universi.perfil.services.PerfilService;
import me.universi.recomendacao.service.RecomendacaoService;
import me.universi.user.entities.User;
import me.universi.user.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  Classe básica de teste, precisa de ajustes e limpeza do código
 */
@SpringBootTest
public class CompetenciaTest {

    @Autowired
    GroupService grupoService;
    @Autowired
    CompetenceService competenciaService;
    @Autowired
    RecomendacaoService recomendacaoService;
    @Autowired
    PerfilService perfilService;
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    CompetenceTypeRepository competenciaTipoRepository;
    @Test
    void create() {
        String nome = "competenciaTest";
        User userNew = new User(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setNome(userNew.getNome());

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("testetipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("testetipo2"+userNew.getId());
        competenciaTipoRepository.save(compTipo1);
        competenciaTipoRepository.save(compTipo2);

        Competence competencia1 = new Competence();
        competencia1.setCompetenceType(compTipo1);
        competencia1.setDescription("Sou top em java - teste 1"+userNew.getId());
        competenciaService.save(competencia1);

        Competence competencia2 = new Competence();
        competencia2.setCompetenceType(compTipo2);
        competencia2.setDescription("Sou top em java - teste 2"+userNew.getId());
        competenciaService.save(competencia2);

        Perfil admin_perfil = userNew.getPerfil();
        admin_perfil.setNome("perfil1");
        admin_perfil.setBio("Bio - admin_perfil"+userNew.getId());
        admin_perfil.setSexo(Sexo.M);

        Perfil comum_perfil = userNew.getPerfil();
        comum_perfil.setNome("perfil2");
        comum_perfil.setBio("Bio - comum_perfil"+userNew.getId());
        comum_perfil.setSexo(Sexo.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
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
        User userNew = new User(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setNome(userNew.getNome());

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("testeupdatetipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("testeupdatetipo2"+userNew.getId());
        competenciaTipoRepository.save(compTipo1);
        competenciaTipoRepository.save(compTipo2);

        Competence competencia1 = new Competence();
        competencia1.setCompetenceType(compTipo1);
        competencia1.setDescription("Sou top em java - update 1"+userNew.getId());
        competenciaService.save(competencia1);

        Competence competencia2 = new Competence();
        competencia2.setCompetenceType(compTipo2);
        competencia2.setDescription("Sou top em java - update 2"+userNew.getId());
        competenciaService.save(competencia2);

        Perfil admin_perfil = userNew.getPerfil();
        admin_perfil.setNome("perfil1");
        admin_perfil.setBio("Bio - admin_perfil"+userNew.getId());
        admin_perfil.setSexo(Sexo.M);

        Perfil comum_perfil = userNew.getPerfil();
        comum_perfil.setNome("perfil2");
        comum_perfil.setBio("Bio - comum_perfil"+userNew.getId());
        comum_perfil.setSexo(Sexo.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        admin_perfil.setCompetencias(competencias);
        comum_perfil.setCompetencias(competencias);

        competenciaService.save(competencia1);
        competenciaService.save(competencia2);

        competencia1.setDescription("alterando descrição1");
        competencia2.setDescription("alterando descrição2");

        competenciaService.update(competencia1);
        competenciaService.update(competencia2);

        perfilService.update(admin_perfil);
        perfilService.update(comum_perfil);

        assertEquals(competencia1.getDescription(), competenciaService.findFirstById(competencia1.getId()).getDescription());
        assertEquals(competencia2.getDescription(), competenciaService.findFirstById(competencia2.getId()).getDescription());
    }
    @Test
    void delete(){
        String nome = "competenciaTestDelete";
        User userNew = new User(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setNome(userNew.getNome());

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("testedeletetipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("testedeletetipo2"+userNew.getId());
        competenciaTipoRepository.save(compTipo1);
        competenciaTipoRepository.save(compTipo2);

        Competence competencia1 = new Competence();
        competencia1.setCompetenceType(compTipo1);
        competencia1.setDescription("Sou top em java - delete 1"+userNew.getId());
        competenciaService.save(competencia1);

        Competence competencia2 = new Competence();
        competencia2.setCompetenceType(compTipo2);
        competencia2.setDescription("Sou top em java - delete 2"+userNew.getId());
        competenciaService.save(competencia2);

        Perfil admin_perfil = userNew.getPerfil();
        admin_perfil.setNome("perfil1");
        admin_perfil.setBio("Bio - admin_perfil"+userNew.getId());
        admin_perfil.setSexo(Sexo.M);

        Perfil comum_perfil = userNew.getPerfil();
        comum_perfil.setNome("perfil2");
        comum_perfil.setBio("Bio - comum_perfil"+userNew.getId());
        comum_perfil.setSexo(Sexo.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        admin_perfil.setCompetencias(competencias);
        comum_perfil.setCompetencias(competencias);

        competenciaService.save(competencia1);
        competenciaService.save(competencia2);

        competencia1.setDescription("alterando descrição1");
        competencia2.setDescription("alterando descrição2");

        perfilService.update(admin_perfil);
        perfilService.update(comum_perfil);

        competenciaService.deleteAll(competencias);



        assertNull(competenciaService.findFirstById(competencia1.getId()));
        assertNull(competenciaService.findFirstById(competencia2.getId()));
    }
    @Test
    void read(){
        String nome = "competenciaTestread";
        User userNew = new User(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setNome(userNew.getNome());

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("testereadtipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("testereadtipo2"+userNew.getId());
        competenciaTipoRepository.save(compTipo1);
        competenciaTipoRepository.save(compTipo2);

        Competence competencia1 = new Competence();
        competencia1.setCompetenceType(compTipo1);
        competencia1.setDescription("Sou top em java - read 1"+userNew.getId());
        competenciaService.save(competencia1);

        Competence competencia2 = new Competence();
        competencia2.setCompetenceType(compTipo2);
        competencia2.setDescription("Sou top em java - read 2"+userNew.getId());
        competenciaService.save(competencia2);

        Perfil admin_perfil = userNew.getPerfil();
        admin_perfil.setNome("perfil1");
        admin_perfil.setBio("Bio - admin_perfil"+userNew.getId());
        admin_perfil.setSexo(Sexo.M);

        Perfil comum_perfil = userNew.getPerfil();
        comum_perfil.setNome("perfil2");
        comum_perfil.setBio("Bio - comum_perfil"+userNew.getId());
        comum_perfil.setSexo(Sexo.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        admin_perfil.setCompetencias(competencias);
        comum_perfil.setCompetencias(competencias);

        competenciaService.save(competencia1);
        competenciaService.save(competencia2);


        assertTrue(competenciaService.findAll().size() >= 2);

    }
}
