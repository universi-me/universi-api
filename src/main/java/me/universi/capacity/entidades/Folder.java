package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import me.universi.capacity.service.FolderService;
import me.universi.competence.entities.CompetenceType;
import me.universi.group.entities.Group;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;

import org.hibernate.annotations.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(name="folder")
@SQLDelete(sql = "UPDATE folder SET deleted = true WHERE id=?")
@SQLRestriction( value = "NOT deleted" )
public class Folder implements Serializable {

    @Serial
    private static final long serialVersionUID = -1163545345342344343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    public static final int FOLDER_REFERENCE_SIZE = 15;
    public static final String FOLDER_REFERENCE_AVAILABLE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    @Column(name = "reference", unique = true, length = FOLDER_REFERENCE_SIZE)
    private String reference;

    @Column
    @Size(max = 100)
    private String name;

    @Column
    @Size(max = 100)
    private String image;

    @Column
    @Size(max = 200)
    private String description;

    @OneToMany(cascade = CascadeType.PERSIST)
    private Collection<Category> categories;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Content> contents;

    @Column
    @NotNull
    @Min(0)
    @Max(5)
    private Integer rating;

    @ManyToOne
    @JoinColumn(name="profile_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    private Profile author;

    @Column(name = "public_folder")
    @NotNull
    public boolean publicFolder;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "folder_granted_access_groups",
        joinColumns = @JoinColumn(name = "folder_id"),
        inverseJoinColumns = @JoinColumn(name = "granted_access_groups_id")
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Group> grantedAccessGroups;

    @OneToMany(mappedBy = "folder")
    @JsonIgnore
    private Collection<FolderProfile> assignedUsers;

    @OneToMany(mappedBy = "folder")
    @JsonIgnore
    private Collection<FolderFavorite> favoriteUsers;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @ManyToMany
    @JoinTable(
        name = "folder_competences",
        joinColumns = @JoinColumn(name = "folder_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_type_id")
    )
    private Collection<CompetenceType> grantsBadgeToCompetences;

    public Folder() {
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Collection<Category> getCategories() {
        return categories;
    }

    public void setCategories(Collection<Category> categories) {
        this.categories = categories;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setContents(Collection<Content> contents) {
        this.contents = contents;
    }

    public Collection<Content> getContents() {
        return this.contents;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }

    public boolean isPublicFolder() {
        return publicFolder;
    }

    public void setPublicFolder(boolean publicFolder) {
        this.publicFolder = publicFolder;
    }

    public Collection<Group> getGrantedAccessGroups() {
        return grantedAccessGroups;
    }

    public void setGrantedAccessGroups(Collection<Group> grantedAccessGroups) {
        this.grantedAccessGroups = grantedAccessGroups;
    }

    public Collection<FolderProfile> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Collection<FolderProfile> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Collection<CompetenceType> getGrantsBadgeToCompetences() {
        return grantsBadgeToCompetences;
    }

    public void setGrantsBadgeToCompetences(Collection<CompetenceType> grantsBadgeToCompetences) {
        this.grantsBadgeToCompetences = grantsBadgeToCompetences;
    }

    @Transient
    public boolean isCanEdit() {
        return FolderService.getInstance().hasPermissions(this, true);
    }

    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Profile> getAssignedBy() {
        return this.assignedUsers.stream()
            .filter(u -> ProfileService.getInstance().isSessionOfProfile(u.getAssignedTo()))
            .map( fp -> fp.getAssignedBy() )
            .toList();
    }

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean isFavorite() {
        FolderFavorite favorite = this.favoriteUsers.stream()
            .filter(f -> ProfileService.getInstance().isSessionOfProfile(f.getProfile()))
            .findAny()
            .orElse(null);

        return favorite != null
            ? true
            : null;
    }
}
