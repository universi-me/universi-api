package me.universi.competence.services;

import me.universi.api.entities.Response;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.repositories.CompetenceTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompetenceTypeService {
    private final CompetenceTypeRepository competenceTypeRepository;

    public CompetenceTypeService(CompetenceTypeRepository competenceTypeRepository) {
        this.competenceTypeRepository = competenceTypeRepository;
    }

    public CompetenceType findFirstById(UUID id) {
        Optional<CompetenceType> optionalCompetenceType = competenceTypeRepository.findFirstById(id);
        if(optionalCompetenceType.isPresent()){
            return optionalCompetenceType.get();
        }else{
            return null;
        }
    }

    public CompetenceType findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
    }

    public CompetenceType findFirstByName(String name) {
        Optional<CompetenceType> optionalCompetenceType = competenceTypeRepository.findFirstByName(name);
        if(optionalCompetenceType.isPresent()){
            return optionalCompetenceType.get();
        }else{
            return null;
        }
    }

    public CompetenceType save(CompetenceType competenceType) {
        return competenceTypeRepository.saveAndFlush(competenceType);
    }

    public void delete(CompetenceType competenceType) {
        competenceType.setDeleted(true);
        save(competenceType);
    }

    public List<CompetenceType> findAll() {
        return competenceTypeRepository.findAll();
    }

    public CompetenceType update(CompetenceType newCompetenceType, UUID id) throws Exception{
        return competenceTypeRepository.findFirstById(id).map(competenceType -> {
            competenceType.setName(newCompetenceType.getName());
            return save(competenceType);
        }).orElseGet(()->{
            try {
                return save(newCompetenceType);
            }catch (Exception e){
                return null;
            }
        });
    }

    public void delete(UUID id) {
        CompetenceType competenceType = findFirstById(id);
        delete(competenceType);
    }

    public Response create(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String name = (String)body.get("name");
            if(name == null) {
                throw new CompetenceException("Parâmetro nome é nulo.");
            }

            if(findFirstByName(name) != null) {
                throw new CompetenceException("Tipo de competência já existe.");
            }

            CompetenceType newCompetence = new CompetenceType();
            newCompetence.setName(name);

            save(newCompetence);

            response.message = "Tipo de Competência Criada";
            response.success = true;

        });
    }

    public Response update(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            String name = (String)body.get("name");

            CompetenceType competenceType = findFirstById(id);
            if (competenceType == null) {
                throw new CompetenceException("Tipo de Competência não encontrada.");
            }

            if(findFirstByName(name) != null) {
                throw new CompetenceException("Tipo de competência já existe.");
            }

            if(name != null) {
                competenceType.setName(name);
            }

            save(competenceType);

            response.message = "Tipo de Competência atualizada";
            response.success = true;

        });
    }

    public Response remove(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            CompetenceType competenceType = findFirstById(id);
            if (competenceType == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            delete(competenceType);

            response.message = "Tipo de Competência removida";
            response.success = true;

        });
    }

    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            CompetenceType competenceType = findFirstById(id);
            if (competenceType == null) {
                throw new CompetenceException("Tipo de Competência não encontrada.");
            }

            response.body.put("competenceType", competenceType);
            response.success = true;

        });
    }

    public Response findAll(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<CompetenceType> competences = findAll();

            response.body.put("list", competences);
            response.success = true;

        });
    }
}
