package me.universi.curriculum.education.servicies;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.education.repositories.TypeEducationRepository;
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


    public Response create(Map<String, Object> body) {
        Response response = new Response();

        try {
            String name = (String) body.get("name");
            if(name.isBlank() || name.isEmpty()){
                throw new TypeEducationException("Paramentro name passado é nulo");
            }

            TypeEducation newTypeEducation = new TypeEducation(name);
            typeEducationRepository.saveAndFlush(newTypeEducation);

            response.message = "TypeEducation criada.";
            response.success = true;
            return response;

        }catch (Exception e){
            response.message = e.getMessage();
            return response;
        }
    }

    public Response get(Map<String, Object> body) {
        Response response = new Response();
        try {
            String id = (String)body.get("typeEducationId");

            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro id é nulo.");
            }

            TypeEducation typeEducation = findById(UUID.fromString(id)).get();

            response.body.put("typeEducation", typeEducation);

            response.success = true;
            return response;
        }catch (Exception e){
            response.message = e.getMessage();
            return response;
        }
    }

    public Response findAll(Map<String, Object> body) {
        Response response = new Response();
        try {

            List<TypeEducation> typeEducations = findAll();

            response.body.put("lista", typeEducations);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }


    public Response remove(Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("tyEducationId");
            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro typeEducationId é nulo.");
            }

            deleteLogic(UUID.fromString(id));

            response.message = "TypeEdcation removida logicamente";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }
}
