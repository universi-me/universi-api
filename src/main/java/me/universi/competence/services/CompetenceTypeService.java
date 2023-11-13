package me.universi.competence.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import me.universi.api.entities.Response;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.repositories.CompetenceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CompetenceTypeService {
    @Autowired
    private CompetenceTypeRepository competenceTypeRepository;

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
        competenceTypeRepository.delete(competenceType);
    }

    public List<CompetenceType> findAll() {
        return competenceTypeRepository.findAll();
    }

    public CompetenceType update(CompetenceType newCompetenceType, UUID id) throws Exception{
        return competenceTypeRepository.findById(id).map(competenceType -> {
            competenceType.setName(newCompetenceType.getName());
            return competenceTypeRepository.saveAndFlush(competenceType);
        }).orElseGet(()->{
            try {
                return competenceTypeRepository.saveAndFlush(newCompetenceType);
            }catch (Exception e){
                return null;
            }
        });
    }

    public void delete(UUID id) {
        competenceTypeRepository.deleteById(id);
    }

    public Response create(Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String name = (String)body.get("name");
            if(name == null) {
                throw new CompetenceException("Parâmetro nome é nulo.");
            }

            if(findFirstByName(name) != null) {
                throw new CompetenceException("Tipo de competência já existe.");
            }

            CompetenceType newCompetence = new CompetenceType();
            newCompetence.setName(name);

            competenceTypeRepository.saveAndFlush(newCompetence);

            response.message = "Competência Criada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    public Response update(Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            String name = (String)body.get("name");

            CompetenceType competenceType = findFirstById(id);
            if (competenceType == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            if(findFirstByName(name) != null) {
                throw new CompetenceException("Tipo de competência já existe.");
            }

            if(name != null) {
                competenceType.setName(name);
            }

            competenceTypeRepository.saveAndFlush(competenceType);

            response.message = "Competência atualizada";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    public Response remove(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            CompetenceType competenceType = findFirstById(id);
            if (competenceType == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            competenceTypeRepository.delete(competenceType);

            response.message = "Competência removida";
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    public Response get(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            String id = (String)body.get("competenceTypeId");
            if(id == null) {
                throw new CompetenceException("Parâmetro competenceTypeId é nulo.");
            }

            CompetenceType competenceType = findFirstById(id);
            if (competenceType == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            response.body.put("competenceType", competenceType);

            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    public Response findAll(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Response response = new Response();
        try {

            List<CompetenceType> competences = findAll();

            response.body.put("list", competences);
            response.success = true;
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }
}
