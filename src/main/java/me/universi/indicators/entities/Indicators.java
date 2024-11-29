package me.universi.indicators.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import me.universi.achievement.entities.Achievement;
import me.universi.profile.entities.Profile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "indicators")
@SQLDelete(sql = "UPDATE indicators SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Indicators implements Serializable {

    @Serial
    private static final long serialVersionUID = -4697933506834446148L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @NotNull
    private Long score;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "indicators")
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Achievement> achievements;

    @OneToOne
    private Profile profile;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Indicators(Long score, Set<Achievement> achievements, Profile profile) {
        this.score = score;
        this.achievements = achievements;
        this.profile = profile;
    }

    public Indicators() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Set<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(Set<Achievement> achievements) {
        this.achievements = achievements;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

}
