package me.universi.profile.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.competence.entities.Competence;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.experience.entities.Experience;
import me.universi.group.entities.Group;
import me.universi.indicators.entities.Indicators;
import me.universi.link.entities.Link;
import me.universi.profile.enums.Gender;
import me.universi.recommendation.entities.Recommendation;
import me.universi.user.entities.User;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Entity(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "name")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "image")
    private String image;
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "competence_profile",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private Collection<Competence> competences;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "profile_group",
            joinColumns = { @JoinColumn(name = "profile_id") },
            inverseJoinColumns = { @JoinColumn(name = "group_id") }
    )
    @JsonIgnore
    private Collection<Group> groups;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "education_profile",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "education_id")
    )
    private Collection<Education> educations;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "experience_profile",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "experience_id")
    )
    private Collection<Experience> experiences;

    @JsonIgnore
    @OneToMany(mappedBy = "profile")
    private Collection<Link> links;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @JsonIgnore
    @OneToMany(mappedBy = "origin")
    private Collection<Recommendation> recomendationsSend;
    @JsonIgnore
    @OneToMany(mappedBy = "destiny")
    private Collection<Recommendation> recomendationsReceived;

    @JsonIgnore
    @OneToMany(mappedBy = "profile")
    private Collection<ContentStatus> contentStatus;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private Indicators indicators;

    public Profile(UUID id, User user, String bio, Collection<Competence> competences, Collection<Group> groups, Collection<Link> links) {
        this.id = id;
        this.user = user;
        this.bio = bio;
        this.competences = competences;
        this.groups = groups;
        this.links = links;
    }

    public Profile(){

    }

    public UUID getId() {
        return id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Collection<Competence> getCompetences() {
        return competences;
    }

    public void setCompetences(Collection<Competence> competences) {
        this.competences = competences;
    }

    public Collection<Group> getGroups() {
        return groups;
    }

    public void setGroups(Collection<Group> groups) {
        this.groups = groups;
    }

    public Collection<Link> getLinks() {
        return links;
    }

    public void setLinks(Collection<Link> links) {
        this.links = links;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Collection<Recommendation> getRecomendationsSend() {
        return recomendationsSend;
    }

    public Collection<Recommendation> getRecomendationsReceived() {
        return recomendationsReceived;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setRecomendationsSend(Collection<Recommendation> recomendationsSend) {
        this.recomendationsSend = recomendationsSend;
    }

    public void setRecomendationsReceived(Collection<Recommendation> recomendationsReceived) {
        this.recomendationsReceived = recomendationsReceived;
    }

    public Collection<ContentStatus> getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(Collection<ContentStatus> contentStatus) {
        this.contentStatus = contentStatus;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Indicators getIndicators() {
        return indicators;
    }

    public void setIndicators(Indicators indicators) {
        this.indicators = indicators;
    }

    public Collection<Education> getEducations() {
        return educations;
    }

    public void setEducations(Collection<Education> educations) {
        this.educations = educations;
    }

    public Collection<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(Collection<Experience> experiences) {
        this.experiences = experiences;
    }

    @Override
    public String toString() {
        return "\nProfile[" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", image='" + image + '\'' +
                ", bio='" + bio + "']\n";
    }
}
