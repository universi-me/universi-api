package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity(name = "profile_group")
@SQLDelete(sql = "UPDATE profile_group SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class ProfileGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    public UUID id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "joined")
    public Date joined;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exited")
    public Date exited;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="profile_id")
    @NotNull
    public Profile profile;


    @ManyToOne
    @PrimaryKeyJoinColumn(name="group_id")
    @NotNull
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
}
