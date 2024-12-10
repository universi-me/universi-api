package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.*;


@Entity(name = "subgroup")
@SQLDelete(sql = "UPDATE subgroup SET deleted = true WHERE id=?")
@SQLRestriction( value = "NOT deleted" )
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Subgroup implements Serializable {

    @Serial
    private static final long serialVersionUID = -2163545341342344343L;

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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="group_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    public Group group;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="subgroup_id")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    public Group subgroup;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    public Subgroup() {
    }

    public Date getAdded() {
        return this.added;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Group getSubgroup() {
        return this.subgroup;
    }

    public void setSubgroup(Group subgroup) {
        this.subgroup = subgroup;
    }
}
