package me.universi.competencia.controller;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.universi.api.entities.Resposta;
import me.universi.competencia.entities.Competencia;
import me.universi.competencia.enums.Nivel;
import me.universi.competencia.exceptions.CompetenciaException;
import me.universi.competencia.services.CompetenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CompetenciaController {
    @Autowired
    public CompetenciaService competenciaService;

    @PostMapping(value = "/competencia/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object create(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String nome = (String)body.get("nome");
            String descricao = (String)body.get("descricao");
            Nivel nivel = (Nivel)Nivel.valueOf((String)body.get("nivel"));

            Competencia competenciaNew = new Competencia(nome, descricao, nivel); // nova competência
            competenciaService.save(competenciaNew);

            resposta.mensagem = "Competencia Criada: " + competenciaNew.toString();
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object update(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        Competencia comp,compOld;
        try {

            String id = (String)body.get("id");
            if(id == null) {
                throw new CompetenciaException("Parametro id é nulo.");
            }

            String nome = (String)body.get("nome");
            String descricao = (String)body.get("descricao");
            String nivelSt = (String)body.get("nivel");

            comp = competenciaService.findFirstById(Long.valueOf(id));
            if (comp == null) {
                throw new CompetenciaException("Competencia não encontrada.");
            }

            compOld = new Competencia(comp.getNome(), comp.getDescricao(), comp.getNivel());

            Nivel nivel = Nivel.valueOf(nivelSt);

            for (Nivel n : Nivel.values()) { // verifica se nivel existe
                System.out.println(n);
                if (nivel.equals(n) && !nivel.equals(comp.getNivel())){
                    comp.setNivel(nivel);
//                       System.out.println("novo nível");
                    break; // sai do loop
                }
            }
            if(nome != null && !nome.equals(comp.getNome())) {
//                   System.out.println("novo nome");
                comp.setNome(nome);
            }
            if (descricao != null && !descricao.equals(comp.getDescricao())) {
//                   System.out.println("nova descrição");
                comp.setDescricao(descricao);
            }
            competenciaService.save(comp);

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }

        if(comp.equals(compOld)) {
            resposta.mensagem = "Competencia não foi modificada pelo usuario: " + comp.toString();
            return resposta;
		}

        resposta.mensagem = "Competencia atualizada: " + comp.toString();
        resposta.sucess = true;
        return resposta;
    }

    @PostMapping(value = "/competencia/remover", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long id = (Long)Long.valueOf((String)body.get("id"));

            Competencia comp = competenciaService.findFirstById(id);
            if (comp != null) {
                competenciaService.delete(comp);

                resposta.mensagem = "Competencia removida: " + comp.toString();
                resposta.sucess = true;
                return resposta;
            }

            resposta.mensagem = "Falha ao remover competencia";
            return resposta;

        } catch (EntityNotFoundException e) {
            resposta.mensagem = "Competencia não encontrada";
            return resposta;
        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/obter", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long id = (Long)Long.valueOf((String)body.get("id"));

            Competencia comp = competenciaService.findFirstById(id);
            resposta.conteudo.put("competencia", comp);

            resposta.mensagem = "Operação realizada com exito.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/competencia/listar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getlist(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            List<Competencia> comps = competenciaService.findAll();

            resposta.conteudo.put("lista", comps);

            resposta.mensagem = "Operação realizada com exito.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }
}
