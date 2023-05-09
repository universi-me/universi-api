package me.universi.competence.services;

import me.universi.competence.entities.CompetenceType;
import me.universi.competence.repositories.CompetenceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetenceTypeService {
    @Autowired
    private CompetenceTypeRepository competenceTypeRepository;

    public CompetenceType findFirstById(Long id) {
        Optional<CompetenceType> optionalCompetenceType = competenceTypeRepository.findFirstById(id);
        if(optionalCompetenceType.isPresent()){
            return optionalCompetenceType.get();
        }else{
            return null;
        }
    }

    public CompetenceType findFirstByName(String name) {
        Optional<CompetenceType> optionalCompetenceType = competenceTypeRepository.findFirstByName(name);
        if(optionalCompetenceType.isPresent()){
            return optionalCompetenceType.get();
        }else{
            return null;
        }
    }

    public void save(CompetenceType competenceType) {
        competenceTypeRepository.saveAndFlush(competenceType);
    }

    public void delete(CompetenceType competenceType) {
        competenceTypeRepository.delete(competenceType);
    }

    public List<CompetenceType> findAll() {
        return competenceTypeRepository.findAll();
    }
}
