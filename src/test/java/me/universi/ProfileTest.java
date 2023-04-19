package me.universi;

import me.universi.competencia.entities.Competence;
import me.universi.competencia.services.CompetenceService;
import me.universi.grupo.services.GroupService;
import me.universi.perfil.entities.Profile;
import me.universi.perfil.enums.Gender;
import me.universi.perfil.services.PerfilService;
import me.universi.recomendacao.service.RecomendacaoService;
import me.universi.user.entities.User;
import me.universi.user.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ProfileTest {

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



    @Test
    void create() {
        Profile profile = perfil("testeCreate");
        assertEquals("testeCreate", profile.getFirstname());
    }
    @Test
    void update(){
        Profile profile = perfil("testeUpdate");
        assertEquals("testeUpdate", profile.getFirstname());

        profile.setFirstname("nomeAtualizado");
        perfilService.update(profile);
        assertEquals("nomeAtualizado", profile.getFirstname());
    }
    @Test
    void delete(){
        Profile profile = perfil("testeDelete");
        Long id = profile.getId();
        assertEquals(profile.getId(), perfilService.findFirstById(id).getId());
        perfilService.delete(profile);
        assertEquals(null,perfilService.findFirstById(id));
    }
    @Test
    void read(){
        perfilService.deleteAll();
        assertEquals(0,perfilService.findAll().size());
        for (int i = 0; i < 10; i++) {
            Profile profile = perfil("nome"+i);
        }
        Collection<Profile> perfis = perfilService.findAll();
        assertEquals(10, perfis.size());

    }

    public Profile perfil(String nome) {
        User userNew = new User(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        userNew.setName(userNew.getName());

        Competence competenciaNew = new Competence();
        //competenciaNew.setNome("Java - admin"+userNew.getId());
        competenciaNew.setDescription("Sou top em java - admin"+userNew.getId());
        competenciaService.save(competenciaNew);

        Competence competenciaNew1 = new Competence();
        //competenciaNew1.setNome("Java - admin 1"+userNew.getId());
        competenciaNew1.setDescription("Sou top em java - admin 1"+userNew.getId());
        competenciaService.save(competenciaNew1);

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname(nome);
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setSexo(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competenciaNew);
        competencias.add(competenciaNew1);
        admin_profile.setCompetences(competencias);

        return admin_profile;
    }
}