package me.universi.curriculum.education.servicies;

import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.repositories.InstitutionRepository;
import me.universi.user.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<Institution> findById(UUID id){
        return institutionRepository.findById(id);
    }

    public Institution update(Institution newInstitution, UUID id) throws Exception{
        return institutionRepository.findById(id).map(institution -> {
            institution.setName(newInstitution.getName());
            institution.setDescription(newInstitution.getDescription());
            return institutionRepository.saveAndFlush(institution);
        }).orElseGet(()->{
            try {
                return institutionRepository.saveAndFlush(newInstitution);
            }catch (Exception e){
                /*Implementar tratamento de exeptions*/
                return null;
            }
        });
    }

    public void deleteLogic(UUID id){
        Institution institution = findById(id).get();
        institution.setIsDeleted(true);
    }

}
