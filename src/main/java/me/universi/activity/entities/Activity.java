package me.universi.activity.entities;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import me.universi.competence.entities.CompetenceType;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.profile.entities.Profile;

@Entity( name = "Activity" )
@Table( schema = "activity", name = "activity" )
@SQLDelete( sql = "UPDATE activity.activity SET deleted_at = NOW() WHERE id = ?" )
@SQLRestriction( "deleted_at IS NULL" )
public class Activity {
    @Id
    @NotNull
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "id", nullable = false )
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn( name = "type_id", nullable = false )
    private ActivityType type;

    @NotBlank
    @Column( name = "location", nullable = false )
    private String location;

    @NotNull
    @Column( name = "workload", nullable = false )
    private Integer workload;

    @NotNull
    @Temporal( TemporalType.DATE )
    @Column(name = "start_date")
    private Date startDate;

    @NotNull
    @Temporal( TemporalType.DATE )
    @Column(name = "end_date")
    private Date endDate;

    @NotNull
    @OneToOne( mappedBy = "activity" )
    @JsonIgnoreProperties( { "activity" } )
    private Group group;

    @NotNull
    @ManyToMany
    @JoinTable(
        schema = "activity",
        name = "badges",
        joinColumns = @JoinColumn( name = "activity_id" ),
        inverseJoinColumns = @JoinColumn( name = "competence_type_id" )
    )
    private Collection<CompetenceType> badges;

    @Nullable
    @JsonIgnore
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "deleted_at" )
    private Date deletedAt;

    public Activity() { }

    public UUID getId() { return id; }

    @Transient @JsonIgnore public @NotBlank String getName() { return group.getName(); }
    @Transient @JsonIgnore public @NotBlank String getDescription() { return group.getDescription(); }
    @Transient @JsonIgnore public Profile getAuthor() { return group.getAdmin(); }
    @Transient @JsonIgnore public @NotNull Collection<ProfileGroup> getParticipants() { return group.getParticipants(); }

    public @NotNull ActivityType getType() { return type; }
    public void setType( @NotNull ActivityType activityType ) { this.type = activityType; }

    public @NotNull Collection<CompetenceType> getBadges() { return badges; }
    public void setBadges(@NotNull Collection<CompetenceType> badges) { this.badges = badges; }

    public @NotBlank String getLocation() { return location; }
    public void setLocation( @NotBlank String location ) { this.location = location; }

    public @NotNull Integer getWorkload() { return workload; }
    public void setWorkload( @NotNull Integer workload ) { this.workload = workload; }

    public @NotNull Date getStartDate() { return startDate; }
    public void setStartDate( @NotNull Date startDate ) { this.startDate = startDate; }

    public @NotNull Date getEndDate() { return endDate; }
    public void setEndDate( @NotNull Date endDate ) { this.endDate = endDate; }

    public @NotNull Group getGroup() { return group; }
    public void setGroup( @NotNull Group group ) { this.group = group; }

    public @Nullable Date getDeletedAt() { return deletedAt; }
}
