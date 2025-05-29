package me.universi.activity.entities;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import me.universi.competence.entities.CompetenceType;
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

    @NotBlank
    @Column( name = "name", nullable = false )
    private String name;

    @NotBlank
    @Column( name = "description", nullable = false )
    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn( name = "author_id", nullable = false )
    private Profile author;

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
    @JsonIgnore
    @OneToMany( mappedBy = "activity", fetch = FetchType.LAZY )
    private Collection<ActivityParticipant> participants;

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

    public @NotBlank String getName() { return name; }
    public void setName(@NotBlank String name) { this.name = name; }

    public @NotBlank String getDescription() { return description; }
    public void setDescription(@NotBlank String description) { this.description = description; }

    public Profile getAuthor() { return author; }
    public void setAuthor(Profile author) { this.author = author; }

    public @NotNull ActivityType getType() { return type; }
    public void setType( @NotNull ActivityType activityType ) { this.type = activityType; }

    public @NotNull Collection<ActivityParticipant> getParticipants() { return participants; }
    public void setParticipants(@NotNull Collection<ActivityParticipant> participants) { this.participants = participants; }

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

    public @Nullable Date getDeletedAt() { return deletedAt; }
}
