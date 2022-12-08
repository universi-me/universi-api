package me.universi.api.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/*
    Classe com uma estrutura padrão de resposta para a api.
 */

public class Resposta {

    // determinar se a operação feita deu erro ou não
    public boolean sucess;

    // mensagem de alerta para mostrar na página
    public String mensagem;

    // endereço url para redirecionar apos receber a resposta
    public String enderecoParaRedirecionar;

    // colocar qualquer dados na resposta
    public Map conteudo;

    public Resposta() {
        // alocar dicionario
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
