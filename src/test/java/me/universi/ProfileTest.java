package me.universi;

import me.universi.competence.dto.CreateCompetenceDTO;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.repositories.CompetenceTypeRepository;
import me.universi.competence.services.CompetenceService;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.enums.Gender;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class ProfileTest {

    @Autowired
    GroupService grupoService;
    @Autowired
    CompetenceService competenceService;

    @Autowired
    ProfileService profileService;
    @Autowired
    UserService userService;

    @Autowired
    CompetenceTypeRepository competenceTypeRepository;

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
        Profile adminProfile = new Profile();
        adminProfile.setFirstname(nome);
        adminProfile.setBio("Bio - admin_perfil"+userNew.getId());
        adminProfile.setGender(Gender.M);
        adminProfile.setUser(userNew);
        userNew.setProfile(adminProfile);
        profileService.save(userNew.getProfile());
        try {
            userService.createUser(userNew, null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("teste read tipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("teste read tipo2"+userNew.getId());
        competenceTypeRepository.save(compTipo1);
        competenceTypeRepository.save(compTipo2);

        competenceService.create(
            new CreateCompetenceDTO(
                compTipo1.getId(),
                "Sou top em java - admin"+userNew.getId(),
                Competence.MIN_LEVEL
            ), adminProfile
        );

        competenceService.create(
            new CreateCompetenceDTO(
                compTipo1.getId(),
                "Sou top em java - admin 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), adminProfile
        );

        return adminProfile;
    }
}
