package me.universi.recomendacao.controller;


import me.universi.grupo.entities.Grupo;
import me.universi.recomendacao.entities.Recomendacao;
import me.universi.usuario.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import me.universi.recomendacao.service.RecomendacaoService;


import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class RecomendacaoController {
    @Autowired
    public RecomendacaoService recomendacaoService;


    // http://localhost:8080/recomendacao/criar?origem=2&destino=4&descricao=testeDescricao&competencia=4
    @RequestMapping("/recomendacao/criar")
    @ResponseBody
    public String create(@RequestParam("origem") Long idOrigem, @RequestParam("destino") Long idDestino, @RequestParam("descricao") String descricao, @RequestParam("competencia") Long id) {
        Recomendacao recomendacoNew = new Recomendacao();
        //TODO - SETAR ATRIBUTOS NO OBJETO
        //TODO - TRATAMENTO DE EXCEÇÕES
        recomendacaoService.save(recomendacoNew);
        return "Recomendacao criada: " + recomendacoNew.toString();
    }
    // http://localhost:8080/recomendacao/atualizar?id=5&4&descricao=testeDescricao
    @RequestMapping("/recomendacao/atualizar")
    @ResponseBody
    public String create(@RequestParam("id") Long id, @RequestParam("descricao") String descricao) {
        try{
            //TODO - CRIAR VALIDAÇÃO DE PARÂMETROS
            Recomendacao recomendacao = recomendacaoService.findById(id);
            if(recomendacao != null){
                recomendacao.setDescricao(descricao);
                recomendacaoService.update(recomendacao);
                return "Recomendacao atualizada: " + recomendacao.toString();
            }
            return "Recomendação não encontrada";
        }catch(EntityNotFoundException e){
            return "Recomendação não encontrada";
        }
    }

    // http://localhost:8080/recomendacao/remover?id=1
    @RequestMapping("/recomendacao/remover")
    @ResponseBody
    public String remove(@RequestParam("id") Long id) {
        try {
            Recomendacao recomendacao = recomendacaoService.findById(id);
            if (recomendacao != null) {
                recomendacaoService.delete(recomendacao);
                return "Recomendação Removida: " + recomendacao.toString();
            }
        } catch (EntityNotFoundException e) {
            return "Recomendação não encontrada";
        }
        return "Falha ao remover";
    }

    // http://localhost:8080/recomendacao/obter?id=1
    @RequestMapping("/recomendacao/obter/{id}")
    @ResponseBody
    public Recomendacao get(@PathVariable Long id) {
        try {
            Recomendacao recomendacao = recomendacaoService.findById(id);
            if(recomendacao != null){
                return recomendacao;
            }else{
                return null;
            }
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    // http://localhost:8080/recomendacao/listar
    @RequestMapping("/recomendacao/listar")
    @ResponseBody
    public List<Recomendacao> getlist() {
        List<Recomendacao> recs = recomendacaoService.findAll();
        return recs;
    }
}