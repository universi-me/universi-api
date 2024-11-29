package me.universi.competence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.*;

import java.util.Date;
import java.util.UUID;

@Entity(name = "competence")
@SQLDelete(sql = "UPDATE competence SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_type_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private CompetenceType competenceType;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "level")
    @Max(3)
    @Min(0)
    private int level;

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

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Competence() {

    }



    public Competence(CompetenceType competenceType, String description, String title, int level, Date startDate, Date endDate) {
        this.competenceType = competenceType;
        this.description = description;
        this.title = title;
        this.level = level;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Competence(CompetenceType competenceType, String description, String title, int level, Date startDate, Boolean presentDate) {
        this.competenceType = competenceType;
        this.description = description;
        this.title = title;
        this.level = level;
        this.startDate = startDate;
        this.presentDate = presentDate;

    }

    public Competence(CompetenceType competenceType, String title, int level) {
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

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }

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

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Boolean getPresentDate() { return presentDate; }

    public void setPresentDate(Boolean presentDate) { this.presentDate = presentDate; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
