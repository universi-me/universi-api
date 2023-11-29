package me.universi.curriculum.controller;

import me.universi.api.entities.Response;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.enums.Level;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.education.servicies.TypeEducationService;
import me.universi.curriculum.exceptions.CurriculumException;
import me.universi.curriculum.experience.entities.TypeExperience;
import me.universi.curriculum.experience.servicies.TypeExperienceService;
import me.universi.curriculum.services.CurriculumService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum")
public class CurriculumController {

    public CurriculumService curriculumService;
    private CompetenceTypeService competenceTypeService;
    private TypeExperienceService typeExperienceService;
    private TypeEducationService typeEducationService;
    private ProfileService profileService;


    public CurriculumController(CurriculumService curriculumService, ProfileService profileService, CompetenceTypeService competenceTypeService,
                                TypeExperienceService typeExperienceService,TypeEducationService typeEducationService){
        this.curriculumService = curriculumService;
        this.profileService = profileService;
        this.typeExperienceService = typeExperienceService;
        this.typeEducationService = typeEducationService;
        this.competenceTypeService = competenceTypeService;
    }

    @GetMapping(value = "/user")
    public List<List> curriculumProfile(){
        return curriculumService.mountCurriculum();
    }

    @GetMapping(value = "/search")
    public List<Profile> searchProfileByEducation(@RequestParam(name = "id", required = false)UUID id){
        return curriculumService.getProfileByEducation(id);
    }

    @PostMapping(value = "/filtrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response filtrar(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            /*O que pode ser filtrado
            * CompetenceType
            * Level
            * TypeExperience
            * TypeEducation
            * Grup ?
            * */
            Collection<Profile> profiles;

            String competenceTypeId = (String)body.get("competenceTypeId");
            String typeExperienceId = (String)body.get("typeExperienceId");
            String typeEducationId = (String)body.get("typeEducationIdId");
            Level level = (Level)body.get("level");

            if(level == null && (typeEducationId == null || typeEducationId.equals("")) &&
                    (typeExperienceId == null || typeEducationId.equals("")) &&
                    (competenceTypeId == null || competenceTypeId.equals(""))) {
                throw new CurriculumException("todos os parametros sao nulos.");
            }

            CompetenceType competenceType = competenceTypeService.findFirstById(competenceTypeId);
            TypeExperience typeExperience = typeExperienceService.findById(UUID.fromString(typeExperienceId)).get();
            TypeEducation typeEducation = typeEducationService.findById(UUID.fromString(typeEducationId)).get();


             profiles = curriculumService.filtrarCurriculum(competenceType, level, typeExperience, typeEducation);

            response.body.put("lista dos perfies filtrados por parametros passados", profiles);

            response.message = "Operação realizada com exito.";
            response.success = true;

        });
    }

}
