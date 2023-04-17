package me.universi.competencia.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity(name = "competenciatipo")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompetenceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competenciatipo")
    private Long id;

    @Column(name = "nome", unique=true)
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
