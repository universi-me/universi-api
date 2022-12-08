package me.universi.competencia.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.universi.competencia.enums.Nivel;
import me.universi.perfil.entities.Perfil;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "competencia")
public class Competencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competencia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competenciatipo")
    private CompetenciaTipo competenciaTipo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profile")
    private Perfil perfil;
    @Column(name = "descricao")
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel")
    private Nivel nivel;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_de_criacao")
    private Date dataDeCriacao;

    public Competencia() {
    }

    public Competencia(String descricao, Nivel nivel) {
        this.descricao = descricao;
        this.nivel = nivel;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Nivel getNivel() { return nivel; }

    public void setNivel(Nivel nivel) { this.nivel = nivel; }

    public Date getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(Date dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }

    public CompetenciaTipo getCompetenciaTipo() {
        return competenciaTipo;
    }

    public void setCompetenciaTipo(CompetenciaTipo competenciaTipo) {
        this.competenciaTipo = competenciaTipo;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
}