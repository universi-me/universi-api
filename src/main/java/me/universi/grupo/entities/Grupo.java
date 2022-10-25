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

    @Column(name = "nome")
    public String nome;

    @Column(name = "descricao")
    public String descricao;

    // TODO: relacionamento
    //@ManyToOne
    //@JoinColumn(name="id_profile")
    public Perfil admin;


    // TODO: relacionamento
    public Collection<Perfil> participantes;

    //public Imagem imagem;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    public GrupoTipo tipo;

    public Grupo() {
    }

    public Grupo(String name, String descricao, Perfil admin, Collection<Perfil> participantes, GrupoTipo tipo)
    {
        this.nome = name;
        this.descricao = descricao;
        this.admin = admin;
        this.participantes = participantes;
        this.tipo = tipo;
    }

    @Override
    public String toString()
    {
        return "Grupo [nome=\""+this.nome+"\", descricao=\""+this.descricao+"\"]";
    }
}