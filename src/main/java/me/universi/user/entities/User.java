package me.universi.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

import java.time.LocalDateTime;
import me.universi.group.entities.Group;
import me.universi.profile.entities.Profile;
import me.universi.user.enums.Authority;
import me.universi.user.services.JsonEmailOwnerSessionFilter;
import me.universi.user.services.JsonUserAdminFilter;
import me.universi.user.services.LoginService;
import me.universi.user.services.UserService;
import me.universi.util.HibernateUtil;
import org.hibernate.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;


@Entity(name = "User")
@Table(name = "system_users", uniqueConstraints = {@UniqueConstraint(name = "system_users_username_organization_key", columnNames = {"username", "organization"})})
@SQLDelete(sql = "UPDATE system_users SET deleted = true WHERE id=?")
@SQLRestriction( "NOT deleted" )
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class User implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = -4463545345342344343L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @Column(name = "username")
    @NotNull
    private String name;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonEmailOwnerSessionFilter.class)
    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @JsonIgnore
    @Column(name = "recovery_token")
    private String recoveryPasswordToken;

    @JsonIgnore
    @Column(name = "recovery_token_date")
    private LocalDateTime recoveryPasswordTokenDate;

    @JsonIgnore
    @Column(name = "version_date")
    private LocalDateTime versionDate;


    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Profile profile;

    @JsonIgnore
    @Column(name = "email_verified")
    @NotNull
    private boolean email_verified;

    @JsonIgnore
    @Column(name = "expired_user")
    @NotNull
    private boolean expired_user;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserAdminFilter.class)
    @Column(name = "blocked_account")
    @NotNull
    private boolean blocked_account;

    @JsonIgnore
    @Column(name = "expired_credentials")
    @NotNull
    private boolean expired_credentials;

    @JsonIgnore
    @Column(name = "inactive")
    @NotNull
    private boolean inactive;

    @JsonIgnore
    @Column(name = "confirmed")
    @NotNull
    private boolean confirmed;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Authority authority;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization")
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    private Group organization;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public String getRecoveryPasswordToken() {
        return recoveryPasswordToken;
    }

    public void setRecoveryPasswordToken(String recoveryPasswordToken) {
        this.recoveryPasswordToken = recoveryPasswordToken;
    }

    public LocalDateTime getRecoveryPasswordTokenDate() {
        return recoveryPasswordTokenDate;
    }

    public void setRecoveryPasswordTokenDate(LocalDateTime recoveryPasswordTokenDate) {
        this.recoveryPasswordTokenDate = recoveryPasswordTokenDate;
    }

    public LocalDateTime getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(LocalDateTime versionDate) {
        this.versionDate = versionDate;
    }

    public boolean isExpired_user() {
        return expired_user;
    }

    public void setExpired_user(boolean expired_user) {
        this.expired_user = expired_user;
    }

    public boolean isBlocked_account() {
        return blocked_account;
    }

    public void setBlocked_account(boolean blocked_account) {
        this.blocked_account = blocked_account;
    }

    public boolean isExpired_credentials() {
        return expired_credentials;
    }

    public void setExpired_credentials(boolean expired_credentials) {
        this.expired_credentials = expired_credentials;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.authority != null) {
            return Arrays.asList(new SimpleGrantedAuthority(this.authority.toString()));
        }
        return null;
    }

    public Authority getAuthority() {
        return authority;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    public String getAccessLevel() {
        if(authority == null || !(getOwnerOfSession() || UserService.getInstance().isUserAdminSession())) {
            return null;
        }
        return authority.toString();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.name;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return !this.expired_user;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return !this.blocked_account;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return !this.expired_credentials;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return !this.inactive;
    }

    @JsonIgnore
    public boolean isConfirmed() {
        return this.confirmed;
    }

    @JsonIgnore
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Transient
    public boolean getOwnerOfSession() {
        return LoginService.getInstance().isSessionOfUser(this);
    }

    @Transient
    public boolean isNeedProfile() {
        return UserService.getInstance().userNeedAnProfile(this, false);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    public Boolean isHasPassword() {
        if(!(getOwnerOfSession() || UserService.getInstance().isUserAdminSession())) {
            return null;
        }
        return getPassword() != null && !getPassword().isEmpty();
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    @Override
    public boolean equals(Object otherUser) {
        if(otherUser == null) return false;
        else if (!(otherUser instanceof UserDetails)) return false;
        else return (otherUser.hashCode() == hashCode());
    }

    @Override
    public int hashCode() {
        return (getUsername() + getOrganization().getNickname()).hashCode();
    }

    public Group getOrganization() {
        return HibernateUtil.resolveLazyHibernateObject(this.organization);
    }

    public void setOrganization(Group organization) {
        this.organization = organization;
    }
}
