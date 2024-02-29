package me.universi.papers.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import me.universi.group.entities.Group;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "paper")
@SQLDelete(sql = "UPDATE paper SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Paper implements Serializable {

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

    @OneToMany(mappedBy = "paper")
    public Collection<PaperFeature> paperFeatures;

    public Paper() {
    }

    @Override
    public String toString() {
        return "\nPaper[" +
                "id='" + id + '\'' +
                ", name='" + name + "']\n";
    }
}
