package me.universi.competence.services;

import me.universi.Sys;
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

import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompetenceService {
    private final CompetenceRepository competenceRepository;
    private final ProfileService profileService;
    private final UserService userService;
    private final CompetenceTypeService competenceTypeService;
    private final CompetenceProfileService competenceProfileService;

    public CompetenceService(CompetenceRepository competenceRepository, ProfileService profileService, UserService userService, CompetenceTypeService competenceTypeService, CompetenceProfileService competenceProfileService){
        this.competenceRepository = competenceRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.competenceTypeService = competenceTypeService;
        this.competenceProfileService = competenceProfileService;
    }

    public static CompetenceService getInstance() {
        return Sys.context.getBean("competenceService", CompetenceService.class);
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

    public boolean profileHasCompetence(@NotNull Profile profile, @NotNull CompetenceType competenceType) {
        return competenceProfileService.findCompetenceByProfile(profile)
            .stream()
            .anyMatch(c -> c.getCompetenceType().getId().equals(competenceType.getId()));
    }

    public void deleteAll(Collection<Competence> competences) {
        for(Competence competence : competences) {
            competence.setDeleted(true);
        }
        competenceRepository.saveAll(competences);
    }

    public void addCompetenceInProfile(@NotNull Profile profile,Competence newCompetence) throws ProfileException {
        competenceProfileService.addToProfile( profile, newCompetence );
    }

    public void delete(UUID id) {
        Competence competence = findFirstById(id);
        deleteLogico(competence);
    }

    public Competence create(@NotNull UUID competenceTypeId, @NotNull String description, @NotNull Integer level, @NotNull Profile profile) {
        CompetenceType compT = competenceTypeService.findFirstById(competenceTypeId);
        if(compT == null)
            throw new CompetenceException("Tipo de Competência não encontrado.");

        return create(compT, description, level, profile);
    }

    public Competence create(@NotNull CompetenceType competenceType, @NotNull String description, @NotNull Integer level, @NotNull Profile profile) {
        Competence newCompetence = new Competence();
        newCompetence.setCompetenceType(competenceType);
        newCompetence.setDescription(description);
        newCompetence.setLevel(level);

        newCompetence = save(newCompetence);
        addCompetenceInProfile(profile, newCompetence);

        return newCompetence;
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
        var hasCompetence = competenceProfileService.findByProfile( profile, competence.getCompetenceType() ).isPresent();

        if(!hasCompetence) {
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

    public boolean validate( Competence competence ) {
        return competence != null
            && competenceTypeService.validate( competence.getCompetenceType() );
    }
}
