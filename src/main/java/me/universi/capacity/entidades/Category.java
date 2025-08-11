package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

import me.universi.image.entities.ImageMetadata;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.*;

import java.util.Date;
import java.util.UUID;

@Entity( name="Category" )
@Table( name = "category", schema = "capacity" )
@SQLDelete(sql = "UPDATE capacity.category SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
@Schema( name = "Category", description = "Category used as a tag on Content and Folder" )
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = -1963545345342344343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    @Size(max = 100)
    @Schema(
        description = "The name of this Category. It is unique between all other categories",
        examples = { "Python", "Arquitetura de Software", "BPMN", }
    )
    private String name;

    @Nullable
    @OneToOne
    @JoinColumn( name = "image_metadata_id" )
    private ImageMetadata image;

    @JsonIgnore
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="profile_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    private Profile author;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Category() {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
