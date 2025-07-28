package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import jakarta.annotation.Nullable;

import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.enums.ContentType;
import me.universi.capacity.service.ContentService;
import me.universi.image.entities.ImageMetadata;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;

import org.hibernate.annotations.*;

@Entity(name = "Content")
@Table( name = "content", schema = "capacity" )
@SQLDelete(sql = "UPDATE capacity.content SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
public class Content implements Serializable {

    @Serial
    private static final long serialVersionUID = -1663545345342344343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "url")
    @Size(max = 2048)
    private String url;
    
    @Column(name = "title")
    @Size(max = 100)
    private String title;

    @Nullable
    @OneToOne
    @JoinColumn( name = "image_metadata_id" )
    private ImageMetadata image;

    @Column(name = "description")
    @Size(max = 200)
    private String description;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable( name = "content_categories", schema = "capacity" )
    private Collection<Category> categories;

    @ManyToMany(mappedBy = "content", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Collection<FolderContents> folderContents;

    public static final int MAX_RATING = 5;
    public static final int MIN_RATING = 0;
    @Column(name = "rating")
    @NotNull
    @Min( MIN_RATING )
    @Max( MAX_RATING )
    private Integer rating;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name="profile_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    private Profile author;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public ContentType type;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Content() {
        this.categories = new ArrayList<>();
        this.folderContents = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public @Nullable ImageMetadata getImage() { return image; }
    public void setImage(ImageMetadata image) { this.image = image; }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Category> getCategories() {
        return categories;
    }

    public void setCategories(Collection<Category> categories) {
        this.categories = categories;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Collection<FolderContents> getFolderContents() {
        return folderContents;
    }

    public void setFolderContents(Collection<FolderContents> folders) {
        this.folderContents = folders;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }

    public void setType(ContentType type){
        this.type = type;
    }

    public ContentType getType(){
        return type;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Transient
    @JsonInclude( JsonInclude.Include.NON_NULL )
    public ContentStatusType getStatus() {
        var profile = ProfileService.getInstance().getProfileInSession();
        if ( profile.isEmpty() )
            return null;

        return ContentService.getInstance()
            .findStatusById( id, profile.get().getId() )
            .getStatus();
    }
}
