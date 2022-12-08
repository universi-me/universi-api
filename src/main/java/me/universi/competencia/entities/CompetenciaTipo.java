package me.universi.competencia.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity(name = "competenciatipo")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompetenciaTipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competenciatipo")
    private Long id;

    @Column(name = "nome", unique=true)
    private String nome;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
