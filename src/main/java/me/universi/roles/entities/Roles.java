package me.universi.roles.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import me.universi.group.entities.Group;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.enums.RoleType;
import me.universi.roles.exceptions.RolesException;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "roles")
@SQLDelete(sql = "UPDATE roles SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Roles implements Serializable {

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
    public Group group;

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

    @Column(name = "role_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    public Roles() {
    }

    private Roles(@NotNull String name, String description, @NotNull Group group, @NotNull RoleType roleType, int permission) {
        this.name = name;
        this.description = description;
        this.group = group;
        this.roleType = roleType;

        Arrays.asList(FeaturesTypes.values())
            .forEach(ft -> this.setPermission(ft, permission));
    }

    public static Roles makeAdmin(@NotNull Group group) {
        return new Roles("Administrador", null, group, RoleType.ADMINISTRATOR, Permission.READ_WRITE_DELETE);
    }

    public static Roles makeParticipant(@NotNull Group group) {
        return new Roles("Participante", null, group, RoleType.PARTICIPANT, Permission.READ);
    }

    public static Roles makeVisitor(@NotNull Group group) {
        return new Roles("Visitante", null, group, RoleType.VISITOR, Permission.READ);
    }

    public static Roles makeCustom(@NotNull String name, String description, @NotNull Group group, int permission) {
        return new Roles(name, description, group, RoleType.CUSTOM, permission);
    }

    @Override
    public String toString() {
        return "\nRoles[" +
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
        return this.roleType == RoleType.CUSTOM;
    }

    @Transient
    public boolean isCanBeAssigned() {
        return this.roleType != RoleType.VISITOR;
    }

    @Transient @JsonIgnore
    public int getPermissionForFeature(FeaturesTypes feature) {
        switch (feature) {
            case FEED:       return this.feedPermission;
            case CONTENT:    return this.contentPermission;
            case GROUP:      return this.groupPermission;
            case PEOPLE:     return this.peoplePermission;
            case COMPETENCE: return this.competencePermission;

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

        else
            throw new RolesException("FeaturesTypes '" + feature + "' não existe");
    }

    @Transient
    public Map<FeaturesTypes, Integer> getPermissions() {
        return Arrays.asList(FeaturesTypes.values())
            .stream()
            .collect(Collectors.toMap(ft -> ft, this::getPermissionForFeature));
    }
}
