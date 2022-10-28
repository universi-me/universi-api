package me.universi.grupo.controller;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.repositories.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class GrupoController
{
    @Autowired
    public GrupoRepository grupoRepository;

    // mapaear tudo exceto, /css, /js, /img, /favicon.ico, comflita com static resources do Thymeleaf
    @GetMapping(value = {"{url:(?!css$|js$|img$|favicon.ico$).*}/**"})
    public String grupo_handler(HttpServletRequest request, HttpSession session, ModelMap map)
    {
        String requestSt = request.getRequestURI();
        System.out.println("path: "+ requestSt);

        // TODO, popular página grupo

        return "grupo";
    }

    // http://localhost:8080/projeto/criar?nome=teste&descricao=teste2
    @RequestMapping("/grupo/criar")
    @ResponseBody
    public String create(@RequestParam("nome") String nome, @RequestParam("descricao") String descricao)
    {
        Grupo grupoNew = new Grupo();
        grupoRepository.save(grupoNew);
        return "Grupo Criado: "+ grupoNew.toString();
    }

    // http://localhost:8080/projeto/remover?id=1
    @RequestMapping("/grupo/remover")
    @ResponseBody
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
    @RequestMapping("/grupo/obter/{id}")
    @ResponseBody
    public Grupo get(@PathVariable Long id)
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
    @ResponseBody
    public List<Grupo> getlist()
    {
        List<Grupo> ret = grupoRepository.findAll();
        return ret;
    }
}
