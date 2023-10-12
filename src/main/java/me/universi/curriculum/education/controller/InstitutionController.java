package me.universi.curriculum.education.controller;

import me.universi.api.entities.Response;
import me.universi.curriculum.education.entities.Institution;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.exceptions.TypeEducationException;
import me.universi.curriculum.education.servicies.InstitutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum/institution")
public class InstitutionController {

    private InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService){
        this.institutionService = institutionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Institution save(@RequestBody Institution institution) throws Exception{
        return  institutionService.save(institution);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Institution> getAll() throws Exception{
        return institutionService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Institution> getInstitution(@PathVariable UUID id){
        return institutionService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Institution update(@RequestBody Institution newTypeEducation, @PathVariable UUID id) throws Exception {
        return institutionService.update(newTypeEducation, id);
    }
    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
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
            institutionService.save(newInstitution);

            response.message = "Institution criada.";
            response.success = true;
            return response;

        }catch (Exception e){
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {
            String id = (String)body.get("InstitutionId");

            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro id é nulo.");
            }

            Institution institution = institutionService.findById(UUID.fromString(id)).get();

            response.body.put("institution", institution);

            response.success = true;
            return response;
        }catch (Exception e){
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/listar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            List<Institution> institutions = institutionService.findAll();

            response.body.put("lista", institutions);

            response.message = "Operação realizada com exito.";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            String id = (String)body.get("institutionId");
            if(id.isEmpty()) {
                throw new TypeEducationException("Parametro institutionId é nulo.");
            }

            institutionService.deleteLogic(UUID.fromString(id));

            response.message = "institution removida logicamente";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }
}
