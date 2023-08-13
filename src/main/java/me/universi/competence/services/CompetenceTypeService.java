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

    public CompetenceType save(CompetenceType competenceType) {
        return competenceTypeRepository.saveAndFlush(competenceType);
    }

    public void delete(CompetenceType competenceType) {
        competenceTypeRepository.delete(competenceType);
    }

    public List<CompetenceType> findAll() {
        return competenceTypeRepository.findAll();
    }

    public CompetenceType update(CompetenceType newCompetenceType, Long id) throws Exception{
        return competenceTypeRepository.findById(id).map(competenceType -> {
            competenceType.setName(newCompetenceType.getName());
            return competenceTypeRepository.saveAndFlush(competenceType);
        }).orElseGet(()->{
            try {
                return competenceTypeRepository.saveAndFlush(newCompetenceType);
            }catch (Exception e){
                return null;
            }
        });
    }

    public void delete(Long id) {
        competenceTypeRepository.deleteById(id);
    }
}
