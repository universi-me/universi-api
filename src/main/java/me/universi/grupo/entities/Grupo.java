package me.universi.grupo.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import me.universi.exercise.entities.Exercise;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.perfil.entities.Perfil;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity(name = "Grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    public Long id;
    
    @Column(name = "nickname")
    public String nickname;

    @Column(name = "nome")
    public String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    public String descricao;

    @Column(name = "imagem")
    public String imagem;

    @ManyToOne
    @JoinColumn(name="id_profile")
    public Perfil admin;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "perfil_grupo",
            joinColumns = { @JoinColumn(name = "id_grupo") },
            inverseJoinColumns = { @JoinColumn(name =  "id_perfil") }
    )
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Perfil.class)
    @JsonIdentityReference(alwaysAsId = true)
    public Collection<Perfil> participantes;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(
            name = "grupo_grupo",
            joinColumns = { @JoinColumn(name = "id_grupo", referencedColumnName = "id_grupo") },
            inverseJoinColumns = { @JoinColumn(name = "id_subgrupo", referencedColumnName = "id_grupo") }
    )
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Grupo.class)
    @JsonIdentityReference(alwaysAsId = true)
    public Collection<Grupo> subGrupos;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    public GrupoTipo tipo;

    // halilidade do grupo ser acessado diretamente pelo url. (pai de todos os grupos)
    @Column(name = "gruporoot")
    public boolean grupoRoot;

    // grupo pode criar subGrupos
    @Column(name = "podeCriarGrupo")
    public boolean podeCriarGrupo;

    @Column(name = "podeEntrar")
    public boolean podeEntrar;

    @Column(name = "podeAddParticipante")
    public boolean podeAddParticipante;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_de_criacao")
    private Date dataDeCriacao;

    @Column(name = "publico")
    public boolean grupoPublico;

    @OneToOne
    private Exercise exercise;

    public Grupo() {
    }

    public Grupo(String nickname, String name, String descricao, Perfil admin, Collection<Perfil> participantes, GrupoTipo tipo, Collection<Grupo> subGrupos, boolean grupoRoot, boolean podeCriarGrupo) {
        this.nickname = nickname;
        this.nome = name;
        this.descricao = descricao;
        this.admin = admin;
        this.participantes = participantes;
        this.tipo = tipo;
        this.subGrupos = subGrupos;
        this.grupoRoot = grupoRoot;
        this.podeCriarGrupo = podeCriarGrupo;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Perfil getAdmin() {
        return admin;
    }

    public void setAdmin(Perfil admin) {
        this.admin = admin;
    }

    public Collection<Perfil> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Collection<Perfil> participantes) {
        this.participantes = participantes;
    }

    public GrupoTipo getTipo() {
        return tipo;
    }

    public void setTipo(GrupoTipo tipo) {
        this.tipo = tipo;
    }
    
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Collection<Grupo> getSubGrupos() {
        return subGrupos;
    }

    public void setSubGrupos(Collection<Grupo> subGrupos) {
        this.subGrupos = subGrupos;
    }

    public boolean isGrupoRoot() {
        return grupoRoot;
    }

    public void setGrupoRoot(boolean grupoRoot) {
        this.grupoRoot = grupoRoot;
    }

    public boolean isPodeCriarGrupo() {
        return podeCriarGrupo;
    }

    public void setPodeCriarGrupo(boolean podeCriarGrupo) {
        this.podeCriarGrupo = podeCriarGrupo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Date getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(Date dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }

    public boolean isGrupoPublico() {
        return grupoPublico;
    }

    public void setGrupoPublico(boolean grupoPublico) {
        this.grupoPublico = grupoPublico;
    }

    public boolean isPodeEntrar() {
        return podeEntrar;
    }

    public void setPodeEntrar(boolean podeEntrar) {
        this.podeEntrar = podeEntrar;
    }

    public boolean isPodeAddParticipante() {
        return podeAddParticipante;
    }

    public void setPodeAddParticipante(boolean podeAddParticipante) {
        this.podeAddParticipante = podeAddParticipante;
    }

    @Override
    public String toString() {
        return "Grupo [id=\""+this.id+"\", nome=\""+this.nome+"\", descricao=\""+this.descricao+"\"]";
    }
}
