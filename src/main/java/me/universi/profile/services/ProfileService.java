package me.universi.profile.services;

import me.universi.competence.services.CompetenceService;
import me.universi.profile.entities.Profile;
import me.universi.profile.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ProfileService {

    @Autowired
    private PerfilRepository perfilRepository;

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

    // search the first 5 containing the string uppercase or lowercase
    public Collection<Profile> findTop5ByNameContainingIgnoreCase(String nome){ return perfilRepository.findTop5ByFirstnameContainingIgnoreCase(nome); }
}
