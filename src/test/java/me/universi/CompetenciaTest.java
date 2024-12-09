package me.universi;

import me.universi.competence.dto.CreateCompetenceDTO;
import me.universi.competence.dto.UpdateCompetenceDTO;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.repositories.CompetenceTypeRepository;
import me.universi.competence.services.CompetenceProfileService;
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

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  Classe básica de teste, precisa de ajustes e limpeza do código
 */
@SpringBootTest
@ActiveProfiles("test")
public class CompetenciaTest {

    @Autowired
    GroupService grupoService;
    @Autowired
    CompetenceService competenciaService;
    @Autowired
    ProfileService profileService;
    @Autowired
    UserService userService;

    @Autowired
    CompetenceTypeRepository competenciaTipoRepository;

    @Autowired
    CompetenceProfileService competenceProfileService;
    @Test
    void create() throws Exception {
        String nome = "competenciaTest";
        User userNew = new User(nome, nome+"@email.com", userService.encodePassword("senha"));
        Profile profile = new Profile();
        profile.setGender(Gender.M);
        profileService.save(profile);
        userNew.setProfile(profile);
        try {
            userService.createUser(userNew, null, null);
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

        var competencia1 = competenciaService.create(
            new CreateCompetenceDTO(
                compTipo1.getId(),
                "Sou top em java - teste 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        var competencia2 = competenciaService.create(
            new CreateCompetenceDTO(
                compTipo2.getId(),
                "Sou top em java - teste 2"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname("perfil1");
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setGender(Gender.M);

        Profile comum_profile = userNew.getProfile();
        comum_profile.setFirstname("perfil2");
        comum_profile.setBio("Bio - comum_perfil"+userNew.getId());
        comum_profile.setGender(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        competenceProfileService.addToProfile(admin_profile, competencias);
        competenceProfileService.addToProfile(comum_profile, competencias);

        profileService.update(admin_profile);
        profileService.update(comum_profile);

        assertEquals( competencia1.getId(), competenciaService.findOrThrow(competencia1.getId()).getId() );
        assertEquals( competencia2.getId(), competenciaService.findOrThrow(competencia2.getId()).getId() );
    }
    @Test
    void update() throws Exception {
        String nome = "competenciaTestUpdate";
        User userNew = new User(nome, nome+"@email.com", userService.encodePassword("senha"));
        Profile profile = new Profile();
        profile.setGender(Gender.M);
        profileService.save(profile);
        userNew.setProfile(profile);
        try {
            userService.createUser(userNew, null, null);
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

        var competencia1 = competenciaService.create(
            new CreateCompetenceDTO(
                compTipo1.getId(),
                "Sou top em java - update 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        var competencia2 = competenciaService.create(
            new CreateCompetenceDTO(
                compTipo2.getId(),
                "Sou top em java - update 2"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname("perfil1");
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setGender(Gender.M);

        Profile comum_profile = userNew.getProfile();
        comum_profile.setFirstname("perfil2");
        comum_profile.setBio("Bio - comum_perfil"+userNew.getId());
        comum_profile.setGender(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        competenceProfileService.addToProfile(admin_profile, competencias);
        competenceProfileService.addToProfile(comum_profile, competencias);

        competencia1.setDescription("alterando descrição1");
        competencia2.setDescription("alterando descrição2");

        competenciaService.update(
            competencia1.getId(),
            new UpdateCompetenceDTO(
                null,
                "alterando descrição1",
                null
            )
        );

        competenciaService.update(
            competencia1.getId(),
            new UpdateCompetenceDTO(
                null,
                "alterando descrição2",
                null
            )
        );

        profileService.update(admin_profile);
        profileService.update(comum_profile);

        assertEquals(competencia1.getDescription(), competenciaService.findOrThrow(competencia1.getId()).getDescription());
        assertEquals(competencia2.getDescription(), competenciaService.findOrThrow(competencia2.getId()).getDescription());
    }
    @Test
    void delete() throws Exception {
        String nome = "competenciaTestDelete";
        User userNew = new User(nome, nome+"@email.com", userService.encodePassword("senha"));
        Profile profile = new Profile();
        profile.setGender(Gender.M);
        profileService.save(profile);
        userNew.setProfile(profile);
        try {
            userService.createUser(userNew, null, null);
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

        var competencia1 = competenciaService.create(
            new CreateCompetenceDTO(
                compTipo1.getId(),
                "Sou top em java - delete 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        var competencia2 = competenciaService.create(
            new CreateCompetenceDTO(
                compTipo2.getId(),
                "Sou top em java - delete 2"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname("perfil1");
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setGender(Gender.M);

        Profile comum_profile = userNew.getProfile();
        comum_profile.setFirstname("perfil2");
        comum_profile.setBio("Bio - comum_perfil"+userNew.getId());
        comum_profile.setGender(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        competenceProfileService.addToProfile(admin_profile, competencias);
        competenceProfileService.addToProfile(comum_profile, competencias);

        competenciaService.delete( competencia1.getId() );
        competenciaService.delete( competencia2.getId() );

        assertTrue( competenciaService.find( competencia1.getId() ).isEmpty() );
        assertTrue( competenciaService.find( competencia2.getId() ).isEmpty() );
    }

    @Test
    void read() throws Exception {
        String nome = "competenciaTestread";
        User userNew = new User(nome, nome+"@email.com", userService.encodePassword("senha"));
        Profile profile = new Profile();
        profile.setGender(Gender.M);
        profileService.save(profile);
        userNew.setProfile(profile);
        try {
            userService.createUser(userNew, null, null);
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

        var competencia1 = competenciaService.create(
            new CreateCompetenceDTO(
                compTipo1.getId(),
                "Sou top em java - read 1"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        var competencia2 = competenciaService.create(
            new CreateCompetenceDTO(
                compTipo2.getId(),
                "Sou top em java - read 2"+userNew.getId(),
                Competence.MIN_LEVEL
            ), profile
        );

        Profile admin_profile = userNew.getProfile();
        admin_profile.setFirstname("perfil1");
        admin_profile.setBio("Bio - admin_perfil"+userNew.getId());
        admin_profile.setGender(Gender.M);

        Profile comum_profile = userNew.getProfile();
        comum_profile.setFirstname("perfil2");
        comum_profile.setBio("Bio - comum_perfil"+userNew.getId());
        comum_profile.setGender(Gender.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competencia1);
        competencias.add(competencia2);
        competenceProfileService.addToProfile(admin_profile, competencias);
        competenceProfileService.addToProfile(comum_profile, competencias);

        assertTrue(competenciaService.findAll().size() >= 2);

    }
}
