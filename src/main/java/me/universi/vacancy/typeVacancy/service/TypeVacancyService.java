package me.universi.vacancy.typeVacancy.service;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.vacancy.typeVacancy.entities.TypeVacancy;
import me.universi.vacancy.typeVacancy.repository.TypeVacancyRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
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

    public Response create(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String name = (String) body.get("name");
            if(name.isBlank() || name.isEmpty()){
                throw new InstantiationException("Parametro name passado é nulo ou vazio");
            }


            TypeVacancy typeVacancy = new TypeVacancy(name);
            save(typeVacancy);

            response.message = "Tipo de Vaga criada.";
            response.success = true;

        });
    }

    public Response get(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("TypeVacancyId");

            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro id é nulo.");
            }

            TypeVacancy typeVacancy = findById(UUID.fromString(id)).get();

            response.body.put("typeVacancy", typeVacancy);
            response.success = true;

        });
    }

    public Response findAll(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<TypeVacancy> typeVacancies = findAll();

            response.body.put("lista", typeVacancies);
            response.success = true;

        });
    }

    public Response remove(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("TypeVacancyId");
            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro TypeVacancyId é nulo.");
            }

            typeVacancyRepository.delete(findById(UUID.fromString(id)).get());

            response.message = "TypeVacancy removida logicamente";
            response.success = true;

        });
    }
}
