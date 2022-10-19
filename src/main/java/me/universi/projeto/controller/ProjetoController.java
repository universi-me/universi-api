package me.universi.projeto.controller;

import me.universi.Sys;
import me.universi.projeto.entities.Projeto;
import me.universi.projeto.repositories.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class ProjetoController
{
    @Autowired
    public ProjetoRepository projetoRepository;

    // http://localhost:8080/projeto/criar?nome=teste&descricao=teste2
    @RequestMapping("/projeto/criar")
    public String create(@RequestParam("nome") String nome, @RequestParam("descricao") String descricao)
    {
        Projeto projetoNew = new Projeto(nome, descricao);
        projetoRepository.save(projetoNew);
        return "Projeto Criado: "+ projetoNew.toString();
    }

    // http://localhost:8080/projeto/remover?id=1
    @RequestMapping("/projeto/remover")
    public String remove(@RequestParam("id") Long id)
    {
        try {
            Projeto proj = projetoRepository.findById(id).get();
            if (proj != null) {
                projetoRepository.delete(proj);
                return "Projeto Removido: " + proj.toString();
            }
        }catch (EntityNotFoundException e) {
            return "Projeto não encontrado";
        }
        return "Falha ao remover";
    }

    // http://localhost:8080/projeto/obter?id=1
    @RequestMapping("/projeto/obter")
    public Projeto get(@RequestParam("id") Long id)
    {
        try {
            Projeto proj = projetoRepository.findById(id).get();
            return proj;
        }catch (EntityNotFoundException e) {
            return null;
        }
    }

    // http://localhost:8080/projeto/listar
    @RequestMapping("/projeto/listar")
    public List<Projeto> getlist()
    {
        List<Projeto> ret = projetoRepository.findAll();
        return ret;
    }
}
