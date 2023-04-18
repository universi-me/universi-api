package me.universi.perfil.services;

import me.universi.competencia.services.CompetenceService;
import me.universi.perfil.entities.Profile;
import me.universi.perfil.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;
    @Autowired
    public CompetenceService competenciaService;

    // Retorna um Perfil passando o id
    public Profile findFirstById(Long id) {
        return perfilRepository.findFirstById(id).orElse(null);
    }

    // Retorna um Perfil passando o id como string
    public Profile findFirstById(String id) {
        return perfilRepository.findFirstById(Long.parseLong(id)).orElse(null);
    }

    public void save(Profile profile) {
        perfilRepository.saveAndFlush(profile);
    }

    public void update(Profile profile) {
        perfilRepository.saveAndFlush(profile);
    }

    public Collection<Profile> findAll(){ return perfilRepository.findAll(); }

    public void delete(Profile profile) {
        perfilRepository.delete(profile);
    }

    public void deleteAll() { perfilRepository.deleteAll();}

    // pesquisar os 5 primeiros contendo a string maiusculo ou minusculo
    public Collection<Profile> findTop5ByNomeContainingIgnoreCase(String nome){ return perfilRepository.findTop5ByNomeContainingIgnoreCase(nome); }
}
