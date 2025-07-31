package me.universi.education.entities;


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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import me.universi.institution.entities.Institution;
import me.universi.profile.entities.Profile;

import me.universi.util.HibernateUtil;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "Education")
@Table( name = "education", schema = "education" )
@SQLDelete(sql = "UPDATE education.education SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_type_id")
    private EducationType educationType;

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

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn( name = "profile_id" )
    @NotNull
    private Profile profile;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Education() {}

    public Education( EducationType typeEducation, Institution institution, Date startDate, Date endDate, Profile profile ){
        this.educationType = typeEducation;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
        this.profile = profile;
    }

    public UUID getId() { return id; }

    public EducationType getEducationType() { return HibernateUtil.resolveLazyHibernateObject(educationType); }
    public void setEducationType(EducationType typeEducation) { this.educationType = typeEducation; }

    public Institution getInstitution() { return HibernateUtil.resolveLazyHibernateObject(institution); }
    public void setInstitution(Institution institution) { this.institution = institution; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public Profile getProfile() { return HibernateUtil.resolveLazyHibernateObject(profile); }
    public void setProfile(Profile profile) { this.profile = profile; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
