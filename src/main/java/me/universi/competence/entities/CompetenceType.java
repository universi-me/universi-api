package me.universi.competence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.profile.entities.Profile;

import java.util.Collection;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "competence_type")
@SQLDelete(sql = "UPDATE competence_type SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
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
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "competence_type_profiles_with_access",
        joinColumns = @JoinColumn(name = "competence_type_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
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
