package me.universi.recomendacao.controller;


import me.universi.api.entities.Resposta;
import me.universi.recomendacao.entities.Recomendacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import me.universi.recomendacao.service.RecomendacaoService;


import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class RecomendacaoController {
    @Autowired
    public RecomendacaoService recomendacaoService;


    // http://localhost:8080/recomendacao/criar?origem=2&destino=4&descricao=testeDescricao&competencia=4
    @PostMapping(value = "/recomendacao/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object create(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long idOrigem = (Long)Long.valueOf((String)body.get("origem"));
            Long idDestino = (Long)Long.valueOf((String)body.get("destino"));
            String descricao = (String)body.get("descricao");
            Long idCompetencia = (Long)Long.valueOf((String)body.get("competencia"));

            Recomendacao recomendacoNew = new Recomendacao();

            //TODO - SETAR ATRIBUTOS NO OBJETO
            //TODO - TRATAMENTO DE EXCEÇÕES

            recomendacaoService.save(recomendacoNew);

            resposta.mensagem = "Recomendacao criada: " + recomendacoNew.toString();
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    // http://localhost:8080/recomendacao/atualizar?id=5&4&descricao=testeDescricao
    @PostMapping(value = "/recomendacao/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object atualizar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long id = (Long)Long.valueOf((String)body.get("id"));
            String descricao = (String)body.get("descricao");

            //TODO - CRIAR VALIDAÇÃO DE PARÂMETROS
            Recomendacao recomendacao = recomendacaoService.findById(id);
            if(recomendacao != null){
                recomendacao.setDescricao(descricao);
                recomendacaoService.update(recomendacao);

                resposta.mensagem = "Recomendacao atualizada: " + recomendacao.toString();
                resposta.sucess = true;
                return resposta;
            }

            resposta.mensagem = "Recomendação não encontrada";
            return resposta;

        }catch(EntityNotFoundException e) {
            resposta.mensagem = "Recomendação não encontrada";
            return resposta;
        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    // http://localhost:8080/recomendacao/remover?id=1
    @PostMapping(value = "/recomendacao/remover", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long id = (Long)Long.valueOf((String)body.get("id"));

            Recomendacao recomendacao = recomendacaoService.findById(id);
            if (recomendacao != null) {
                recomendacaoService.delete(recomendacao);

                resposta.mensagem = "Recomendação Removida: " + recomendacao.toString();
                resposta.sucess = true;
                return resposta;
            }
        } catch (EntityNotFoundException e) {
            resposta.mensagem = "Recomendação não encontrada";
            return resposta;
        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
        return "Falha ao remover";
    }

    // http://localhost:8080/recomendacao/obter?id=1
    @PostMapping(value = "/recomendacao/obter/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            Long id = (Long)Long.valueOf((String)body.get("id"));

            Recomendacao recomendacao = recomendacaoService.findById(id);
            if(recomendacao != null){
                resposta.conteudo.put("recomendacao", recomendacao);

                resposta.mensagem = "Operação realizada com exito.";
                resposta.sucess = true;
                return resposta;
            }

            resposta.mensagem = "Recomedação não encontrada.";
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    // http://localhost:8080/recomendacao/listar
    @PostMapping(value = "/recomendacao/listar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getlist(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            List<Recomendacao> recs = recomendacaoService.findAll();

            resposta.conteudo.put("lista", recs);

            resposta.mensagem = "Operação realizada com exito.";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }
}