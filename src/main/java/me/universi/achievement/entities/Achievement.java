package me.universi.achievement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotNull;
import me.universi.indicators.entities.Indicators;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity(name = "achievement")
@SQLDelete(sql = "UPDATE achievement SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Achievement implements Serializable {

    @Serial
    private static final long serialVersionUID = 6323683588919800286L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

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
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Indicators> indicators;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Achievement(UUID id, String icon, String title, String description) {
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
