package me.universi.profile.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.entidades.FolderFavorite;
import me.universi.capacity.entidades.FolderProfile;
import me.universi.competence.entities.Competence;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.experience.entities.Experience;
import me.universi.group.entities.ProfileGroup;
import me.universi.indicators.entities.Indicators;
import me.universi.link.entities.Link;
import me.universi.roles.entities.Roles;
import me.universi.profile.enums.Gender;
import me.universi.recommendation.entities.Recommendation;
import me.universi.user.entities.User;
import org.hibernate.annotations.*;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Entity(name = "profile")
@SQLDelete(sql = "UPDATE profile SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Profile implements Serializable {

    @Serial
    private static final long serialVersionUID = -7368873462832313132L;

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
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Competence> competences;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", fetch = FetchType.EAGER)
    private Collection<ProfileGroup> groups;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "education_profile",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "education_id")
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Education> educations;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "experience_profile",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "experience_id")
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Experience> experiences;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
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
    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private Collection<ContentStatus> contentStatus;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private Indicators indicators;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Collection<FolderProfile> assignedFolders;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Collection<FolderFavorite> favoriteFolders;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Roles roles;

    @JsonIgnore
    @Column(name = "hidden")
    private boolean hidden = Boolean.FALSE;

    public Profile(UUID id, User user, String bio, Collection<Competence> competences, Collection<ProfileGroup> groups, Collection<Link> links, Collection<FolderProfile> assignedFolders, Collection<FolderFavorite> favoriteFolders, boolean hidden) {
        this.id = id;
        this.user = user;
        this.bio = bio;
        this.competences = competences;
        this.groups = groups;
        this.links = links;
        this.assignedFolders = assignedFolders;
        this.favoriteFolders = favoriteFolders;
        this.hidden = hidden;
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

    public Collection<ProfileGroup> getGroups() {
        return groups;
    }

    public void setGroups(Collection<ProfileGroup> groups) {
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

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public Collection<FolderProfile> getAssignedFolders() {
        return assignedFolders;
    }

    public void setAssignedFolders(Collection<FolderProfile> assignedFolders) {
        this.assignedFolders = assignedFolders;
    }

    public Collection<FolderFavorite> getFavoriteFolders() {
        return favoriteFolders;
    }

    public void setFavoriteFolders(Collection<FolderFavorite> favoriteFolders) {
        this.favoriteFolders = favoriteFolders;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
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
