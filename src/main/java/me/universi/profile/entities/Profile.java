package me.universi.profile.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.entidades.FolderFavorite;
import me.universi.capacity.entidades.FolderProfile;
import me.universi.competence.entities.CompetenceType;
import me.universi.education.entities.Education;
import me.universi.experience.entities.Experience;
import me.universi.group.entities.ProfileGroup;
import me.universi.image.entities.ImageMetadata;
import me.universi.image.services.ImageMetadataService;
import me.universi.link.entities.Link;
import me.universi.role.entities.Role;
import me.universi.profile.enums.Gender;
import me.universi.user.entities.User;
import org.hibernate.annotations.*;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Entity(name = "Profile")
@Table( name = "profile", schema = "profile" )
@SQLDelete(sql = "UPDATE profile.profile SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
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

    @Nullable
    @OneToOne
    @JoinColumn( name = "image_metadata_id" )
    private ImageMetadata image;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", fetch = FetchType.EAGER)
    private Collection<ProfileGroup> groups;

    @JsonIgnore
    @OneToMany( mappedBy = "profile" )
    private Collection<Education> educations;

    @JsonIgnore
    @OneToMany( mappedBy = "profile" )
    private Collection<Experience> experiences;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private Collection<Link> links;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private Collection<ContentStatus> contentStatus;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
    private Collection<FolderProfile> assignedFolders;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Collection<FolderFavorite> favoriteFolders;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Role role;

    @JsonIgnore
    @Column(name = "hidden")
    private boolean hidden = Boolean.FALSE;

    @JsonIgnore
    @OneToMany( fetch = FetchType.EAGER )
    @JoinTable(
        name = "profile_competence_badges",
        schema = "profile",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_type_id")
    )
    private Collection<CompetenceType> competenceBadges;

    public Profile(UUID id, User user, String bio, Collection<ProfileGroup> groups, Collection<Link> links, Collection<FolderProfile> assignedFolders, Collection<FolderFavorite> favoriteFolders, boolean hidden) {
        this.id = id;
        this.user = user;
        this.bio = bio;
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

    public Collection<ContentStatus> getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(Collection<ContentStatus> contentStatus) {
        this.contentStatus = contentStatus;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Collection<CompetenceType> getCompetenceBadges() {
        return competenceBadges;
    }

    public void setCompetenceBadges(Collection<CompetenceType> competenceBadges) {
        this.competenceBadges = competenceBadges;
    }

    public @Nullable ImageMetadata getImage() { return image; }
    public void setImage(ImageMetadata image) { this.image = image; }

    @Transient
    public boolean hasBadge(@NotNull CompetenceType competenceType) {
        return this.getCompetenceBadges().stream().anyMatch(ct -> ct.getId().equals(competenceType.getId()));
    }

    @Override
    public String toString() {
        return "\nProfile[" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", image='" + ImageMetadataService.getInstance().getUri( image ) + '\'' +
                ", bio='" + bio + "']\n";
    }
}
