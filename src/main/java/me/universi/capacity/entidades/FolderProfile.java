package me.universi.capacity.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.service.FolderService;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.*;

@Entity(name = "FolderProfile")
@Table( name = "folder_profile", schema = "capacity" )
@SQLDelete( sql = "UPDATE capacity.folder_profile SET removed = CURRENT_TIMESTAMP WHERE id=?" )
@SQLRestriction( "removed IS NULL" )
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Schema( description = "A assignment of a Folder to a Profile by another Profile" )
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
    @Schema( description = "When this folder was assigned" )
    public Date created;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "removed")
    public Date removed;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="assigned_by_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    @Schema( description = "The Profile who assigned the Folder" )
    public Profile assignedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="assigned_to_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    @Schema( description = "The Profile whose the Folder was assigned to" )
    public Profile assignedTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="folder_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    @Schema( description = "The assigned Folder" )
    public Folder folder;

    public FolderProfile() {
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getRemoved() { return removed; }
    public void setRemoved(Date removed) { this.removed = removed; }

    public Profile getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Profile assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Profile getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Profile assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    @Transient
    public int getFolderSize() {
        return this.folder.getFolderContents().size();
    }

    @Transient
    public int getDoneUntilNow() {
        return FolderService.getInstance().getStatuses(assignedTo, folder).stream()
            .filter(cs -> cs.getStatus().equals(ContentStatusType.DONE))
            .filter(Objects::nonNull)
            .toList()
            .size();
    }
}
