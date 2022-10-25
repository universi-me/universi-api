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
    @RequestMapping("/grupo/criar")
    public String create(@RequestParam("nome") String nome, @RequestParam("descricao") String descricao)
    {
        Grupo grupoNew = new Grupo();
        grupoRepository.save(grupoNew);
        return "Grupo Criado: "+ grupoNew.toString();
    }

    // http://localhost:8080/projeto/remover?id=1
    @RequestMapping("/grupo/remover")
    public String remove(@RequestParam("id") Long id)
    {
        try {
            Grupo proj = grupoRepository.findById(id).get();
            if (proj != null) {
                grupoRepository.delete(proj);
                return "Grupo Removido: " + proj.toString();
            }
        }catch (EntityNotFoundException e) {
            return "Grupo não encontrado";
        }
        return "Falha ao remover";
    }

    // http://localhost:8080/projeto/obter?id=1
    @RequestMapping("/grupo/obter")
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
    @RequestMapping("/grupo/listar")
    public List<Grupo> getlist()
    {
        List<Grupo> ret = grupoRepository.findAll();
        return ret;
    }
}
