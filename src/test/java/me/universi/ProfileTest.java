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
import me.universi.user.services.AccountService;
import me.universi.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.Optional;
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
    @Autowired
    private AccountService accountService;

    @Test
    void create() throws Exception {
        Profile profile = perfil("testeCreate");
        assertEquals("testeCreate", profile.getFirstname());
    }
    @Test
    void delete() throws Exception {
        Profile profile = perfil("testeDelete");
        UUID id = profile.getId();
        assertEquals(profile.getId(), profileService.findOrThrow(id).getId());
        profileService.delete( id.toString() );
        assertEquals(Optional.empty(), profileService.find(id));
    }
    @Test
    void read() throws Exception {
        var previousSize = profileService.findAll().size();

        for (int i = 0; i < 10; i++) {
            perfil("nome"+i);
        }

        Collection<Profile> perfis = profileService.findAll();
        assertEquals( previousSize + 10, perfis.size() );

    }

    public Profile perfil(String nome) throws Exception {
        User userNew = new User(nome, nome+"@email.com", accountService.encodePassword("senha"));
        userService.createUser( userNew, null, null, null );
        var adminProfile = userNew.getProfile();

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("teste read tipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("teste read tipo2"+userNew.getId());
        competenceTypeRepository.save(compTipo1);
        competenceTypeRepository.save(compTipo2);

        competenceService.create(
            new CreateCompetenceDTO(
                compTipo1.getId().toString(),
                "Sou top em java - admin"+userNew.getId(),
                Competence.MIN_LEVEL
            ), adminProfile
        );

        competenceService.create(
            new CreateCompetenceDTO(
                compTipo1.getId().toString(),
                "Sou top em java - admin 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), adminProfile
        );

        return adminProfile;
    }
}
