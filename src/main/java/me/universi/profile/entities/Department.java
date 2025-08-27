package me.universi.profile.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity( name = "Department" )
@Table( name = "department", schema = "profile" )
@SQLDelete( sql = "UPDATE profile.department SET deleted_at = NOW() WHERE id = ?" )
@SQLRestriction( "deleted_at IS NULL" )
@Schema( description = "A subdivision of the company this platform represents, used to group and filter users" )
public class Department implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "acronym", nullable = false)
    @Schema( description = "An acronym for this Department", example = "EDN" )
    private String acronym;

    @Column(name = "name", nullable = false)
    @Schema( description = "The name of this Department", example = "Example Department Name" )
    private String name;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date creationDate;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_at", nullable = true)
    @Nullable
    private Date deleted;

    public Department() {}
    public Department(String acronym, String name) {
        this.acronym = acronym;
        this.name = name;
    }

    public UUID getId() { return id; }

    public String getAcronym() { return acronym; }
    public void setAcronym(String acronym) { this.acronym = acronym; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getCreationDate() { return creationDate; }
    public Date getDeleted() { return deleted; }
}
