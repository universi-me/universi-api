package me.universi.vacancy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import me.universi.competence.entities.Competence;
import me.universi.profile.entities.Profile;
import me.universi.vacancy.typeVacancy.entities.TypeVacancy;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name="vacancy")
@SQLDelete(sql = "UPDATE vacancy SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profile")
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_vacancy_id")
    private TypeVacancy typeVacancy;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "prerequisites")
    private String prerequisites;

    @Temporal(TemporalType.DATE)
    @Column(name = "registration_date")
    private Date registrationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_registration_date")
    private Date endRegistrationDate;

    @ManyToMany
    @JoinTable(
            name = "competence_vacancy",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private List<Competence> competenceRequired;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Vacancy(){
        this.isActive = true;
    }

    public Vacancy(TypeVacancy typeVacancy, String title, String description, String prerequisites, Date registrationDate, Date endRegistrationDate, Date creationDate) {
        this.typeVacancy = typeVacancy;
        this.title = title;
        this.description = description;
        this.prerequisites = prerequisites;
        this.registrationDate = registrationDate;
        this.endRegistrationDate = endRegistrationDate;
        this.creationDate = creationDate;
        this.isActive = true;
    }
    public Vacancy(String title, String description, String prerequisites, Date registrationDate, Date endRegistrationDate, List<Competence> competenceRequired, Date creationDate) {
        this.title = title;
        this.description = description;
        this.prerequisites = prerequisites;
        this.registrationDate = registrationDate;
        this.endRegistrationDate = endRegistrationDate;
        this.competenceRequired = competenceRequired;
        this.creationDate = creationDate;
        this.isActive = true;
    }

    public UUID getId(){
        return this.id;
    }
    public Profile getProfile(){
        return this.profile;
    }
    public void setProfile(Profile profile){
        this.profile = profile;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public Date getCreationDate(){
        return this.creationDate;
    }
    public void setCreationDate(Date creationDate){
        this.creationDate = creationDate;
    }

    public TypeVacancy getTypeVacancy() {
        return typeVacancy;
    }

    public void setTypeVacancy(TypeVacancy typeVacancy) {
        this.typeVacancy = typeVacancy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getEndRegistrationDate() {
        return endRegistrationDate;
    }

    public void setEndRegistrationDate(Date endRegistrationDate) {
        this.endRegistrationDate = endRegistrationDate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public List<Competence> getCompetenceRequired() {
        return competenceRequired;
    }

    public void setCompetenceRequired(List<Competence> competenceRequired) {
        this.competenceRequired = competenceRequired;
    }
}
