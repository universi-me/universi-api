package me.universi.perfil.entities;

import me.universi.competencia.entities.Competencia;
import me.universi.link.entities.Link;
import me.universi.perfil.enums.Sexo;
import me.universi.recomendacao.entities.Recomendacao;
import me.universi.usuario.entities.Usuario;
import me.universi.grupo.entities.Grupo;

import javax.persistence.*;
import java.util.Collection;

@Entity(name = "perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profile")
    private Long id;

    @OneToOne
    private Usuario usuario;
    @Column(name = "bio")
    private String bio;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_link")
    private Link link;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "oerfil_competencia",
            joinColumns = { @JoinColumn(name = "id_perfil") },
            inverseJoinColumns = { @JoinColumn(name = "id_competencia") }
    )
    private Collection<Competencia> competencias;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "perfil_grupo",
            joinColumns = { @JoinColumn(name = "id_perfil") },
            inverseJoinColumns = { @JoinColumn(name = "id_grupo") }
    )
    private Collection<Grupo> grupos;
    @OneToMany(mappedBy = "link")
    private Collection<Link> links;
    @Column(name = "sexo")
    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    @OneToMany(mappedBy = "origem")
    private Collection<Recomendacao> recomendacoesFeitas;
    @OneToMany(mappedBy = "destino")
    private Collection<Recomendacao> recomendacoesRecebidas;

    public Perfil(Long id, Usuario usuario, String bio, Link link, Collection<Competencia> competencias, Collection<Grupo> grupos, Collection<Link> links) {
        this.id = id;
        this.usuario = usuario;
        this.bio = bio;
        this.link = link;
        this.competencias = competencias;
        this.grupos = grupos;
        this.links = links;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Collection<Competencia> getCompetencias() {
        return competencias;
    }

    public void setCompetencias(Collection<Competencia> competencias) {
        this.competencias = competencias;
    }

    public Collection<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(Collection<Grupo> grupos) {
        this.grupos = grupos;
    }

    public Collection<Link> getLinks() {
        return links;
    }

    public void setLinks(Collection<Link> links) {
        this.links = links;
    }
}
