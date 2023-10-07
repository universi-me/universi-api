package me.universi.competence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import me.universi.competence.enums.Level;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "competence")
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_type_id")
    private CompetenceType competenceType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private Level level;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "present_date")
    private Boolean presentDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    public Competence() {
    }



    public Competence(CompetenceType competenceType, String description, String title, Level level, Date startDate, Date endDate) {
        this.competenceType = competenceType;
        this.description = description;
        this.title = title;
        this.level = level;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Competence(CompetenceType competenceType, String description, String title, Level level, Date startDate, Boolean presentDate) {
        this.competenceType = competenceType;
        this.description = description;
        this.title = title;
        this.level = level;
        this.startDate = startDate;
        this.presentDate = presentDate;
    }

    public Competence(CompetenceType competenceType, String title, Level level) {
        this.competenceType = competenceType;
        this.title = title;
        this.level = level;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Level getLevel() { return level; }

    public void setLevel(Level level) { this.level = level; }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public CompetenceType getCompetenceType() {
        return competenceType;
    }

    public void setCompetenceType(CompetenceType competenceType) {
        this.competenceType = competenceType;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Boolean getPresentDate() { return presentDate; }

    public void setPresentDate(Boolean presentDate) { this.presentDate = presentDate; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }
}
