package me.universi.capacity.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import me.universi.profile.entities.Profile;

@Entity(name = "FolderFavorite")
@Table( name = "folder_favorite", schema = "capacity" )
@SQLDelete(sql = "UPDATE capacity.folder_favorite SET deleted = true, removed = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction( "NOT deleted" )
public class FolderFavorite implements Serializable {
    private static final long serialVersionUID = 2134355147415946228L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="profile_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    private Profile profile;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="folder_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    private Folder folder;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "removed")
    private Date removed;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public FolderFavorite() { /* Default constructor */ }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }

    public Folder getFolder() { return folder; }
    public void setFolder(Folder folder) { this.folder = folder; }

    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }

    public Date getRemoved() { return removed; }
    public void setRemoved(Date removed) { this.removed = removed; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
