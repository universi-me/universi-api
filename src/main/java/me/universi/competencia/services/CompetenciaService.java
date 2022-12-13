package me.universi.competencia.services;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.entities.CompetenciaTipo;
import me.universi.competencia.repositories.CompetenciaRepository;
import me.universi.perfil.entities.Perfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetenciaService {
    @Autowired
    private CompetenciaRepository competenciaRepository;

    public Competencia findFirstById(Long id) {
        Optional<Competencia> competenciaOptional = competenciaRepository.findFirstById(id);
        if(competenciaOptional.isPresent()){
            return competenciaOptional.get();
        }else{
            return null;
        }
    }

    public void save(Competencia competencia) {
        competenciaRepository.saveAndFlush(competencia);
    }

    public void delete(Competencia competencia) {
        competenciaRepository.delete(competencia);
    }

    public List<Competencia> findAll() {
        return competenciaRepository.findAll();
    }

    public void update(Competencia competencia){ competenciaRepository.saveAndFlush(competencia); }

    public boolean perfilTemCompetencia(Perfil perfil, Competencia competencia) {
        try {
            if(perfil.getCompetencias() != null) {
                for(Competencia compNow : perfil.getCompetencias()) {
                    if(competencia.getId() == compNow.getId()) {
                        return true;
                    }
                }
            }
        }catch (Exception e) {
            return false;
        }
        return false;
    }
}
