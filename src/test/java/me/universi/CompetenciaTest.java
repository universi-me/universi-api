package me.universi;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.repositories.CompetenceTypeRepository;
import me.universi.competence.services.CompetenceService;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.enums.Gender;
import me.universi.profile.services.PerfilService;
import me.universi.recommendation.service.RecomendacaoService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
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
    UserService userService;

    @Autowired
    CompetenceTypeRepository competenciaTipoRepository;
    @Test
    void create() {
        String nome = "competenciaTest";
        User userNew = new User(nome, nome+"@email.com", userService.codificarSenha("senha"));
        try {
            userService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setName(userNew.getName());

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

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname("perfil1");
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setSexo(Gender.M);

        Profile comum_profile = userNew.getProfile();
        comum_profile.setFirstname("perfil2");
        comum_profile.setBio("Bio - comum_perfil"+userNew.getId());
        comum_profile.setSexo(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        admin_profile.setCompetences(competencias);
        comum_profile.setCompetences(competencias);

        competenciaService.save(competencia1);
        competenciaService.save(competencia2);

        perfilService.update(admin_profile);
        perfilService.update(comum_profile);

        assertEquals(competencia1.getId(), competenciaService.findFirstById(competencia1.getId()).getId());
        assertEquals(competencia2.getId(), competenciaService.findFirstById(competencia2.getId()).getId());
    }
    @Test
    void update(){
        String nome = "competenciaTestUpdate";
        User userNew = new User(nome, nome+"@email.com", userService.codificarSenha("senha"));
        try {
            userService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setName(userNew.getName());

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

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname("perfil1");
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setSexo(Gender.M);

        Profile comum_profile = userNew.getProfile();
        comum_profile.setFirstname("perfil2");
        comum_profile.setBio("Bio - comum_perfil"+userNew.getId());
        comum_profile.setSexo(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        admin_profile.setCompetences(competencias);
        comum_profile.setCompetences(competencias);

        competenciaService.save(competencia1);
        competenciaService.save(competencia2);

        competencia1.setDescription("alterando descrição1");
        competencia2.setDescription("alterando descrição2");

        competenciaService.update(competencia1);
        competenciaService.update(competencia2);

        perfilService.update(admin_profile);
        perfilService.update(comum_profile);

        assertEquals(competencia1.getDescription(), competenciaService.findFirstById(competencia1.getId()).getDescription());
        assertEquals(competencia2.getDescription(), competenciaService.findFirstById(competencia2.getId()).getDescription());
    }
    @Test
    void delete(){
        String nome = "competenciaTestDelete";
        User userNew = new User(nome, nome+"@email.com", userService.codificarSenha("senha"));
        try {
            userService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setName(userNew.getName());

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

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname("perfil1");
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setSexo(Gender.M);

        Profile comum_profile = userNew.getProfile();
        comum_profile.setFirstname("perfil2");
        comum_profile.setBio("Bio - comum_perfil"+userNew.getId());
        comum_profile.setSexo(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        admin_profile.setCompetences(competencias);
        comum_profile.setCompetences(competencias);

        competenciaService.save(competencia1);
        competenciaService.save(competencia2);

        competencia1.setDescription("alterando descrição1");
        competencia2.setDescription("alterando descrição2");

        perfilService.update(admin_profile);
        perfilService.update(comum_profile);

        competenciaService.deleteAll(competencias);



        assertNull(competenciaService.findFirstById(competencia1.getId()));
        assertNull(competenciaService.findFirstById(competencia2.getId()));
    }
    @Test
    void read(){
        String nome = "competenciaTestread";
        User userNew = new User(nome, nome+"@email.com", userService.codificarSenha("senha"));
        try {
            userService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userNew.setName(userNew.getName());

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

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname("perfil1");
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setSexo(Gender.M);

        Profile comum_profile = userNew.getProfile();
        comum_profile.setFirstname("perfil2");
        comum_profile.setBio("Bio - comum_perfil"+userNew.getId());
        comum_profile.setSexo(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        admin_profile.setCompetences(competencias);
        comum_profile.setCompetences(competencias);

        competenciaService.save(competencia1);
        competenciaService.save(competencia2);


        assertTrue(competenciaService.findAll().size() >= 2);

    }
}
