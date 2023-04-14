package me.universi.competencia.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.universi.competencia.enums.Nivel;
import me.universi.perfil.entities.Perfil;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "competencia")
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competencia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competenciatipo")
    private CompetenciaTipo competenceType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profile")
    private Perfil profile;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel")
    private Nivel level;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_de_criacao")
    private Date createdAt;

    public Competence() {
    }

    public Competence(String description, Nivel level) {
        this.description = description;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Nivel getLevel() { return level; }

    public void setLevel(Nivel level) { this.level = level; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public CompetenciaTipo getCompetenceType() {
        return competenceType;
    }

    public void setCompetenceType(CompetenciaTipo competenceType) {
        this.competenceType = competenceType;
    }

    public Perfil getProfile() {
        return profile;
    }

    public void setProfile(Perfil profile) {
        this.profile = profile;
    }
}
