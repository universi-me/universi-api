package me.universi.group.entities;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.group.enums.GroupTypeKind;

@Entity(name = "GroupType")
@Table( name = "type", schema = "system_group" )
@SQLDelete( sql = "UPDATE system_group.type SET deleted_at = NOW() WHERE id=?" )
@SQLRestriction( value = "deleted_at IS NULL" )
@Schema( description = "Describes which type the Group is" )
public class GroupType {
    @Id
    @NotNull
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "id", nullable = false )
    private UUID id;

    @NotBlank
    @Column( name = "label", nullable = false )
    @Schema( description = "The name of this GroupType. Unique amongst other GroupTypes", examples = { "Department", "Team", "Campus" } )
    private String label;

    @Nullable
    @JsonIgnore
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "deleted_at" )
    private Date deletedAt;

    @NotNull
    @JsonIgnore
    @Column( name = "kind" )
    @Enumerated( EnumType.STRING )
    private GroupTypeKind kind;

    public GroupType() { }

    public @NotBlank String getLabel() { return label; }
    public void setLabel( @NotBlank String label ) { this.label = label; }

    public UUID getId() { return id; }
    public @Nullable Date getDeletedAt() { return deletedAt; }

    public @NotNull GroupTypeKind getKind() { return kind; }

    @Schema( description = "If true, this GroupType can be assigned to Groups as normal. Otherwise, it's restricted and can only be assigned automatically" )
    public boolean isCanBeAssigned() { return kind == GroupTypeKind.REGULAR; }

    @Schema( description = "If true, this GroupType can be deleted by a system administrator" )
    public boolean isCanBeDeleted() { return kind == GroupTypeKind.REGULAR; }
}
