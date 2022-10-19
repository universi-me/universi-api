package me.universi.projeto.controller;

import me.universi.projeto.entities.Projeto;
import me.universi.projeto.repositories.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjetoController
{
    @Autowired
    public ProjetoRepository projetoRepository;

    // http://localhost:8080/projeto/create?nome=teste&descricao=teste2
    @RequestMapping("/projeto/create")
    public String create(@RequestParam("nome") String nome, @RequestParam("descricao") String descricao)
    {
        Projeto projetoNew = new Projeto(nome, descricao);
        projetoRepository.save(projetoNew);
        return "Projeto Criado: "+ projetoNew.toString();
    }

    // http://localhost:8080/projeto/remove?id=1
    @RequestMapping("/projeto/remove")
    public String remove(@RequestParam("id") Long id)
    {
        Projeto proj = projetoRepository.getReferenceById(id);
        if(proj != null) {
            projetoRepository.delete(proj);
            return "Projeto Removido: " + proj.toString();
        }
        return "Projeto não encontrado";
    }

    // http://localhost:8080/projeto/listar
    @RequestMapping("/projeto/listar")
    public List<Projeto> getlist()
    {
        List<Projeto> ret = projetoRepository.findAll();
        return ret;
    }
}
