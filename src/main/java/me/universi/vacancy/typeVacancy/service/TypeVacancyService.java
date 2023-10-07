package me.universi.vacancy.typeVacancy.service;

import me.universi.vacancy.typeVacancy.entities.TypeVacancy;
import me.universi.vacancy.typeVacancy.repository.TypeVacancyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TypeVacancyService {

    private TypeVacancyRepository typeVacancyRepository;

    public TypeVacancyService(TypeVacancyRepository typeVacancyRepository){
        this.typeVacancyRepository = typeVacancyRepository;
    }

    public TypeVacancy save(TypeVacancy typeVacancy){
        typeVacancy.setDeleted(false);
        return typeVacancyRepository.save(typeVacancy);
    }

    public List<TypeVacancy> findAll(){
        return typeVacancyRepository.findAll();
    }

    public Optional<TypeVacancy> findById(UUID id){
        return typeVacancyRepository.findById(id);
    }

    public TypeVacancy update(TypeVacancy newTypeVacancy, UUID id) throws Exception{
        return typeVacancyRepository.findById(id).map(typeVacancy -> {
            typeVacancy.setName(newTypeVacancy.getName());
            return typeVacancyRepository.saveAndFlush(typeVacancy);
        }).orElseGet(()->{
            try {
                return typeVacancyRepository.saveAndFlush(newTypeVacancy);
            }catch (Exception e){
                /*Implementar tratamento de exeptions*/
                return null;
            }
        });
    }
}
