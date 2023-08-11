package me.universi.user.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import me.universi.indicators.entities.Indicators;
import me.universi.profile.entities.Profile;
import me.universi.user.enums.Authority;
import me.universi.user.services.JsonEmailOwnerSessionFilter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Entity(name = "system_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_sequence", allocationSize = 1)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonEmailOwnerSessionFilter.class)
    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private Profile profile;

    @JsonIgnore
    @Column(name = "email_verified")
    private boolean email_verified;

    @JsonIgnore
    @Column(name = "expired_user")
    private boolean expired_user;

    @JsonIgnore
    @Column(name = "blocked_account")
    private boolean blocked_account;

    @JsonIgnore
    @Column(name = "expired_credentials")
    private boolean expired_credentials;

    @JsonIgnore
    @Column(name = "inactive")
    private boolean inactive;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Authority authority;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "indicators_id", referencedColumnName = "id")
    @JsonBackReference
    private Indicators indicators;

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Indicators getIndicators() {
        return indicators;
    }

    public void setIndicators(Indicators indicators) {
        this.indicators = indicators;
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
