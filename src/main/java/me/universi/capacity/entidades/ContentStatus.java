package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.*;

@Entity(name="ContentStatus")
@Table( name = "content_status", schema = "capacity" )
@SQLDelete(sql = "UPDATE capacity.content_status SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
@Schema( description = "Current status of the Content for a specific Profile" )
public class ContentStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = -1763545345342344343L;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContentStatusType status;

    @JsonIgnore
    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name="content_id")
    private Content content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profile_id")
    private Profile profile;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public ContentStatus() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ContentStatusType getStatus() {
        return status;
    }

    public void setStatus(ContentStatusType status) {
        this.status = status;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
