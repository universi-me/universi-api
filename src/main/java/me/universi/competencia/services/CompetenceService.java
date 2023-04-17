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
public class CompetenceService {
    @Autowired
    private CompetenceRepository competenceRepository;

    public Competence findFirstById(Long id) {
        Optional<Competence> optionalCompetence = competenceRepository.findFirstById(id);
        if(optionalCompetence.isPresent()){
            return optionalCompetence.get();
        }else{
            return null;
        }
    }

    public void save(Competence competence) {
        competenceRepository.saveAndFlush(competence);
    }

    public void delete(Competence competence) {
        competenceRepository.delete(competence);
    }

    public List<Competence> findAll() {
        return competenceRepository.findAll();
    }

    public void update(Competence competence){ competenceRepository.saveAndFlush(competence); }

    public boolean profileHasCompetence(Perfil profile, Competence competence) {
        try {
            if(profile.getCompetencias() != null) {
                for(Competence compNow : profile.getCompetencias()) {
                    if(competence.getId() == compNow.getId()) {
                        return true;
                    }
                }
            }
        }catch (Exception e) {
            return false;
        }
        return false;
    }
    public void deleteAll(Collection<Competence> competences){
        competenceRepository.deleteAll(competences);
    }
}
