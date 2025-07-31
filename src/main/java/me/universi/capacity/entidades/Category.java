package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

import me.universi.image.entities.ImageMetadata;
import me.universi.profile.entities.Profile;
import me.universi.util.HibernateUtil;
import org.hibernate.annotations.*;

import java.util.Date;
import java.util.UUID;

@Entity( name="Category" )
@Table( name = "category", schema = "capacity" )
@SQLDelete(sql = "UPDATE capacity.category SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = -1963545345342344343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    @Size(max = 100)
    private String name;

    @Nullable
    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn( name = "image_metadata_id" )
    private ImageMetadata image;

    @JsonIgnore
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
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

    public @Nullable ImageMetadata getImage() { return HibernateUtil.resolveLazyHibernateObject(image); }
    public void setImage(ImageMetadata image) { this.image = image; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Profile getAuthor() {
        return HibernateUtil.resolveLazyHibernateObject(author);
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
