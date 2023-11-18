package me.universi.group.entities.GroupSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "group_features")
@SQLDelete(sql = "UPDATE group_features SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class GroupFeatures  implements Serializable {

    @Serial
    private static final long serialVersionUID = -31637842245354343L;

    @JsonIgnore
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
    @OneToOne(fetch = FetchType.EAGER)
    @NotNull
    public GroupSettings groupSettings;

    @Column(name = "contents")
    public boolean showContents = Boolean.TRUE;

    @Column(name = "groups")
    public boolean showGroups = Boolean.TRUE;

    @Column(name = "participants")
    public boolean showParticipants = Boolean.TRUE;

    public GroupFeatures() {
    }

}
