package me.universi.achievement.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import me.universi.indicators.entities.Indicators;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;


@Entity
public class Achievement implements Serializable {

    @Serial
    private static final long serialVersionUID = 6323683588919800286L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "achievement_generator")
    @SequenceGenerator(name = "achievement_generator", sequenceName = "achievement_sequence", allocationSize = 1)
    private Long id;

    @NotNull
    private String icon;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(name="indicators_achievement", joinColumns=
            {@JoinColumn(name="indicators_id")}, inverseJoinColumns=
            {@JoinColumn(name="achievement_id")})
    private Set<Indicators> indicators;

    public Achievement(Long id, String icon, String title, String description) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public Achievement() {
    }

    public Achievement(String icon, String title, String description) {
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getIcon(), that.getIcon()) && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIcon(), getTitle(), getDescription());
    }
}
