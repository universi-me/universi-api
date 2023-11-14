package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity(name = "subgroup")
@SQLDelete(sql = "UPDATE subgroup SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Subgroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    public UUID id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "added")
    public Date added;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "removed")
    public Date removed;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="group_id")
    @NotNull
    public Group group;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="subgroup_id")
    @NotNull
    public Group subgroup;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    public Subgroup() {
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
