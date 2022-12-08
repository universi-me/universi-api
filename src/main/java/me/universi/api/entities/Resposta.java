package me.universi.api.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class Resposta {
    public boolean sucess;
    public String mensagem;
    public String enderecoParaRedirecionar;
    public Map conteudo;

    public Resposta() {
        conteudo = new HashMap();
    }

    @Override
    public String toString() {
        try {
            // Parse esta classe para Json String
            ObjectMapper mapper = new ObjectMapper();
            return (String)mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
