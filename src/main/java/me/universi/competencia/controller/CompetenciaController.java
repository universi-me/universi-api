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

    // http://localhost:80/competencia/criar?nome=teste&descricao=teste2&nivel=NENHUMA_EXPERIENCIA
    @RequestMapping("/competencia/criar")
    @ResponseBody
    public String create(@RequestParam("nome") String nome, @RequestParam("descricao") String descricao, @RequestParam("nivel") String nivel)
    {
        try
        {
            Nivel nivel_ = Nivel.valueOf(nivel); // string para enum
            Competencia competenciaNew = new Competencia(nome, descricao, nivel_); // nova competência
            competenciaRepository.save(competenciaNew);
            return "Competencia Criada: " + competenciaNew.toString();
        }
        catch (IllegalArgumentException e)
        {
            return "Nível '"+nivel+"' não existe";
        }
    }

    // http://localhost:80/competencia/atualizar?id=3&nome=teste&descricao=teste2&nivel=NENHUMA_EXPERIENCIA
    @RequestMapping("/competencia/atualizar")
    @ResponseBody
    public String update(@RequestParam("id") Long id, @RequestParam("nome") String nome, @RequestParam("descricao") String descricao, @RequestParam("nivel") String nivel) {

        Competencia comp,compOld;

        try {
            Nivel nivel_ = Nivel.valueOf(nivel);
            comp = competenciaRepository.findById(id).get();
            compOld = new Competencia(comp.getNome(), comp.getDescricao(), comp.getNivel());
            if (comp != null) { // verifica se a competencia existe

                for (Nivel n : Nivel.values()) { // verifica se nivel existe
                    System.out.println(n);
                    if (nivel_.equals(n) && !nivel_.equals(comp.getNivel())){
                        comp.setNivel(nivel_);
//                        System.out.println("novo nível");
                        break; // sai do loop
                    }
                }

                if(nome != null && !nome.equals(comp.getNome())) {
//                    System.out.println("novo nome");
                    comp.setNome(nome);
                }

                if (descricao != null && !descricao.equals(comp.getDescricao())) {
//                    System.out.println("nova descrição");
                    comp.setDescricao(descricao);
                }

                competenciaRepository.save(comp);
            }
        } catch (EntityNotFoundException e) {
            return "Competencia não encontrada";
        } catch (IllegalArgumentException e) {
            return "Nível '"+nivel+"' não existe";
        }

        if(comp.equals(compOld))
            return "Competencia não foi modificada pelo usuario: " + comp.toString();

        return "Competencia atualizada: " + comp.toString();
    }


    // http://localhost:80/competencia/remover?id=1
    @RequestMapping("/competencia/remover")
    @ResponseBody
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

    // http://localhost:80/competencia/obter?id=1
    @RequestMapping("/competencia/obter")
    @ResponseBody
    public Competencia get(@RequestParam("id") Long id) {
        try {
            Competencia comp = competenciaRepository.findById(id).get();
            return comp;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    // http://localhost:80/competencia/listar
    @RequestMapping("/competencia/listar")
    @ResponseBody
    public List<Competencia> getlist() {
        List<Competencia> comps = competenciaRepository.findAll();
        return comps;
    }
}
