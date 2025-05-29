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

    public @NotNull Collection<ActivityParticipant> getParticipants() { return participants; }
    public void setParticipants(@NotNull Collection<ActivityParticipant> participants) { this.participants = participants; }

    public @NotNull Collection<CompetenceType> getBadges() { return badges; }
    public void setBadges(@NotNull Collection<CompetenceType> badges) { this.badges = badges; }

    public @Nullable Date getDeletedAt() { return deletedAt; }
}
