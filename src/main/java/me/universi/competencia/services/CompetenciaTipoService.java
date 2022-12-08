package me.universi.competencia.services;

import me.universi.competencia.entities.CompetenciaTipo;
import me.universi.competencia.repositories.CompetenciaTipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetenciaTipoService {
    @Autowired
    private CompetenciaTipoRepository competenciaTipoRepository;

    public CompetenciaTipo findFirstById(Long id) {
        Optional<CompetenciaTipo> competenciaTipoOptional = competenciaTipoRepository.findFirstById(id);
        if(competenciaTipoOptional.isPresent()){
            return competenciaTipoOptional.get();
        }else{
            return null;
        }
    }

    public CompetenciaTipo findFirstByNome(String nome) {
        Optional<CompetenciaTipo> competenciaTipoOptional = competenciaTipoRepository.findFirstByNome(nome);
        if(competenciaTipoOptional.isPresent()){
            return competenciaTipoOptional.get();
        }else{
            return null;
        }
    }

    public void save(CompetenciaTipo competenciaTipo) {
        competenciaTipoRepository.saveAndFlush(competenciaTipo);
    }

    public void delete(CompetenciaTipo competenciaTipo) {
        competenciaTipoRepository.delete(competenciaTipo);
    }

    public List<CompetenciaTipo> findAll() {
        return competenciaTipoRepository.findAll();
    }
}
