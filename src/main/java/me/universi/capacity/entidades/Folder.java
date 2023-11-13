package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.universi.group.entities.Group;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name="folder")
@SQLDelete(sql = "UPDATE folder SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

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
    private Collection<Content> contents;

    @Column
    @NotNull
    @Min(0)
    @Max(5)
    private Integer rating;

    @ManyToOne
    @JoinColumn(name="profile_id")
    @NotNull
    private Profile author;

    @Column(name = "public_folder")
    @NotNull
    public boolean publicFolder;

    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<Group> grantedAccessGroups;

    @ManyToOne
    @JoinColumn(name = "owner_group_id")
    @NotNull
    private Group ownerGroup;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "users_folders",
        joinColumns = @JoinColumn(name = "folder_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id"),
        uniqueConstraints = { @UniqueConstraint(columnNames = {"folder_id", "profile_id"}) }
    )
    @JsonIgnore
    private Collection<Profile> assignedUsers;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

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

    public Group getOwnerGroup() {
        return this.ownerGroup;
    }

    public void setOwnerGroup(Group ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    public Collection<Profile> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Collection<Profile> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
