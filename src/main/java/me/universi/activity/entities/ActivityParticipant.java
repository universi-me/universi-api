package me.universi.activity.entities;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import me.universi.profile.entities.Profile;

@Entity( name = "ActivityParticipant" )
@Table( schema = "activity", name = "participant" )
@SQLDelete( sql = "UPDATE activity.participant SET removed_at = NOW() WHERE id = ?" )
@SQLRestriction( "removed_at IS NULL" )
public class ActivityParticipant {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "id", nullable = false )
    @NotNull
    private UUID id;

    @NotNull
    @JoinColumn( name = "activity_id" )
    @ManyToOne( optional = false )
    private Activity activity;

    @NotNull
    @JoinColumn( name = "profile_id" )
    @ManyToOne( optional = false )
    private Profile profile;

    @CreationTimestamp
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "joined_at" )
    private Date joinedAt;

    @Nullable
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "removed_at" )
    private Date removedAt;

    public ActivityParticipant() { }

    public UUID getId() { return id; }

    public @NotNull Activity getActivity() { return activity; }
    public void setActivity( @Valid @NotNull Activity activity ) { this.activity = activity; }

    public @NotNull Profile getProfile() { return profile; }
    public void setProfile( @NotNull Profile profile ) { this.profile = profile; }

    public Date getJoinedAt() { return joinedAt; }

    public @Nullable Date getRemovedAt() { return removedAt; }
}
