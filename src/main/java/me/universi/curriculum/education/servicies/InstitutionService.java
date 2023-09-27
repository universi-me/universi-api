package me.universi.curriculum.education.servicies;

import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.repositories.InstitutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitutionService {

    private InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository){
        this.institutionRepository = institutionRepository;
    }

    public Institution save(Institution institution){
        return institutionRepository.save(institution);
    }

    public List<Institution> findAll(){
        return institutionRepository.findAll();
    }


}
