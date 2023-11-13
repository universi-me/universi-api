package me.universi.curriculum.education.servicies;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.education.repositories.InstitutionRepository;
import me.universi.user.entities.User;
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
        institution.setDeleted(true);
        save(institution);
    }

    public Response create(Map<String, Object> body) {
        Response response = new Response();

        try {
            String name = (String) body.get("name");
            if(name.isBlank() || name.isEmpty()){
                throw new InstantiationException("Parametro name passado é nulo ou vazio");
            }

            String description = (String) body.get("description");
            if(description.isEmpty() || description.isBlank()){
                throw new InstantiationException("Parametro description é nulo ou vazio");
            }

            Institution newInstitution = new Institution(name, description);
            institutionRepository.saveAndFlush(newInstitution);

            response.message = "Institution criada.";
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
            String id = (String)body.get("InstitutionId");

            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro id é nulo.");
            }

            Institution institution = findById(UUID.fromString(id)).get();

            response.body.put("institution", institution);

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

            List<Institution> institutions = institutionRepository.findAll();

            response.body.put("lista", institutions);

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

            String id = (String)body.get("institutionId");
            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro institutionId é nulo.");
            }

            deleteLogic(UUID.fromString(id));

            response.message = "institution removida logicamente";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

}
