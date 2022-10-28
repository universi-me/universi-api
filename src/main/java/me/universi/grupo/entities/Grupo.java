package me.universi.grupo.entities;

import me.universi.grupo.enums.GrupoTipo;
import me.universi.perfil.entities.Perfil;

import javax.persistence.*;
import java.util.Collection;

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

    @Column(name = "descricao")
    public String descricao;

    @ManyToOne
    @JoinColumn(name="id_profile")
    public Perfil admin;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "perfil_grupo",
            joinColumns = { @JoinColumn(name = "id_grupo") },
            inverseJoinColumns = { @JoinColumn(name =  "id_perfil") }
    )
    public Collection<Perfil> participantes;

    //public Imagem imagem;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    public GrupoTipo tipo;

    public Grupo() {
    }

    public Grupo(String nickname, String name, String descricao, Perfil admin, Collection<Perfil> participantes, GrupoTipo tipo)
    {
        this.nickname = nickname;
        this.nome = name;
        this.descricao = descricao;
        this.admin = admin;
        this.participantes = participantes;
        this.tipo = tipo;
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

    @Override
    public String toString()
    {
        return "Grupo [id=\""+this.id+"\", nome=\""+this.nome+"\", descricao=\""+this.descricao+"\"]";
    }
}
