package me.universi.vacancy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity(name="vacancy")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vacancy_generator")
    @SequenceGenerator(name = "vacancy_generator", sequenceName = "vacancy_sequence", allocationSize = 1)
    @Column(name = "id_vacancy")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profile")
    private Profile profile;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate;

    public Vacancy(){

    }
    public Vacancy(String description){
        this.description = description;
    }

    public Long getId(){
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
}
