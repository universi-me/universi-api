package me.universi.roles.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import me.universi.group.entities.Group;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.enums.RoleType;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;
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

    @OneToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    public Collection<RolesFeature> rolesFeatures;

    @Column(name = "role_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    public Roles() {
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

    @Transient
    @JsonIgnore
    public int getPermissionForFeature(FeaturesTypes feature) {
        var featureType = this.rolesFeatures.stream()
            .filter(ft -> ft.featureType == feature)
            .findFirst();

        if (featureType.isPresent())
            return featureType.get().permission;

        else
            return Permission.NONE;
    }
}
