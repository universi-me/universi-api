package me.universi.competencia.entities;

import me.universi.competencia.enums.Nivel;

import javax.persistence.*;

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

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Competencia)) {
			return false;
		}
		
        Competencia competencia = (Competencia) obj;

        return this.nome.equals(competencia.getNome()) && this.descricao.equals(competencia.getDescricao()) && this.nivel.equals(competencia.getNivel());
    }
}