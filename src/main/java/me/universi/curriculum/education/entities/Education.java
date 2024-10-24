package me.universi.curriculum.education.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import me.universi.institution.entities.Institution;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "education")
@SQLDelete(sql = "UPDATE education SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_education_id")
    private TypeEducation typeEducation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Institution institution;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    /*Vai verificar se o usuario ainda nao terminou*/
    @Column(name = "present_date")
    private Boolean presentDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Education(){
        this.presentDate = false;
    }

    public Education( TypeEducation typeEducation, Institution institution, Date startDate, Date endDate, Boolean presentDate){
        this.typeEducation = typeEducation;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
        this.presentDate = presentDate;
    }

    public Education( TypeEducation typeEducation, Institution institution, Date startDate, Date endDate){
        this.typeEducation = typeEducation;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
        this.presentDate = false;
    }

    public UUID getId() {
        return id;
    }

    public TypeEducation getTypeEducation() {
        return typeEducation;
    }

    public void setTypeEducation(TypeEducation typeEducation) {
        this.typeEducation = typeEducation;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
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

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
