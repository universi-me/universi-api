package me.universi;

import me.universi.competencia.entities.Competence;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PerfilTest {

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
        Perfil perfil = perfil("testeCreate");
        assertEquals("testeCreate", perfil.getNome());
    }
    @Test
    void update(){
        Perfil perfil = perfil("testeUpdate");
        assertEquals("testeUpdate", perfil.getNome());

        perfil.setNome("nomeAtualizado");
        perfilService.update(perfil);
        assertEquals("nomeAtualizado", perfil.getNome());
    }
    @Test
    void delete(){
        Perfil perfil = perfil("testeDelete");
        Long id = perfil.getId();
        assertEquals(perfil.getId(), perfilService.findFirstById(id).getId());
        perfilService.delete(perfil);
        assertEquals(null,perfilService.findFirstById(id));
    }
    @Test
    void read(){
        perfilService.deleteAll();
        assertEquals(0,perfilService.findAll().size());
        for (int i = 0; i < 10; i++) {
            Perfil perfil = perfil("nome"+i);
        }
        Collection<Perfil> perfis = perfilService.findAll();
        assertEquals(10, perfis.size());

    }

    public Perfil perfil(String nome) {
        User userNew = new User(nome, nome+"@email.com", usuarioService.codificarSenha("senha"));
        try {
            usuarioService.createUser(userNew);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        userNew.setNome(userNew.getNome());

        Competence competenciaNew = new Competence();
        //competenciaNew.setNome("Java - admin"+userNew.getId());
        competenciaNew.setDescription("Sou top em java - admin"+userNew.getId());
        competenciaService.save(competenciaNew);

        Competence competenciaNew1 = new Competence();
        //competenciaNew1.setNome("Java - admin 1"+userNew.getId());
        competenciaNew1.setDescription("Sou top em java - admin 1"+userNew.getId());
        competenciaService.save(competenciaNew1);

        Perfil admin_perfil = userNew.getPerfil();
        admin_perfil.setNome(nome);
        admin_perfil.setBio("Bio - admin_perfil"+userNew.getId());
        admin_perfil.setSexo(Sexo.M);

        Collection<Competence> competencias = new ArrayList<Competence>();
        competencias.add(competenciaNew);
        competencias.add(competenciaNew1);
        admin_perfil.setCompetencias(competencias);

        return admin_perfil;
    }
}
