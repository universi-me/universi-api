package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import me.universi.profile.entities.Profile;
import me.universi.role.entities.Role;

import org.hibernate.annotations.*;


@Entity(name = "ProfileGroup")
@Table(name = "profile_group", schema = "system_group")
@SQLDelete(sql = "UPDATE system_group.profile_group SET deleted = true, exited = NOW() WHERE id=?")
@SQLRestriction( value = "NOT deleted" )
@Schema( description = "Unites the Group and Profile entities to store Group participants and their Role" )
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
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name="profile_id")
    @NotNull
    private Profile profile;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name="group_id")
    @NotNull
    private Group group;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @PrimaryKeyJoinColumn(name="role_id")
    @Schema( description = "Role assigned to the participant" )
    private Role role;

    public ProfileGroup() {
    }

    @Transient @JsonIgnore public boolean isAdmin() { return this.getRole().isAdmin(); }
    @Transient @JsonIgnore public boolean isParticipant() { return this.getRole().isParticipant(); }
    @Transient @JsonIgnore public boolean isVisitor() { return this.getRole().isVisitor(); }
    @Transient @JsonIgnore public boolean isCustom() { return this.getRole().isCustom(); }

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

    public @NotNull Role getRole() { return this.role; }
    public void setRole( @NotNull Role role ) { this.role = role; }
}
