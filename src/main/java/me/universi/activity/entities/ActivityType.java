package me.universi.activity.entities;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.*;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table( schema = "activity", name = "type" )
@SQLDelete( sql = "UPDATE activity.type SET deleted_at = NOW() WHERE id = ?" )
@SQLRestriction( "deleted_at IS NULL" )
public class ActivityType {
    @Id
    @NotNull
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "id", nullable = false )
    private UUID id;

    @NotBlank
    @Column( name = "name", nullable = false )
    private String name;

    @Nullable
    @JsonIgnore
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "deleted_at" )
    private Date deletedAt;

    public ActivityType() { }

    public @NotBlank String getName() { return name; }
    public void setName( @NotBlank String name ) { this.name = name; }

    public @NotNull UUID getId() { return id; }
    public @Nullable Date getDeletedAt() { return deletedAt; }
}
