package me.universi.indicators.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import me.universi.achievement.entities.Achievement;
import me.universi.user.entities.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Indicators implements Serializable {

    @Serial
    private static final long serialVersionUID = -4697933506834446148L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicators_generator")
    @SequenceGenerator(name = "indicators_generator", sequenceName = "indicators_sequence", allocationSize = 1)
    private Long id;

    @NotNull
    private Long score;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "indicators")
    private Set<Achievement> achievements;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Indicators(Long score, Set<Achievement> achievements, User user) {
        this.score = score;
        this.achievements = achievements;
        this.user = user;
    }

    public Indicators() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
