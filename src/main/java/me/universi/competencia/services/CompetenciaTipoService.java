package me.universi.competencia.services;

import me.universi.competencia.entities.CompetenceType;
import me.universi.competencia.repositories.CompetenceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetenciaTipoService {
    @Autowired
    private CompetenceTypeRepository competenciaTipoRepository;

    public CompetenceType findFirstById(Long id) {
        Optional<CompetenceType> competenciaTipoOptional = competenciaTipoRepository.findFirstById(id);
        if(competenciaTipoOptional.isPresent()){
            return competenciaTipoOptional.get();
        }else{
            return null;
        }
    }

    public CompetenceType findFirstByNome(String nome) {
        Optional<CompetenceType> competenciaTipoOptional = competenciaTipoRepository.findFirstByNome(nome);
        if(competenciaTipoOptional.isPresent()){
            return competenciaTipoOptional.get();
        }else{
            return null;
        }
    }

    public void save(CompetenceType competenciaTipo) {
        competenciaTipoRepository.saveAndFlush(competenciaTipo);
    }

    public void delete(CompetenceType competenciaTipo) {
        competenciaTipoRepository.delete(competenciaTipo);
    }

    public List<CompetenceType> findAll() {
        return competenciaTipoRepository.findAll();
    }
}
