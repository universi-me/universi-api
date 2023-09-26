package me.universi.curriculum.profileExperience.servicies;

import me.universi.curriculum.profileExperience.entities.TypeExperience;
import me.universi.curriculum.profileExperience.repositories.TypeExperienceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeExperienceService {

    private TypeExperienceRepository typeExperienceRepository;

    public TypeExperienceService(TypeExperienceRepository typeExperienceRepository){
        this.typeExperienceRepository = typeExperienceRepository;
    }

    public TypeExperience save(TypeExperience typeExperience){
        return typeExperienceRepository.save(typeExperience);
    }

    public List<TypeExperience> findAll(){
        return typeExperienceRepository.findAll();
    }
}
