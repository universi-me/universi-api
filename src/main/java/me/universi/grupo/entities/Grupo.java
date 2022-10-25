package me.universi.grupo.entities;

import javax.persistence.*;

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

    public Grupo() {
    }

    public Grupo(String name, String descricao){
        this.nome = name;
        this.descricao = descricao;
    }

    @Override
    public String toString()
    {
        return "Grupo [nome=\""+this.nome+"\", descricao=\""+this.descricao+"\"]";
    }
}