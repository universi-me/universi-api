package me.universi.perfil.services;

import me.universi.perfil.entities.Perfil;
import me.universi.perfil.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;

    // Retorna um Perfil passando o id
    public Perfil findFirstById(Long id) {
        return perfilRepository.findFirstById(id).orElse(null);
    }

    // Retorna um Perfil passando o id como string
    public Perfil findFirstById(String id) {
        return perfilRepository.findFirstById(Long.parseLong(id)).orElse(null);
    }

}
