package me.universi.perfil.services;

import me.universi.competencia.entities.Competencia;
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

    // Retorna um Perfil passando o id
    public Perfil findFirstById(Long id) {
        return perfilRepository.findFirstById(id).orElse(null);
    }

    // Retorna um Perfil passando o id como string
    public Perfil findFirstById(String id) {
        return perfilRepository.findFirstById(Long.parseLong(id)).orElse(null);
    }

    public void save(Perfil perfil) {
        perfilRepository.save(perfil);
    }

    public void update(Perfil perfil) {
        perfilRepository.save(perfil);
    }

    public Collection<Perfil> findAll(){ return perfilRepository.findAll(); }

    public void delete(Perfil perfil) {
        perfilRepository.delete(perfil);
    }

    public void deleteAll() { perfilRepository.deleteAll();}

    public void adicionarCompetencia(Perfil perfil, Competencia comp) {
        Collection<Competencia> compArr = perfil.getCompetencias();
        if(compArr == null) {
            compArr = new ArrayList<Competencia>();
        }
        if(!compArr.contains(comp)) {
            compArr.add(comp);
            perfil.setCompetencias(compArr);
            this.save(perfil);
        }
    }

    public void removerCompetencia(Perfil perfil, Competencia comp) {
        Collection<Competencia> compArr = perfil.getCompetencias();
        if(compArr == null) {
            compArr = new ArrayList<Competencia>();
        }
        if(compArr.contains(comp)) {
            compArr.remove(comp);
            perfil.setCompetencias(compArr);
            this.save(perfil);
        }
    }

}
