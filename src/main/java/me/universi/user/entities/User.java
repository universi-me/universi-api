package me.universi.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.universi.indicators.entities.Indicators;
import me.universi.perfil.entities.Perfil;
import me.universi.user.enums.Autoridade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity(name = "usuario")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @JsonIgnore
    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "senha")
    private String senha;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private Perfil perfil;

    @JsonIgnore
    @Column(name = "email_verificado")
    private boolean email_verificado;

    @JsonIgnore
    @Column(name = "usuario_expirado")
    private boolean usuario_expirado;

    @JsonIgnore
    @Column(name = "conta_bloqueada")
    private boolean conta_bloqueada;

    @JsonIgnore
    @Column(name = "credenciais_expiradas")
    private boolean credenciais_expiradas;

    @JsonIgnore
    @Column(name = "inativo")
    private boolean inativo;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "autoridade")
    private Autoridade autoridade;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "indicators_id", referencedColumnName = "id")
    private Indicators indicators;

    public User(String name, String email, String password){
        this.nome = name;
        this.email = email;
        this.senha = password;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public boolean isEmail_verificado() {
        return email_verificado;
    }

    public void setEmail_verificado(boolean email_verificado) {
        this.email_verificado = email_verificado;
    }

    public boolean isUsuario_expirado() {
        return usuario_expirado;
    }

    public void setUsuario_expirado(boolean usuario_expirado) {
        this.usuario_expirado = usuario_expirado;
    }

    public boolean isConta_bloqueada() {
        return conta_bloqueada;
    }

    public void setConta_bloqueada(boolean conta_bloqueada) {
        this.conta_bloqueada = conta_bloqueada;
    }

    public boolean isCredenciais_expiradas() {
        return credenciais_expiradas;
    }

    public void setCredenciais_expiradas(boolean credenciais_expiradas) {
        this.credenciais_expiradas = credenciais_expiradas;
    }

    public boolean isInativo() {
        return inativo;
    }

    public void setInativo(boolean inativo) {
        this.inativo = inativo;
    }

    public Autoridade getAutoridade() {
        return autoridade;
    }

    public void setAutoridade(Autoridade autoridade) {
        this.autoridade = autoridade;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.autoridade != null) {
            return Arrays.asList(new SimpleGrantedAuthority(this.autoridade.toString()));
        }
        return null;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.senha;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.nome;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return !this.usuario_expirado;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return !this.conta_bloqueada;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credenciais_expiradas;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return !this.inativo;
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
