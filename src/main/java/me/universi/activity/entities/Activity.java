package me.universi.activity.entities;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.activity.enums.ActivityStatus;
import me.universi.competence.entities.CompetenceType;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.profile.entities.Profile;

@Entity( name = "Activity" )
@Table( schema = "activity", name = "activity" )
@SQLDelete( sql = "UPDATE activity.activity SET deleted_at = NOW() WHERE id = ?" )
@SQLRestriction( "deleted_at IS NULL" )
@Schema( description = "Stores data related to events external to the Universi.me system, like meetings and workshops" )
public class Activity {
    @Id
    @NotNull
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "id", nullable = false )
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "type_id", nullable = false )
    private ActivityType type;

    @NotBlank
    @Column( name = "location", nullable = false )
    @Schema(
        description = "Where this Activity will take place, usually with an address or meeting URL and time of the day",
        examples = { "At 5555 Example Street in Example City at 3 p.m.", "https://fake.example.site/meeting-id at 7 a.m." }
    )
    private String location;

    @Nullable
    @Column( name = "workload", nullable = true )
    @Schema( description = "How many hours will this Activity take", example = "3" )
    private Integer workload;

    @NotNull
    @Temporal( TemporalType.DATE )
    @Column(name = "start_date")
    @Schema( description = "The date this Activity takes place or starts, if it takes multiple days" )
    private Date startDate;

    @NotNull
    @Temporal( TemporalType.DATE )
    @Column(name = "end_date")
    @Schema( description = "The date this Activity ends. Should always be after `startDate` or equal if this event takes place on the same day" )
    private Date endDate;

    @NotNull
    @OneToOne( mappedBy = "activity", fetch = FetchType.LAZY)
    @JsonIgnoreProperties( { "activity" } )
    @Schema( description = "The group used by this Activity to store relevant data, such as name, description, Contents, participants etc." )
    private Group group;

    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        schema = "activity",
        name = "badges",
        joinColumns = @JoinColumn( name = "activity_id" ),
        inverseJoinColumns = @JoinColumn( name = "competence_type_id" )
    )
    @Schema( description = "The CompetenceType badges this Activity grants. A participant with a Competence with any of these types gain a Competence badge" )
    private Collection<CompetenceType> badges;

    @Nullable
    @JsonIgnore
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "deleted_at" )
    private Date deletedAt;

    @Transient
    public @NotNull ActivityStatus getStatus() {
        var now = new Date();

        // this.endDate saved at 00:00 – adding 24hrs minus 1ms for comparison
        var endDate = new Date( this.endDate.getTime() + 86_399_999 );

        if ( endDate.before( now ) )
            return ActivityStatus.ENDED;

        else if ( this.getStartDate().before( now ) )
            return ActivityStatus.STARTED;

        return ActivityStatus.NOT_STARTED;
    }

    public Activity() { }

    public UUID getId() { return id; }

    @Transient @JsonIgnore public @NotBlank String getName() { return getGroup().getName(); }
    @Transient @JsonIgnore public @NotBlank String getDescription() { return getGroup().getDescription(); }
    @Transient @JsonIgnore public Profile getAuthor() { return getGroup().getAdmin(); }
    @Transient @JsonIgnore public @NotNull Collection<ProfileGroup> getParticipants() { return getGroup().getParticipants(); }

    public @NotNull ActivityType getType() { return type; }
    public void setType( @NotNull ActivityType activityType ) { this.type = activityType; }

    public @NotNull Collection<CompetenceType> getBadges() { return badges; }
    public void setBadges(@NotNull Collection<CompetenceType> badges) { this.badges = badges; }

    public @NotBlank String getLocation() { return location; }
    public void setLocation( @NotBlank String location ) { this.location = location; }

    public Optional<Integer> getWorkload() { return Optional.ofNullable( workload ); }
    @JsonSetter public void setWorkload( @Nullable Integer workload ) { this.workload = workload; }
    public void setWorkload( Optional<Integer> workload ) { this.workload = workload.orElse( null ); }

    public @NotNull Date getStartDate() { return startDate; }
    public void setStartDate( @NotNull Date startDate ) { this.startDate = startDate; }

    public @NotNull Date getEndDate() { return endDate; }
    public void setEndDate( @NotNull Date endDate ) { this.endDate = endDate; }

    public @NotNull Group getGroup() { return group; }
    public void setGroup( @NotNull Group group ) { this.group = group; }

    public @Nullable Date getDeletedAt() { return deletedAt; }
    public void setDeletedAt( @Nullable Date deletedAt ) { this.deletedAt = deletedAt; }
}
