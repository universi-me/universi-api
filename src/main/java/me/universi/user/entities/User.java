package me.universi.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import me.universi.profile.entities.Profile;
import me.universi.user.enums.Authority;
import me.universi.user.services.JsonEmailOwnerSessionFilter;
import me.universi.user.services.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Entity(name = "system_users")
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
    @OneToOne(mappedBy = "user", cascade = CascadeType.DETACH)
    private Profile profile;

    @JsonIgnore
    @Column(name = "email_verified")
    @NotNull
    private boolean email_verified;

    @JsonIgnore
    @Column(name = "expired_user")
    @NotNull
    private boolean expired_user;

    @JsonIgnore
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
    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Authority authority;

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

    @Transient
    public boolean getOwnerOfSession() {
        return UserService.getInstance().isSessionOfUser(this);
    }

    @Transient
    public boolean isNeedProfile() {
        return UserService.getInstance().userNeedAnProfile(this);
    }

    @Override
    public boolean equals(Object otherUser) {
        if(otherUser == null) return false;
        else if (!(otherUser instanceof UserDetails)) return false;
        else return (otherUser.hashCode() == hashCode());
    }
    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
}
