package me.universi.perfil.entities;

import me.universi.competencia.entities.Competencia;
import me.universi.link.entities.Link;
import me.universi.perfil.enums.Sexo;
import me.universi.recomendacao.entities.Recomendacao;
import me.universi.usuario.entities.Usuario;
import me.universi.grupo.entities.Grupo;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity(name = "perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profile")
    private Long id;

    @OneToOne
    private Usuario usuario;
    @Column(name = "nome")
    private String nome;
    @Column(name = "sobrenome")
    private String sobrenome;
    @Column(name = "imagem")
    private String imagem;
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_link")
    private Link link;
    @ManyToMany(mappedBy = "perfil", fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    private Collection<Competencia> competencias;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "perfil_grupo",
            joinColumns = { @JoinColumn(name = "id_perfil") },
            inverseJoinColumns = { @JoinColumn(name = "id_grupo") }
    )
    private Collection<Grupo> grupos;
    @OneToMany(mappedBy = "perfil")
    private Collection<Link> links;
    @Column(name = "sexo")
    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    @OneToMany(mappedBy = "origem")
    private Collection<Recomendacao> recomendacoesFeitas;
    @OneToMany(mappedBy = "destino")
    private Collection<Recomendacao> recomendacoesRecebidas;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_de_criacao")
    private Date dataDeCriacao;

    public Perfil(Long id, Usuario usuario, String bio, Link link, Collection<Competencia> competencias, Collection<Grupo> grupos, Collection<Link> links) {
        this.id = id;
        this.usuario = usuario;
        this.bio = bio;
        this.link = link;
        this.competencias = competencias;
        this.grupos = grupos;
        this.links = links;
    }

    public Perfil(){

    }

    public Long getId() {
        return id;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Collection<Recomendacao> getRecomendacoesFeitas() {
        return recomendacoesFeitas;
    }

    public Collection<Recomendacao> getRecomendacoesRecebidas() {
        return recomendacoesRecebidas;
    }

    public Date getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(Date dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }
}
