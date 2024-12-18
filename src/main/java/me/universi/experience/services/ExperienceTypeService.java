package me.universi.experience.services;

import org.springframework.stereotype.Service;

import me.universi.experience.entities.ExperienceType;
import me.universi.experience.repositories.ExperienceTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExperienceTypeService {

    private final ExperienceTypeRepository typeExperienceRepository;

    public ExperienceTypeService(ExperienceTypeRepository typeExperienceRepository){
        this.typeExperienceRepository = typeExperienceRepository;
    }

    public ExperienceType save(ExperienceType typeExperience){
        return typeExperienceRepository.saveAndFlush(typeExperience);
    }

    public List<ExperienceType> findAll(){
        return typeExperienceRepository.findAll();
    }

    public Optional<ExperienceType> findById(UUID id){
        return typeExperienceRepository.findFirstById(id);
    }

    public ExperienceType update(ExperienceType newTypeExperience, UUID id) throws Exception{
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
        ExperienceType typeExperience = findById(id).get();
        typeExperience.setDeleted(true);
        update(typeExperience, id);
    }

    public List<ExperienceType> findAllNotDeleted(){
        List<ExperienceType> typeExperiences = new ArrayList<>();
        for (ExperienceType type: typeExperienceRepository.findAll()) {
            if (!type.isDeleted()){
                typeExperiences.add(type);
            }
        }
        return typeExperiences;
    }

}
