package me.universi.competence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import me.universi.profile.entities.Profile;

import java.util.UUID;

@Entity( name = "CompetenceProfile" )
@Table( name = "competence_profile" )
public class CompetenceProfile {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private @NotNull UUID id;

    @JoinColumn( name = "profile_id" )
    @ManyToOne( optional = false )
    private Profile profile;

    @JoinColumn( name = "competence_id" )
    @ManyToOne( optional = false )
    private Competence competence;

    public CompetenceProfile( ) { }
    public CompetenceProfile(Profile profile, Competence competence) {
        this.profile = profile;
        this.competence = competence;
    }

    public UUID getId() { return id; }
    public void setId(@NotNull UUID id) { this.id = id; }

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }

    public Competence getCompetence() { return competence; }
    public void setCompetence(Competence competence) { this.competence = competence; }
}
