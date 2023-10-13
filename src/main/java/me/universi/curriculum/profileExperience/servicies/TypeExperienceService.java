package me.universi.curriculum.profileExperience.servicies;

import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.profileExperience.entities.TypeExperience;
import me.universi.curriculum.profileExperience.repositories.TypeExperienceRepository;
import me.universi.user.entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<TypeExperience> findById(UUID id){
        return typeExperienceRepository.findById(id);
    }

    public TypeExperience update(TypeExperience newTypeExperience, UUID id) throws Exception{
        return typeExperienceRepository.findById(id).map(typeExperience -> {
            typeExperience.setName(newTypeExperience.getName());
            return typeExperienceRepository.saveAndFlush(typeExperience);
        }).orElseGet(()->{
            try {
                return typeExperienceRepository.saveAndFlush(newTypeExperience);
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
            if (!type.getDeleted()){
                typeExperiences.add(type);
            }
        }
        return typeExperiences;
    }

}
