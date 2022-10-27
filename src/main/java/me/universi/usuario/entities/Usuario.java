package me.universi.usuario.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.universi.usuario.enums.Autoridade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "senha")
    private String senha;

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

    public Usuario(String name, String email, String password){
        this.nome = name;
        this.email = email;
        this.senha = password;
    }

    public Usuario() {

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(this.autoridade.toString()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.usuario_expirado;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.conta_bloqueada;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credenciais_expiradas;
    }

    @Override
    public boolean isEnabled() {
        return !this.inativo;
    }
}
