package me.universi.recommendation.entities;

import me.universi.competence.entities.CompetenceType;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "recommendation")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recommendation")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin")
    private Profile origin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destiny")
    private Profile destiny;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competence_type")
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
