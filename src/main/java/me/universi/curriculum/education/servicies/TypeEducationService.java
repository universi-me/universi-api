package me.universi.curriculum.education.servicies;

import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.repositories.TypeEducationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TypeEducationService {

    private TypeEducationRepository typeEducationRepository;

    public TypeEducationService(TypeEducationRepository typeEducationRepository){
        this.typeEducationRepository = typeEducationRepository;
    }

    public TypeEducation save(TypeEducation typeEducation){
        return typeEducationRepository.save(typeEducation);
    }

    public List<TypeEducation> findAll(){
        return typeEducationRepository.findAll();
    }

    public Optional<TypeEducation> findById(UUID id){
        return typeEducationRepository.findById(id);
    }

    public TypeEducation update(TypeEducation newTypeEducation, UUID id) throws Exception{
        return typeEducationRepository.findById(id).map(typeEducation -> {
            typeEducation.setName(newTypeEducation.getName());
            return typeEducationRepository.saveAndFlush(typeEducation);
        }).orElseGet(()->{
            try {
                return typeEducationRepository.saveAndFlush(newTypeEducation);
            }catch (Exception e){
                /*Implementar tratamento de exeptions*/
                return null;
            }
        });
    }

    public void deleteLogic(UUID id){
        TypeEducation typeEducation = findById(id).get();
        typeEducation.setDeleted(true);
        save(typeEducation);
    }
}
