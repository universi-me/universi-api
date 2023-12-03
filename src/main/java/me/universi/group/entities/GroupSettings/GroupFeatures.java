package me.universi.group.entities.GroupSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "group_features")
@SQLDelete(sql = "UPDATE group_features SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class GroupFeatures  implements Serializable {

    @Serial
    private static final long serialVersionUID = -31637842245354343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @JoinColumn(name="group_settings_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    public GroupSettings groupSettings;

    @Column(name = "name")
    @NotNull
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "enabled")
    public boolean enabled = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "added")
    @Temporal(TemporalType.TIMESTAMP)
    public Date added;

    @JsonIgnore
    @Column(name = "removed")
    @Temporal(TemporalType.TIMESTAMP)
    public Date removed;

    public GroupFeatures() {
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public Date getRemoved() {
        return removed;
    }

    public void setRemoved(Date removed) {
        this.removed = removed;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }
}
