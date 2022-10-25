package me.universi.grupo.controller;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.repositories.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class GrupoController
{
    @Autowired
    public GrupoRepository grupoRepository;

    // http://localhost:8080/projeto/criar?nome=teste&descricao=teste2
    @RequestMapping("/projeto/criar")
    public String create(@RequestParam("nome") String nome, @RequestParam("descricao") String descricao)
    {
        Grupo grupoNew = new Grupo(nome, descricao);
        grupoRepository.save(grupoNew);
        return "Projeto Criado: "+ grupoNew.toString();
    }

    // http://localhost:8080/projeto/remover?id=1
    @RequestMapping("/projeto/remover")
    public String remove(@RequestParam("id") Long id)
    {
        try {
            Grupo proj = grupoRepository.findById(id).get();
            if (proj != null) {
                grupoRepository.delete(proj);
                return "Projeto Removido: " + proj.toString();
            }
        }catch (EntityNotFoundException e) {
            return "Projeto não encontrado";
        }
        return "Falha ao remover";
    }

    // http://localhost:8080/projeto/obter?id=1
    @RequestMapping("/projeto/obter")
    public Grupo get(@RequestParam("id") Long id)
    {
        try {
            Grupo proj = grupoRepository.findById(id).get();
            return proj;
        }catch (EntityNotFoundException e) {
            return null;
        }
    }

    // http://localhost:8080/projeto/listar
    @RequestMapping("/projeto/listar")
    public List<Grupo> getlist()
    {
        List<Grupo> ret = grupoRepository.findAll();
        return ret;
    }
}
