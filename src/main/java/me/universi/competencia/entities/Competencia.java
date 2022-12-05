package me.universi.competencia.entities;

import me.universi.competencia.enums.Nivel;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "competencia")
public class Competencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competencia")
    private Long id;
    @Column(name = "nome")
    private String nome;
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

    public Competencia(String nome, String descricao, Nivel nivel) {
        this.nome = nome;
        this.descricao = descricao;
        this.nivel = nivel;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Competencia)) {
			return false;
		}
		try {
            Competencia competencia = (Competencia) obj;
            return this.nome.equals(competencia.getNome()) && this.descricao.equals(competencia.getDescricao()) && this.nivel.equals(competencia.getNivel());
        } catch (Exception e) {
            return false;
        }
    }
}