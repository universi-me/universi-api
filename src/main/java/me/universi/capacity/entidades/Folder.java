package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import me.universi.capacity.service.FolderService;
import me.universi.competence.entities.CompetenceType;
import me.universi.group.entities.Group;
import me.universi.image.entities.ImageMetadata;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;

import org.hibernate.annotations.*;

@Entity(name="Folder")
@Table( name = "folder", schema = "capacity" )
@SQLDelete(sql = "UPDATE capacity.folder SET deleted = true WHERE id=?")
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

    @Nullable
    @OneToOne
    @JoinColumn( name = "image_metadata_id" )
    private ImageMetadata image;

    @Column
    @Size(max = 200)
    private String description;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable( name = "folder_categories", schema = "capacity" )
    private Collection<Category> categories;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToMany( mappedBy = "folder" )
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<FolderContents> folderContents;

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
        schema = "capacity",
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
        schema = "capacity",
        joinColumns = @JoinColumn(name = "folder_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_type_id")
    )
    private Collection<CompetenceType> grantsBadgeToCompetences;

    public Folder() {
        this.assignedUsers = new ArrayList<>();
        this.favoriteUsers = new ArrayList<>();
        this.grantsBadgeToCompetences = new ArrayList<>();
        this.grantedAccessGroups = new ArrayList<>();
        this.folderContents = new ArrayList<>();
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

    public @Nullable ImageMetadata getImage() { return image; }
    public void setImage(ImageMetadata image) { this.image = image; }

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

    public void setContents(Collection<FolderContents> folderContents) {
        this.folderContents = folderContents;
    }

    public Collection<FolderContents> getFolderContents() {
        return this.folderContents;
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

    public Collection<FolderFavorite> getFavoriteUsers() { return favoriteUsers; }
    public void setFavoriteUsers(Collection<FolderFavorite> favoriteUsers) { this.favoriteUsers = favoriteUsers; }

    @Transient
    public boolean isCanEdit() {
        return FolderService.getInstance().hasPermissionToEdit( this );
    }

    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Profile> getAssignedBy() {
        return this.assignedUsers.stream()
            .filter(u -> ProfileService.getInstance().isSessionOfProfile(u.getAssignedTo()))
            .map( fp -> fp.getAssignedBy() )
            .filter(Objects::nonNull)
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
