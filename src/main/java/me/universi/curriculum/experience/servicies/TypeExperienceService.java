package me.universi.curriculum.experience.servicies;

import me.universi.curriculum.experience.entities.TypeExperience;
import me.universi.curriculum.experience.repositories.TypeExperienceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TypeExperienceService {

    private final TypeExperienceRepository typeExperienceRepository;

    public TypeExperienceService(TypeExperienceRepository typeExperienceRepository){
        this.typeExperienceRepository = typeExperienceRepository;
    }

    public TypeExperience save(TypeExperience typeExperience){
        return typeExperienceRepository.saveAndFlush(typeExperience);
    }

    public List<TypeExperience> findAll(){
        return typeExperienceRepository.findAll();
    }

    public Optional<TypeExperience> findById(UUID id){
        return typeExperienceRepository.findFirstById(id);
    }

    public TypeExperience update(TypeExperience newTypeExperience, UUID id) throws Exception{
        return typeExperienceRepository.findById(id).map(typeExperience -> {
            typeExperience.setName(newTypeExperience.getName());
            return save(typeExperience);
        }).orElseGet(()->{
            try {
                return save(newTypeExperience);
            }catch (Exception e){
                return null;
            }
        });
    }
    public void deleteLogic(UUID id) throws Exception {
        TypeExperience typeExperience = findById(id).get();
        typeExperience.setDeleted(true);
        update(typeExperience, id);
    }

    public List<TypeExperience> findAllNotDeleted(){
        List<TypeExperience> typeExperiences = new ArrayList<>();
        for (TypeExperience type: typeExperienceRepository.findAll()) {
            if (!type.isDeleted()){
                typeExperiences.add(type);
            }
        }
        return typeExperiences;
    }

}
