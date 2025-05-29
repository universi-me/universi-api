package me.universi;

import me.universi.competence.dto.CreateCompetenceDTO;
import me.universi.competence.dto.UpdateCompetenceDTO;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 *  Classe básica de teste, precisa de ajustes e limpeza do código
 */
@SpringBootTest
@ActiveProfiles("test")
class CompetenciaTest {

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
        String nome = "competenceTest";
        User userNew = new User(nome, nome+"@email.com", accountService.encodePassword("senha"));
        userService.createUser( userNew, null, null, null );
        var profile = userNew.getProfile();

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("teste tipo 1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("teste tipo 2"+userNew.getId());
        competenceTypeRepository.save(compTipo1);
        competenceTypeRepository.save(compTipo2);

        var competence1 = competenceService.create(
            new CreateCompetenceDTO(
                compTipo1.getId().toString(),
                "Sou top em java - teste 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        var competence2 = competenceService.create(
            new CreateCompetenceDTO(
                compTipo2.getId().toString(),
                "Sou top em java - teste 2"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        Profile adminProfile = userNew.getProfile();
        adminProfile.setFirstname("perfil1");
        adminProfile.setBio("Bio - admin_perfil"+userNew.getId());
        adminProfile.setGender(Gender.M);

        Profile commonProfile = userNew.getProfile();
        commonProfile.setFirstname("perfil2");
        commonProfile.setBio("Bio - comum_perfil"+userNew.getId());
        commonProfile.setGender(Gender.M);

        assertEquals( competence1.getId(), competenceService.findOrThrow(competence1.getId()).getId() );
        assertEquals( competence2.getId(), competenceService.findOrThrow(competence2.getId()).getId() );
    }
    @Test
    void update() throws Exception {
        String nome = "competenceTestUpdate";
        User userNew = new User(nome, nome+"@email.com", accountService.encodePassword("senha"));
        userService.createUser( userNew, null, null, null );
        Profile profile = userNew.getProfile();

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("teste update tipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("teste update tipo2"+userNew.getId());
        competenceTypeRepository.save(compTipo1);
        competenceTypeRepository.save(compTipo2);

        var competence1 = competenceService.create(
            new CreateCompetenceDTO(
                compTipo1.getId().toString(),
                "Sou top em java - update 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        var competence2 = competenceService.create(
            new CreateCompetenceDTO(
                compTipo2.getId().toString(),
                "Sou top em java - update 2"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        Profile adminProfile = userNew.getProfile();
        adminProfile.setFirstname("perfil1");
        adminProfile.setBio("Bio - admin_perfil"+userNew.getId());
        adminProfile.setGender(Gender.M);

        Profile commonProfile = userNew.getProfile();
        commonProfile.setFirstname("perfil2");
        commonProfile.setBio("Bio - comum_perfil"+userNew.getId());
        commonProfile.setGender(Gender.M);

        competence1.setDescription("alterando descrição1");
        competence2.setDescription("alterando descrição2");

        competenceService.update(
            competence1.getId(),
            new UpdateCompetenceDTO(
                null,
                "alterando descrição1",
                null
            )
        );

        competenceService.update(
            competence1.getId(),
            new UpdateCompetenceDTO(
                null,
                "alterando descrição2",
                null
            )
        );

        assertEquals(competence1.getDescription(), competenceService.findOrThrow(competence1.getId()).getDescription());
        assertEquals(competence2.getDescription(), competenceService.findOrThrow(competence2.getId()).getDescription());
    }
    @Test
    void delete() throws Exception {
        String nome = "competenceTestDelete";
        User userNew = new User(nome, nome+"@email.com", accountService.encodePassword("senha"));
        userService.createUser( userNew, null, null, null );
        var profile = userNew.getProfile();

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("teste delete tipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("teste delete tipo2"+userNew.getId());
        competenceTypeRepository.save(compTipo1);
        competenceTypeRepository.save(compTipo2);

        var competence1 = competenceService.create(
            new CreateCompetenceDTO(
                compTipo1.getId().toString(),
                "Sou top em java - delete 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        var competence2 = competenceService.create(
            new CreateCompetenceDTO(
                compTipo2.getId().toString(),
                "Sou top em java - delete 2"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        Profile adminProfile = userNew.getProfile();
        adminProfile.setFirstname("perfil1");
        adminProfile.setBio("Bio - admin_perfil"+userNew.getId());
        adminProfile.setGender(Gender.M);

        Profile comumProfile = userNew.getProfile();
        comumProfile.setFirstname("perfil2");
        comumProfile.setBio("Bio - comum_perfil"+userNew.getId());
        comumProfile.setGender(Gender.M);

        competenceService.delete( competence1.getId() );
        competenceService.delete( competence2.getId() );

        assertTrue( competenceService.find( competence1.getId() ).isEmpty() );
        assertTrue( competenceService.find( competence2.getId() ).isEmpty() );
    }

    @Test
    void read() throws Exception {
        String nome = "competenceTestRead";
        User userNew = new User(nome, nome+"@email.com", accountService.encodePassword("senha"));
        userService.createUser( userNew, null, null, null );
        var profile = userNew.getProfile();

        CompetenceType compTipo1 = new CompetenceType();
        compTipo1.setName("teste read tipo1"+userNew.getId());
        CompetenceType compTipo2 = new CompetenceType();
        compTipo2.setName("teste read tipo2"+userNew.getId());
        competenceTypeRepository.save(compTipo1);
        competenceTypeRepository.save(compTipo2);

        competenceService.create(
            new CreateCompetenceDTO(
                compTipo1.getId().toString(),
                "Sou top em java - read 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        competenceService.create(
            new CreateCompetenceDTO(
                compTipo2.getId().toString(),
                "Sou top em java - read 2"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        Profile adminProfile = userNew.getProfile();
        adminProfile.setFirstname("perfil1");
        adminProfile.setBio("Bio - admin_perfil"+userNew.getId());
        adminProfile.setGender(Gender.M);

        Profile commonProfile = userNew.getProfile();
        commonProfile.setFirstname("perfil2");
        commonProfile.setBio("Bio - comum_perfil"+userNew.getId());
        commonProfile.setGender(Gender.M);

        assertTrue(competenceService.findAll().size() >= 2);

    }
}
