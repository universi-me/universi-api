package me.universi;

import me.universi.competence.entities.Competence;
import me.universi.competence.services.CompetenceService;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.enums.Gender;
import me.universi.profile.services.ProfileService;
import me.universi.recommendation.service.RecomendacaoService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
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
    ProfileService profileService;
    @Autowired
    UserService userService;



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
        profileService.update(profile);
        assertEquals("nomeAtualizado", profile.getFirstname());
    }
    @Test
    void delete(){
        Profile profile = perfil("testeDelete");
        Long id = profile.getId();
        assertEquals(profile.getId(), profileService.findFirstById(id).getId());
        profileService.delete(profile);
        assertEquals(null, profileService.findFirstById(id));
    }
    @Test
    void read(){
        profileService.deleteAll();
        assertEquals(0, profileService.findAll().size());
        for (int i = 0; i < 10; i++) {
            Profile profile = perfil("nome"+i);
        }
        Collection<Profile> perfis = profileService.findAll();
        assertEquals(10, perfis.size());

    }

    public Profile perfil(String nome) {
        User userNew = new User(nome, nome+"@email.com", userService.encodePassword("senha"));
        try {
            userService.createUser(userNew);
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
        admin_profile.setGender(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competenciaNew);
        competencias.add(competenciaNew1);
        admin_profile.setCompetences(competencias);

        return admin_profile;
    }
}
