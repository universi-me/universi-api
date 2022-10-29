package me.universi.competencia.controller;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.enums.Nivel;
import me.universi.competencia.repositories.CompetenciaRepository;
import me.universi.grupo.entities.Grupo;
import me.universi.grupo.repositories.GrupoRepository;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.enums.Autoridade;
import me.universi.usuario.services.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CompetenciaController {

    @Autowired
    public CompetenciaRepository competenciaRepository;

    // http://localhost:8080/competencia/criar?nome=teste&descricao=teste2&nivel=NENHUMA_EXPERIENCIA
    @RequestMapping("/competencia/criar")
    public String create(@RequestParam("nome") String nome, @RequestParam("descricao") String descricao, @RequestParam("nivel") Nivel nivel) {
        Competencia competenciaNew = new Competencia(nome, descricao, nivel);
        competenciaRepository.save(competenciaNew);
        return "Competencia Criada: " + competenciaNew.toString();
    }

    // http://localhost:8080/competencia/atualizar?id=3&nome=teste&descricao=teste2&nivel=NENHUMA_EXPERIENCIA
    @RequestMapping("/competencia/atualizar")
    public String update(@RequestParam("id") Long id, @RequestParam("nome") String nome, @RequestParam("descricao") String descricao, @RequestParam("nivel") Nivel nivel) {
        Competencia comp,compOld;
        try {
            comp = competenciaRepository.findById(id).get();
            compOld = competenciaRepository.findById(id).get();
            if (comp != null) { // verifica se a competencia existe
                Boolean nivelOk = false;
                for (Nivel n : Nivel.values()) { // verifica se nivel existe
                    if (nivel.equals(n)){
                        nivelOk = true; // nivel existe
                        break; // sai do loop
                    }
                }

                if(nivelOk)
                    comp.setNivel(nivel);

                if(nome != null)
                    comp.setNome(nome);

                if (descricao != null)
                    comp.setDescricao(descricao);

                competenciaRepository.save(comp);
            }
        } catch (EntityNotFoundException e) {
            return "Competencia não encontrada";
        }
        if(comp.equals(compOld))
            return "Competencia não foi modificada pelo usuario" + comp.toString();

        return "Competencia atualizada: " + comp.toString();
    }


    // http://localhost:8080/competencia/remover?id=1
    @RequestMapping("/competencia/remover")
    public String remove(@RequestParam("id") Long id) {
        try {
            Competencia comp = competenciaRepository.findById(id).get();
            if (comp != null) {
                competenciaRepository.delete(comp);
                return "Competencia removida: " + comp.toString();
            }
        } catch (EntityNotFoundException e) {
            return "Competencia não encontrada";
        }
        return "Falha ao remover";
    }

    // http://localhost:8080/competencia/obter?id=1
    @RequestMapping("/competencia/obter")
    public Competencia get(@RequestParam("id") Long id) {
        try {
            Competencia comp = competenciaRepository.findById(id).get();
            return comp;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    // http://localhost:8080/competencia/listar
    @RequestMapping("/competencia/listar")
    public List<Competencia> getlist() {
        List<Competencia> comps = competenciaRepository.findAll();
        return comps;
    }
}
