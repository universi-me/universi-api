package me.universi.perfil.entities;

import me.universi.link.entities.Link;
import me.universi.usuario.entities.Usuario;

import javax.persistence.*;

@Entity
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

    public Perfil(){
    }

    public Perfil(Usuario usuario, String bio) {
        this.usuario = usuario;
        this.bio = bio;
    }

}
