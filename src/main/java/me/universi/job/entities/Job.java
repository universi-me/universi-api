package me.universi.job.entities;

import java.util.Collection;
import java.util.UUID;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import me.universi.competence.entities.CompetenceType;
import me.universi.institution.entities.Institution;
import me.universi.profile.entities.Profile;

@Entity(name = "Job")
@Table(name = "job", schema = "job")
@SQLDelete(sql = "UPDATE job.job SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
public class Job {
    public static final int SHORT_DESCRIPTION_MAX_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "short_description", nullable = false, length = SHORT_DESCRIPTION_MAX_LENGTH)
    @Length(max = SHORT_DESCRIPTION_MAX_LENGTH)
    private String shortDescription;

    @Column(name = "long_description", nullable = false)
    private String longDescription;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToMany
    @JoinTable(
        name = "job_competences",
        schema = "job",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_type_id")
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<CompetenceType> requiredCompetences;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Profile author;

    @Column(name = "closed", nullable = false)
    private boolean closed;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Transient
    public boolean isOpen() {
        return !isClosed();
    }

    /* ---------------------------------------------------------------------- */
    /* ------------------ Constructors, getters and setters ----------------- */
    /* ---------------------------------------------------------------------- */

    public Job() { }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public Institution getInstitution() { return institution; }
    public void setInstitution(Institution institution) { this.institution = institution; }

    public String getLongDescription() { return longDescription; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }

    public Collection<CompetenceType> getRequiredCompetences() { return requiredCompetences; }
    public void setRequiredCompetences(Collection<CompetenceType> requiredCompetences) { this.requiredCompetences = requiredCompetences; }

    public Profile getAuthor() { return author; }
    public void setAuthor(Profile author) { this.author = author; }

    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
