package me.universi.recomendacao.exceptions;

public class RecomendacaoInvalidaException extends Exception{
    private String mensagem;
    public RecomendacaoInvalidaException(String mensagem){
        super(mensagem);
        this.mensagem = mensagem;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
