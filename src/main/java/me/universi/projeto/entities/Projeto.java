package me.universi.projeto.entities;

import javax.persistence.*;

@Entity(name = "Projeto")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projeto")
    public Long id;

    @Column(name = "nome")
    public String nome;

    @Column(name = "descricao")
    public String descricao;

    public Projeto() {
    }

    public Projeto(String name, String descricao){
        this.nome = name;
        this.descricao = descricao;
    }

    @Override
    public String toString()
    {
        return "Projeto [nome=\""+this.nome+"\", descricao=\""+this.descricao+"\"]";
    }
}