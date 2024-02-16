package me.universi.competence.services;

import me.universi.api.entities.Response;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.repositories.CompetenceTypeRepository;
import me.universi.user.services.UserService;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CompetenceTypeService {
    private final CompetenceTypeRepository competenceTypeRepository;

    public CompetenceTypeService(CompetenceTypeRepository competenceTypeRepository) {
        this.competenceTypeRepository = competenceTypeRepository;
    }

    public CompetenceType findFirstById(UUID id) {
        CompetenceType competenceType = competenceTypeRepository.findFirstById(id).orElse(null);

        if(hasAccessToCompetenceType(competenceType)){
            return competenceType;
        }else{
            return null;
        }
    }

    public CompetenceType findFirstById(String id) {
        try {
            return findFirstById(UUID.fromString(id));
        } catch (IllegalArgumentException invalidUUID) {
            return null;
        }
    }

    public CompetenceType findFirstByName(String name) {
        CompetenceType competenceType = competenceTypeRepository.findFirstByName(name).orElse(null);

        if (hasAccessToCompetenceType(competenceType)) {
            return competenceType;
        }else{
            return null;
        }
    }

    private CompetenceType save(CompetenceType competenceType) {
        return competenceTypeRepository.saveAndFlush(competenceType);
    }

    private void delete(CompetenceType competenceType) {
        competenceType.setDeleted(true);
        save(competenceType);
    }

    private List<CompetenceType> findAll() {
        return competenceTypeRepository.findAll();
    }

    public CompetenceType update(CompetenceType newCompetenceType, UUID id) {
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

            if (!UserService.getInstance().isUserAdminSession()) {
                response.status = 403;
                throw new CompetenceException("Esta operação não é permitida para este usuário.");
            }

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

            if (!UserService.getInstance().isUserAdminSession()) {
                response.status = 403;
                throw new CompetenceException("Esta operação não é permitida para este usuário.");
            }

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
            if (competenceType == null || !hasAccessToCompetenceType(competenceType)) {
                throw new CompetenceException("Tipo de Competência não encontrada.");
            }

            response.body.put("competenceType", competenceType);
            response.success = true;

        });
    }

    public Response findAll(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<CompetenceType> competences = findAll()
                .stream()
                .filter(this::hasAccessToCompetenceType)
                .toList();

            response.body.put("list", competences);
            response.success = true;

        });
    }

    public boolean hasAccessToCompetenceType(CompetenceType competence) {
        if (competence == null) return false;

        return competence.isReviewed() || UserService.getInstance().isUserAdminSession();
    }
}
