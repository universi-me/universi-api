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
import org.hibernate.annotations.*;


@Entity(name = "profile_group")
@SQLDelete(sql = "UPDATE profile_group SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ProfileGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = -9163545341342344343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    public UUID id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "joined")
    public Date joined;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exited")
    public Date exited;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="profile_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    public Profile profile;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="group_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    public Group group;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    public ProfileGroup() {
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

    public Date getJoined() {
        return this.joined;
    }
}
