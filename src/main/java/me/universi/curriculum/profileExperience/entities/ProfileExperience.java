package me.universi.curriculum.profileExperience.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "profile_experience")
public class ProfileExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

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

    public ProfileExperience(){

    }

    public ProfileExperience(TypeExperience typeExperience,String local, String description, Date startDate, Date endDate, Boolean presentDate) {
        this.typeExperience = typeExperience;
        this.local = local;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.presentDate = presentDate;
    }

    public ProfileExperience(TypeExperience typeExperience,String local, String description, Date startDate, Date endDate) {
        this.typeExperience = typeExperience;
        this.local = local;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
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
}
