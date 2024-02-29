package me.universi.papers.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import me.universi.group.entities.Group;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "paper_profile")
@SQLDelete(sql = "UPDATE paper_profile SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class PaperProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = -31637842245354343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    public UUID id;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    @CreationTimestamp
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    public Date created;

    @JsonIgnore
    @Column(name = "removed")
    @Temporal(TemporalType.TIMESTAMP)
    public Date removed;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name="paper_id")
    @NotNull
    public Paper paper;

    @JsonIgnore
    @JoinColumn(name= "group_id")
    @OneToOne(fetch = FetchType.EAGER)
    @NotNull
    public Group group;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name="profile_id")
    @OneToOne(fetch = FetchType.EAGER)
    @NotNull
    public Profile profile;

    public PaperProfile() {
    }
}
