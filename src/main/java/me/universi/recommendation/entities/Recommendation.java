package me.universi.recommendation.entities;

import me.universi.competence.entities.CompetenceType;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Entity(name = "recommendation")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_origin_id")
    @NotNull
    private Profile origin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_destiny_id")
    @NotNull
    private Profile destiny;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_type_id")
    @NotNull
    private CompetenceType competenceType;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    public Recommendation(Profile origin, Profile destiny, CompetenceType competencia, String description) {
        this.origin = origin;
        this.destiny = destiny;
        this.competenceType = competencia;
        this.description = description;
    }
    public Recommendation() {}

    public UUID getId() {
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
