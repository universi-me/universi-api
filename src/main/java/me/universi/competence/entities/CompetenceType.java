package me.universi.competence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import me.universi.profile.entities.Profile;

import java.util.Collection;
import java.util.UUID;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "CompetenceType")
@Table( name = "competence_type", schema = "competence" )
@SQLDelete(sql = "UPDATE competence.competence_type SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompetenceType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @Column(name = "name", unique=true)
    private String name;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @Column(name = "reviewed")
    private boolean reviewed = Boolean.FALSE;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "competence_type_profiles_with_access",
        schema = "competence",
        joinColumns = @JoinColumn(name = "competence_type_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Profile> profilesWithAccess;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public boolean isReviewed() { return reviewed; }

    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }

    public Collection<Profile> getProfilesWithAccess() { return profilesWithAccess; }
    public void setProfilesWithAccess(Collection<Profile> profilesWithAccess) { this.profilesWithAccess = profilesWithAccess; }
    public void addProfileWithAccess(Profile profile) { this.profilesWithAccess.add(profile); }
}
