package me.universi.role.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

import me.universi.api.exceptions.UniversiUnprocessableOperationException;
import me.universi.group.entities.Group;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.enums.RoleType;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity( name = "Role" )
@Table( name = "role", schema = "system_group" )
@SQLDelete( sql = "UPDATE role SET deleted = true WHERE id=?" )
@SQLRestriction( "NOT deleted" )
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = -7368873462832313132L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    public UUID id;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    @CreationTimestamp
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    public Date created;

    @JsonIgnore
    @Column(name = "removed")
    @Temporal(TemporalType.TIMESTAMP)
    public Date removed;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @NotNull
    private Group group;

    @Column(name= "feed_permission") @JsonIgnore
    @Min(0) @NotNull public int feedPermission = 0;

    @Column(name= "content_permission") @JsonIgnore
    @Min(0) @NotNull public int contentPermission = 0;

    @Column(name= "group_permission") @JsonIgnore
    @Min(0) @NotNull public int groupPermission = 0;

    @Column(name= "people_permission") @JsonIgnore
    @Min(0) @NotNull public int peoplePermission = 0;

    @Column(name= "competence_permission") @JsonIgnore
    @Min(0) @NotNull public int competencePermission = 0;

    @Column(name= "job_permission") @JsonIgnore
    @Min(0) @NotNull public int jobPermission = 0;

    @Column(name = "role_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    public Role() {
    }

    private Role(@NotNull String name, String description, @NotNull Group group, @NotNull RoleType roleType, int permission) {
        this.name = name;
        this.description = description;
        this.group = group;
        this.roleType = roleType;

        Arrays.asList(FeaturesTypes.values())
            .forEach(ft -> this.setPermission(ft, permission));
    }

    public Group getGroup() {
        return group;
    }

    public static Role makeAdmin(@NotNull Group group) {
        return new Role("Administrador", null, group, RoleType.ADMINISTRATOR, Permission.READ_WRITE_DELETE);
    }

    public static Role makeParticipant(@NotNull Group group) {
        return new Role("Participante", null, group, RoleType.PARTICIPANT, Permission.READ);
    }

    public static Role makeVisitor(@NotNull Group group) {
        return new Role("Visitante", null, group, RoleType.VISITOR, Permission.READ);
    }

    public static Role makeCustom(@NotNull String name, String description, @NotNull Group group, int permission) {
        return new Role(name, description, group, RoleType.CUSTOM, permission);
    }

    @Override
    public String toString() {
        return "\nRole[" +
                "id='" + id + '\'' +
                ", name='" + name + "']\n";
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    @Transient
    public boolean isCanBeEdited() {
        return this.isCustom();
    }
    @Transient
    public boolean isCanBeAssigned() {
        return !this.isVisitor();
    }

    @Transient @JsonIgnore public boolean isAdmin() { return this.roleType == RoleType.ADMINISTRATOR; }
    @Transient @JsonIgnore public boolean isParticipant() { return this.roleType == RoleType.PARTICIPANT; }
    @Transient @JsonIgnore public boolean isVisitor() { return this.roleType == RoleType.VISITOR; }
    @Transient @JsonIgnore public boolean isCustom() { return this.roleType == RoleType.CUSTOM; }

    @Transient @JsonIgnore
    public int getPermissionForFeature(FeaturesTypes feature) {
        switch (feature) {
            case FEED:       return this.feedPermission;
            case CONTENT:    return this.contentPermission;
            case GROUP:      return this.groupPermission;
            case PEOPLE:     return this.peoplePermission;
            case COMPETENCE: return this.competencePermission;
            case JOBS:       return this.jobPermission;

            // default case is necessary for compilation
            // If another FeatureTypes is added, add a case above
            default:
                return Permission.NONE;
        }
    }

    public void setPermission(FeaturesTypes feature, int permission) {
        if (feature == FeaturesTypes.FEED)
            this.feedPermission = permission;

        else if (feature == FeaturesTypes.CONTENT)
            this.contentPermission = permission;

        else if (feature == FeaturesTypes.GROUP)
            this.groupPermission = permission;

        else if (feature == FeaturesTypes.PEOPLE)
            this.peoplePermission = permission;

        else if (feature == FeaturesTypes.COMPETENCE)
            this.competencePermission = permission;

        else if (feature == FeaturesTypes.JOBS)
            this.jobPermission = permission;

        else
            throw new UniversiUnprocessableOperationException("FeaturesTypes '" + feature + "' não existe");
    }

    @Transient
    public Map<FeaturesTypes, Integer> getPermissions() {
        return Arrays.asList(FeaturesTypes.values())
            .stream()
            .collect(Collectors.toMap(ft -> ft, this::getPermissionForFeature));
    }
}
