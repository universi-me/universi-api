package me.universi.group.entities.GroupSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "group_environment")
@SQLDelete(sql = "UPDATE group_environment SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GroupEnvironment implements Serializable {

    @Serial
    private static final long serialVersionUID = -31637842245354343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @JoinColumn(name="group_settings_id")
    @OneToOne(fetch = FetchType.EAGER)
    @NotNull
    public GroupSettings groupSettings;

    @Column(name = "signup_enabled")
    public boolean signup_enabled = Boolean.TRUE;
    @Column(name = "signup_confirm_account_enabled")
    public boolean signup_confirm_account_enabled = Boolean.FALSE;

    @Column(name = "login_google_enabled")
    public boolean login_google_enabled = Boolean.FALSE;
    @Column(name = "google_client_id")
    public String google_client_id;

    @Column(name = "recaptcha_enabled")
    public boolean recaptcha_enabled = Boolean.FALSE;
    @Column(name = "recaptcha_api_key")
    public String recaptcha_api_key;
    @Column(name = "recaptcha_api_project_id")
    public String recaptcha_api_project_id;
    @Column(name = "recaptcha_site_key")
    public String recaptcha_site_key;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "added")
    @Temporal(TemporalType.TIMESTAMP)
    public Date added;

    @JsonIgnore
    @Column(name = "removed")
    @Temporal(TemporalType.TIMESTAMP)
    public Date removed;

    public GroupEnvironment() {
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public Date getRemoved() {
        return removed;
    }

    public void setRemoved(Date removed) {
        this.removed = removed;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }
}
