package me.universi.competence.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity(name = "competence_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompetenceType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competence_type_generator")
    @SequenceGenerator(name = "competence_type_generator", sequenceName = "competence_type_sequence", allocationSize = 1)
    @Column(name = "id_competence_type")
    private Long id;

    @Column(name = "name", unique=true)
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
