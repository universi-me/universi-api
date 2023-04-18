package me.universi.recomendacao.entities;

import me.universi.competencia.entities.CompetenceType;
import me.universi.perfil.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "recomendacao")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacao")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origem")
    private Profile origin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destiny")
    private Profile destiny;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competenciatipo")
    private CompetenceType competenceType;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate;

    public Recommendation(Profile origin, Profile destiny, CompetenceType competencia, String description) {
        this.origin = origin;
        this.destiny = destiny;
        this.competenceType = competencia;
        this.description = description;
    }
    public Recommendation() {}

    public Long getId() {
        return id;
    }

    public Profile getOrigin() {
        return origin;
    }

    public void setOrigin(Profile origin) {
        this.origin = origin;
    }

    public Profile getDestiny() {
        return destiny;
    }

    public void setDestiny(Profile destiny) {
        this.destiny = destiny;
    }

    public CompetenceType getCompetenceType() {
        return competenceType;
    }

    public void setCompetenceType(CompetenceType competenceType) {
        this.competenceType = competenceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
