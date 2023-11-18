package me.universi.group.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "group_admin")
@SQLDelete(sql = "UPDATE group_admin SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class GroupAdmin implements Serializable {

    @Serial
    private static final long serialVersionUID = -1163545342242399343L;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    public UUID id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "added")
    public Date added;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "removed")
    public Date removed;

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="profile_id")
    @NotNull
    public Profile profile;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="group_id")
    @NotNull
    public Group group;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    public GroupAdmin() {
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Date getAdded() {
        return this.added;
    }

    public void setDeleted(boolean b) {
        this.deleted = b;
    }

    public boolean getDeleted() {
        return this.deleted;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public Date getRemoved() {
        return this.removed;
    }

    public void setRemoved(Date removed) {
        this.removed = removed;
    }
}
