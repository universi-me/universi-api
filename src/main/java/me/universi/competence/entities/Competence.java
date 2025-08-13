package me.universi.competence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.universi.activity.entities.Activity;
import me.universi.activity.services.ActivityService;
import me.universi.profile.entities.Profile;

import org.hibernate.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity( name = "Competence" )
@Table( name = "competence", schema = "competence" )
@SQLDelete(sql = "UPDATE competence.competence SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema( description = "A self-declared measurement of a Profile's proficiency on a subject" )
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(name = "competence_type_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private CompetenceType competenceType;

    @JoinColumn( name = "profile_id" )
    @ManyToOne
    private Profile profile;

    @Column(name = "description", columnDefinition = "TEXT")
    @Schema( description = "A short plain text description of this Competence" )
    private String description;

    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;
    @Column(name = "level")
    @Min( MIN_LEVEL ) @Max( MAX_LEVEL )
    @Schema( description = "The level of the Profile's proficiency in the CompetenceType" )
    private int level;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @Transient
    @Schema( description = "If true, this profile has a Competence badge of this CompetenceType for completing a Folder with this CompetenceType" )
    public boolean isHasBadge() {
        return this.profile.hasBadge( competenceType );
    }

    @Transient
    @Schema( description = "All Activities this Profile participates which lists this CompetenceType" )
    public List<Activity> getActivities() {
        return ActivityService
            .getInstance()
            .findByProfileAndCompetenceType( profile, competenceType );
    }

    public Competence() {}

    public Competence( CompetenceType competenceType, String description, int level, Profile profile ) {
        this.competenceType = competenceType;
        this.description = description;
        this.level = level;
        this.profile = profile;
    }

    public UUID getId() { return id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public CompetenceType getCompetenceType() { return competenceType; }
    public void setCompetenceType(CompetenceType competenceType) { this.competenceType = competenceType; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
}
