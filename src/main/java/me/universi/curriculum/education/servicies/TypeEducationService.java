package me.universi.curriculum.education.servicies;

import me.universi.curriculum.education.controller.TypeEducationController;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.repositories.TypeEducationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
