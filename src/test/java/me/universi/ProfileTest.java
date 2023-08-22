package me.universi;

import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.repositories.CompetenceTypeRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
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

    @Autowired
    CompetenceTypeRepository competenciaTipoRepository;

    @Test
    void create() throws Exception {
        Profile profile = perfil("testeCreate");
        assertEquals("testeCreate", profile.getFirstname());
    }
    @Test
    void update() throws Exception {
        Profile profile = perfil("testeUpdate");
        assertEquals("testeUpdate", profile.getFirstname());

        profile.setFirstname("nomeAtualizado");
        profileService.update(profile);
        assertEquals("nomeAtualizado", profile.getFirstname());
    }
    @Test
    void delete() throws Exception {
        Profile profile = perfil("testeDelete");
        UUID id = profile.getId();
        assertEquals(profile.getId(), profileService.findFirstById(id).getId());
        profileService.delete(profile);
        assertEquals(null, profileService.findFirstById(id));
    }
    @Test
    void read() throws Exception {
        profileService.deleteAll();
        assertEquals(0, profileService.findAll().size());
        for (int i = 0; i < 10; i++) {
            perfil("nome"+i);
        }
        Collection<Profile> perfis = profileService.findAll();
        assertEquals(10, perfis.size());

    }

    public Profile perfil(String nome) throws Exception {
        User userNew = new User(nome, nome+"@email.com", userService.encodePassword("senha"));
        Profile admin_profile = new Profile();
        admin_profile.setFirstname(nome);
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setGender(Gender.M);
        admin_profile.setUser(userNew);
        userNew.setProfile(admin_profile);
        profileService.save(userNew.getProfile());
        try {
            userService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("testereadtipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("testereadtipo2"+userNew.getId());
        competenciaTipoRepository.save(compTipo1);
        competenciaTipoRepository.save(compTipo2);

        Competence competenciaNew = new Competence();
        competenciaNew.setProfile(admin_profile);
        competenciaNew.setCompetenceType(compTipo1);
        competenciaNew.setTitle("Java - admin"+userNew.getId());
        competenciaNew.setDescription("Sou top em java - admin"+userNew.getId());
        competenciaService.save(competenciaNew);

        Competence competenciaNew1 = new Competence();
        competenciaNew1.setProfile(admin_profile);
        competenciaNew1.setCompetenceType(compTipo2);
        competenciaNew1.setTitle("Java - admin 1"+userNew.getId());
        competenciaNew1.setDescription("Sou top em java - admin 1"+userNew.getId());
        competenciaService.save(competenciaNew1);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competenciaNew);
        competencias.add(competenciaNew1);
        admin_profile.setCompetences(competencias);

        return admin_profile;
    }
}
