package me.universi.education.servicies;

import me.universi.api.entities.Response;
import me.universi.education.entities.TypeEducation;
import me.universi.education.exceptions.TypeEducationException;
import me.universi.education.repositories.TypeEducationRepository;

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
public class TypeEducationService {

    private TypeEducationRepository typeEducationRepository;

    public TypeEducationService(TypeEducationRepository typeEducationRepository){
        this.typeEducationRepository = typeEducationRepository;
    }

    public TypeEducation save(TypeEducation typeEducation){
        return typeEducationRepository.saveAndFlush(typeEducation);
    }

    public List<TypeEducation> findAll(){
        return typeEducationRepository.findAll();
    }

    public Optional<TypeEducation> findById(UUID id){
        return typeEducationRepository.findFirstById(id);
    }

    public TypeEducation update(TypeEducation newTypeEducation, UUID id) throws Exception{
        return typeEducationRepository.findById(id).map(typeEducation -> {
            typeEducation.setName(newTypeEducation.getName());
            return save(typeEducation);
        }).orElseGet(()->{
            try {
                return save(newTypeEducation);
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


    public Response create(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String name = (String) body.get("name");
            if(name.isBlank() || name.isEmpty()){
                throw new TypeEducationException("Paramentro name passado é nulo");
            }

            TypeEducation newTypeEducation = new TypeEducation(name);
            save(newTypeEducation);

            response.message = "Tipo de Educação criada com sucesso.";
            response.success = true;

        });
    }

    public Response get(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("typeEducationId");

            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro id é nulo.");
            }

            TypeEducation typeEducation = findById(UUID.fromString(id)).get();

            response.body.put("typeEducation", typeEducation);
            response.success = true;

        });
    }

    public Response findAll(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<TypeEducation> typeEducations = findAll();

            response.body.put("lista", typeEducations);
            response.success = true;

        });
    }


    public Response remove(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("tyEducationId");
            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro typeEducationId é nulo.");
            }

            deleteLogic(UUID.fromString(id));

            response.message = "Tipo de Educação removida com sucesso.";
            response.success = true;

        });
    }
}
