package me.universi.link.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.universi.link.enums.TipoLink;
import me.universi.perfil.entities.Perfil;

import jakarta.persistence.*;

@Entity(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_link")
    private long id;
    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoLink tipo;
    @Column(name = "url")
    private String url;

    @Column(name = "nome")
    private String nome;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil")
    private Perfil perfil;

    public Link(TipoLink tipo, String url){
        this.tipo = tipo;
        this.url = url;
    }

    public Link() {}

    public TipoLink getTipo() {
        return tipo;
    }

    public void setTipo(TipoLink tipo) {
        this.tipo = tipo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
