package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

@Entity(name="contentstatus")
public class ContentStatus {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContentStatusType status;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name="content_id")
    private Content content;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="profile_id")
    private Profile profile;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

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
}
