package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import me.universi.user.services.JsonUserAdminFilter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "GroupEnvironment")
@Table( name = "group_environment", schema = "system_group" )
@SQLDelete(sql = "UPDATE system_group.group_environment SET deleted = true WHERE id=?")
@SQLRestriction( value = "NOT deleted" )
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

    /** Signup **/
    @Column(name = "signup_enabled")
    public boolean signup_enabled = Boolean.TRUE;
    @Column(name = "signup_confirm_account_enabled")
    public boolean signup_confirm_account_enabled = Boolean.FALSE;
    @Column(name = "recovery_enabled")
    public boolean recovery_enabled = Boolean.TRUE;

    /** Google Oauth Login **/
    @Column(name = "login_google_enabled")
    public boolean login_google_enabled = Boolean.FALSE;
    @Column(name = "google_client_id")
    public String google_client_id;

    /** Google Recaptcha **/
    @Column(name = "recaptcha_enabled")
    public boolean recaptcha_enabled = Boolean.FALSE;
    @Column(name = "recaptcha_api_key")
    public String recaptcha_api_key;
    @Column(name = "recaptcha_api_project_id")
    public String recaptcha_api_project_id;
    @Column(name = "recaptcha_site_key")
    public String recaptcha_site_key;

    /** Keycloak **/
    @Column(name = "keycloak_enabled")
    public boolean keycloak_enabled = Boolean.FALSE;
    @Column(name = "keycloak_client_id")
    public String keycloak_client_id;
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserAdminFilter.class)
    @Column(name = "keycloak_client_secret")
    public String keycloak_client_secret;
    @Column(name = "keycloak_realm")
    public String keycloak_realm;
    @Column(name = "keycloak_url")
    public String keycloak_url;
    @Column(name = "keycloak_redirect_url")
    public String keycloak_redirect_url;

    /** Email **/
    @Column(name = "email_enabled")
    public boolean email_enabled = Boolean.FALSE;
    @Column(name = "email_host")
    public String email_host;
    @Column(name = "email_port")
    public String email_port;
    @Column(name = "email_protocol")
    public String email_protocol;
    @Column(name = "email_username")
    public String email_username;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserAdminFilter.class)
    @Column(name = "email_password")
    public String email_password;

    /** Email Notifications **/
    @Column(name = "message_new_content_enabled")
    public boolean alert_new_content_enabled = Boolean.TRUE;
    @Column(name = "message_template_new_content")
    public String message_template_new_content;
    @Column(name = "message_assigned_content_enabled")
    public boolean alert_assigned_content_enabled = Boolean.TRUE;
    @Column(name = "message_template_assigned_content")
    public String message_template_assigned_content;

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
