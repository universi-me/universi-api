package me.universi.perfil.services;

import me.universi.competencia.entities.Competence;
import me.universi.competencia.services.CompetenceService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;
    @Autowired
    public CompetenceService competenciaService;

    // Retorna um Perfil passando o id
    public Perfil findFirstById(Long id) {
        return perfilRepository.findFirstById(id).orElse(null);
    }

    // Retorna um Perfil passando o id como string
    public Perfil findFirstById(String id) {
        return perfilRepository.findFirstById(Long.parseLong(id)).orElse(null);
    }

    public void save(Perfil perfil) {
        perfilRepository.saveAndFlush(perfil);
    }

    public void update(Perfil perfil) {
        perfilRepository.saveAndFlush(perfil);
    }

    public Collection<Perfil> findAll(){ return perfilRepository.findAll(); }

    public void delete(Perfil perfil) {
        perfilRepository.delete(perfil);
    }

    public void deleteAll() { perfilRepository.deleteAll();}

    // pesquisar os 5 primeiros contendo a string maiusculo ou minusculo
    public Collection<Perfil> findTop5ByNomeContainingIgnoreCase(String nome){ return perfilRepository.findTop5ByNomeContainingIgnoreCase(nome); }
}
