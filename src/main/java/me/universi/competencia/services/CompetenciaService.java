package me.universi.competencia.services;

import me.universi.competencia.entities.Competencia;
import me.universi.competencia.repositories.CompetenciaRepository;
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
        competenciaRepository.save(competencia);
    }

    public void delete(Competencia competencia) {
        competenciaRepository.delete(competencia);
    }

    public List<Competencia> findAll() {
        return competenciaRepository.findAll();
    }
}
