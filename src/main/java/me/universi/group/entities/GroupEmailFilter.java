package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

import me.universi.group.enums.GroupEmailFilterType;
import org.hibernate.annotations.*;


@Entity(name = "GroupEmailFilter")
@Table( name = "group_email_filter", schema = "system_group" )
@SQLDelete(sql = "UPDATE system_group.group_email_filter SET deleted = true WHERE id=?")
@SQLRestriction( value = "NOT deleted" )
@Schema( description = "A filter to be applied to validate new user's emails" )
public class GroupEmailFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -31637842245354343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @Column(name = "enabled")
    @Schema( description = "If false, this filter will not be applied" )
    public boolean enabled = Boolean.FALSE;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public GroupEmailFilterType type = GroupEmailFilterType.END_WITH;

    @Column(name = "email")
    @Schema( description = "The filter declared" )
    public String email;

    @CreationTimestamp
    @Column(name = "added")
    public LocalDateTime added;

    @JsonIgnore
    @Column(name = "removed")
    public LocalDateTime removed;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_settings_id")
    @NotNull
    private GroupSettings groupSettings;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public GroupEmailFilter() {
    }

    public boolean matches( String email ) {
        if ( this.email == null ) return false;

        switch ( this.type ) {
            case END_WITH:
                return email.endsWith( this.email );

            case START_WITH:
                return email.startsWith( this.email );

            case CONTAINS:
                return email.contains( this.email );

            case EQUALS:
                return email.equals( this.email );

            case MASK:
                return Pattern.compile( this.email.replace( "*" , "(.*)" ) ).matcher( email ).find();

            case REGEX:
                return Pattern.compile( this.email ).matcher( email ).find();

            default:
                return false;
        }
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public UUID getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public GroupEmailFilterType getType() {
        return type;
    }

    public void setType(GroupEmailFilterType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRemoved() {
        return removed;
    }

    public void setRemoved(LocalDateTime removed) {
        this.removed = removed;
    }

    public GroupSettings getGroupSettings() {
        return groupSettings;
    }

    public void setGroupSettings(GroupSettings groupSettings) {
        this.groupSettings = groupSettings;
    }

    public LocalDateTime getAdded() {
        return added;
    }

    @JsonIgnore
    public Group getGroup() { return getGroupSettings().getGroup(); }
}
