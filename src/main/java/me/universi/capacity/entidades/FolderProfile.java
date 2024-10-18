package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.service.FolderService;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.*;

@Entity(name = "folder_profile")
@SQLDelete( sql = "UPDATE folder_profile SET deleted = true, removed = CURRENT_TIMESTAMP WHERE id=?" )
@SQLRestriction( "NOT deleted" )
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class FolderProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = -9163545341342344343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    public UUID id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    public Date created;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "removed")
    public Date removed;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="author_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    public Profile author;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="profile_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    public Profile profile;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="folder_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    public Folder folder;

    @Column(name = "assigned")
    public boolean assigned = Boolean.FALSE;

    public FolderProfile() {
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    @Transient
    public int getFolderSize() {
        return this.folder.getContents().size();
    }

    @Transient
    public int getDoneUntilNow() {
        return FolderService.getInstance().getStatuses(profile, folder).stream()
            .filter(cs -> cs.getStatus().equals(ContentStatusType.DONE))
            .toList()
            .size();
    }
}
