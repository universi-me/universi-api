package me.universi.curriculum.experience.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "experience")
@SQLDelete(sql = "UPDATE experience SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_experience_id")
    private TypeExperience typeExperience;

    /*Criar tipo local ?*/
    @Column(name = "local")
    private String local;

    @Column(name = "description")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
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

    public Experience(){

    }

    public Experience(TypeExperience typeExperience, String local, String description, Date startDate, Date endDate, Boolean presentDate) {
        this.typeExperience = typeExperience;
        this.local = local;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.presentDate = presentDate;
    }

    public Experience(TypeExperience typeExperience, String local, String description, Date startDate, Date endDate) {
        this.typeExperience = typeExperience;
        this.local = local;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.presentDate = false;
    }

    public UUID getId() {
        return id;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getPresentDate() {
        return presentDate;
    }

    public void setPresentDate(Boolean presentDate) {
        this.presentDate = presentDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public TypeExperience getTypeExperience() {
        return typeExperience;
    }

    public void setTypeExperience(TypeExperience typeExperience) {
        this.typeExperience = typeExperience;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
