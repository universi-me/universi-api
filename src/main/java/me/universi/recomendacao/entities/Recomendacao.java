package me.universi.recomendacao.entities;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.entities.CompetenciaTipo;
import me.universi.perfil.entities.Perfil;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "recomendacao")
public class Recomendacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacao")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origem")
    private Perfil origem;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destino")
    private Perfil destino;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competenciatipo")
    private CompetenciaTipo competenciaTipo;
    @Column(name = "descricao")
    private String descricao;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_de_criacao")
    private Date dataDeCriacao;

    public Recomendacao(Perfil origem, Perfil destino, CompetenciaTipo competencia, String descricao) {
        this.origem = origem;
        this.destino = destino;
        this.competenciaTipo = competencia;
        this.descricao = descricao;
    }
    public Recomendacao() {}

    public Long getId() {
        return id;
    }

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

    public CompetenciaTipo getCompetenciaTipo() {
        return competenciaTipo;
    }

    public void setCompetenciaTipo(CompetenciaTipo competenciaTipo) {
        this.competenciaTipo = competenciaTipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(Date dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }
}
