package me.universi.recomendacao.entities;

import me.universi.competencia.entities.Competencia;
import me.universi.perfil.entities.Perfil;

import javax.persistence.*;

@Entity(name = "recomendacao")
public class Recomendacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacao")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recomendacoesFeitas")
    private Perfil origem;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recomendacoesRecebidas")
    private Perfil destino;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competencia")
    private Competencia competencia;
    @Column(name = "descricao")
    private String descricao;

    public Recomendacao(Perfil origem, Perfil destino, Competencia competencia, String descricao) {
        this.origem = origem;
        this.destino = destino;
        this.competencia = competencia;
        this.descricao = descricao;
    }
    public Recomendacao() {}

    public Perfil getOrigem() {
        return origem;
    }

    public void setOrigem(Perfil origem) {
        this.origem = origem;
    }

    public Perfil getDestino() {
        return destino;
    }

    public void setDestino(Perfil destino) {
        this.destino = destino;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
