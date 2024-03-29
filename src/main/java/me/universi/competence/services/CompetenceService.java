package me.universi.competence.services;

import me.universi.api.entities.Response;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.repositories.CompetenceRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompetenceService {
    private final CompetenceRepository competenceRepository;
    private final ProfileService profileService;
    private final UserService userService;
    private final CompetenceTypeService competenceTypeService;

    public CompetenceService(CompetenceRepository competenceRepository, ProfileService profileService, UserService userService, CompetenceTypeService competenceTypeService){
        this.competenceRepository = competenceRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.competenceTypeService = competenceTypeService;
    }

    public Competence findFirstById(UUID id) {
        Optional<Competence> optionalCompetence = competenceRepository.findFirstById(id);
        if(optionalCompetence.isPresent()){
            return optionalCompetence.get();
        }else{
            return null;
        }
    }

    public Competence save(Competence competence) {
        try {
            return competenceRepository.saveAndFlush(competence);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Competence findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
    }

    public void deleteLogico(Competence competence) {
        competence.setDeleted(true);
        save(competence);
    }

    public List<Competence> findAll() {
        return competenceRepository.findAll();
    }

    public void update(Competence competence){ competenceRepository.saveAndFlush(competence); }

    public boolean profileHasCompetence(Profile profile, Competence competence) {
        try {
            if(profile.getCompetences() != null) {
                for(Competence compNow : profile.getCompetences()) {
                    if(Objects.equals(competence.getId(), compNow.getId())) {
                        return true;
                    }
                }
            }
        }catch (Exception e) {
            return false;
        }
        return false;
    }

    public void deleteAll(Collection<Competence> competences) {
        for(Competence competence : competences) {
            competence.setDeleted(true);
        }
        competenceRepository.saveAll(competences);
    }

    public void addCompetenceInProfile(User user,Competence newCompetence) throws ProfileException {
        Profile profile = profileService.getProfileByUserIdOrUsername(user.getProfile().getId(), user.getUsername());
        profile.getCompetences().add(newCompetence);
        profileService.save(profile);
    }

    public void delete(UUID id) {
        Competence competence = findFirstById(id);
        deleteLogico(competence);
    }

    public Response create(Map<String, Object> body) {
        return Response.buildResponse(response -> {

            User user = userService.getUserInSession();

            String competenceTypeId = (String)body.get("competenciatipoId");
            if(competenceTypeId == null) {
                throw new CompetenceException("Parametro competenciatipoId é nulo.");
            }

            String description = (String)body.get("descricao");
            if(description == null) {
                throw new CompetenceException("Parametro descricao é nulo.");
            }

            String level = (String)body.get("nivel");
            if(level == null) {
                throw new CompetenceException("Parametro nivel é nulo.");
            }

            CompetenceType compT = competenceTypeService.findFirstById(competenceTypeId);
            if(compT == null) {
                throw new CompetenceException("Tipo de Competência não encontrado.");
            }

            Competence newCompetence = new Competence();
            newCompetence.setCompetenceType(compT);
            newCompetence.setDescription(description);
            newCompetence.setLevel(Integer.parseInt(level));

            save(newCompetence);

            /*Essa linha vai dar problema quando for para adicionar nas vagas a competence
            * esta adicionando na conta do usuario dieretamente*/
            addCompetenceInProfile(user, newCompetence);

            response.message = "Competência Criada e adicionado ao perfil";
            response.success = true;

        });
    }

    public Response update( Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            String competenceTypeId = (String)body.get("competenciaTipoId");
            String description = (String)body.get("descricao");
            String level = (String)body.get("nivel");



            Competence competence = findFirstById(id);
            if (competence == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            checkPermissionForEdit(competence, false);

            if(competenceTypeId != null && !competenceTypeId.isEmpty()) {
                CompetenceType compT = competenceTypeService.findFirstById(competenceTypeId);
                if(compT == null) {
                    throw new CompetenceException("Tipo de Competência não encontrado.");
                }
                competence.setCompetenceType(compT);
            }
            if (description != null) {
                competence.setDescription(description);
            }
            if (level != null) {
                competence.setLevel(Integer.parseInt(level));
            }

            save(competence);

            response.message = "Competência atualizada";
            response.success = true;

        });
    }

    private void checkPermissionForEdit(Competence competence, boolean forDelete) {
        User user = userService.getUserInSession();
        Profile profile = profileService.getProfileByUserIdOrUsername(user.getProfile().getId(), user.getUsername());
        if(!profile.getCompetences().contains(competence)) {
            if(forDelete) {
                if(userService.isUserAdminSession()) {
                    return;
                }
            }
        } else {
            return;
        }
        throw new CompetenceException("Você não tem permissão para editar essa competência.");
    }

    public Response remove( Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            Competence competence = findFirstById(id);
            if (competence == null) {
                throw new CompetenceException("Competência não encontrada.");
            }

            checkPermissionForEdit(competence, true);

            deleteLogico(competence);

            response.message = "Competência removida";
            response.success = true;

        });
    }

    public Response get( Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("competenciaId");
            if(id == null) {
                throw new CompetenceException("Parametro competenciaId é nulo.");
            }

            Competence competence = findFirstById(id);
            if (competence == null) {
                throw new CompetenceException("Competencia não encontrada.");
            }

            response.body.put("competencia", competence);
            response.success = true;

        });
    }

    public Response findAll( Map<String, Object> body) {
        return Response.buildResponse(response -> {

            List<Competence> competences = findAll();

            response.body.put("lista", competences);

            response.message = "Operação realizada com exito.";
            response.success = true;

        });
    }

}
