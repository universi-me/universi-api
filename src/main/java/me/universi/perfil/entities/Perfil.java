package me.universi.perfil.entities;

import me.universi.usuario.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public Perfil(){
    }

    public Perfil(Usuario usuario, String bio) {
        this.usuario = usuario;
        this.bio = bio;
    }

}
