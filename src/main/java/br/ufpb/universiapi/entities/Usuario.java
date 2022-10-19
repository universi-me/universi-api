package br.ufpb.universiapi.entities;

import javax.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;
    private String nome;
    private String email;
    private String senha;

    public Usuario(String name, String email, String password){
        this.nome = name;
        this.email = email;
        this.senha = password;
    }

}
