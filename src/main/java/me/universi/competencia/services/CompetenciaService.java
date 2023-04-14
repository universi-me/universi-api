package me.universi.competencia.services;

import me.universi.competencia.entities.Competence;
import me.universi.competencia.entities.CompetenceType;
import me.universi.competencia.repositories.CompetenceRepository;
import me.universi.perfil.entities.Perfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CompetenciaService {
    @Autowired
    private CompetenceRepository competenciaRepository;

    public Competence findFirstById(Long id) {
        Optional<Competence> competenciaOptional = competenciaRepository.findFirstById(id);
        if(competenciaOptional.isPresent()){
            return competenciaOptional.get();
        }else{
            return null;
        }
    }

    public void save(Competence competencia) {
        competenciaRepository.saveAndFlush(competencia);
    }

    public void delete(Competence competencia) {
        competenciaRepository.delete(competencia);
    }

    public List<Competence> findAll() {
        return competenciaRepository.findAll();
    }

    public void update(Competence competencia){ competenciaRepository.saveAndFlush(competencia); }

    public boolean perfilTemCompetencia(Perfil perfil, Competence competencia) {
        try {
            if(perfil.getCompetencias() != null) {
                for(Competence compNow : perfil.getCompetencias()) {
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
    public void deleteAll(Collection<Competence> competencias){
        competenciaRepository.deleteAll(competencias);
    }
}
