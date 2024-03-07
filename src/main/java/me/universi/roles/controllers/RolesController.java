package me.universi.roles.controllers;

import java.util.Map;
import me.universi.api.entities.Response;
import me.universi.roles.entities.Roles;
import me.universi.roles.entities.RolesFeature;
import me.universi.roles.exceptions.RolesException;
import me.universi.roles.services.RolesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/roles")
public class RolesController {
    private final RolesService rolesService;

    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Roles roles = rolesService.createRole(body);
            response.body.put("roles", roles);
            response.message = "Papel \"" + roles.name + "\" criado com sucesso.";
        });
    }

    @PostMapping(value = "/edit", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("roles", rolesService.editRole(body));
            response.message = "Papel editado com sucesso.";
        });
    }

    @PostMapping(value = "/list", produces = "application/json")
    @ResponseBody
    public Response paper_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("roles", rolesService.listRolesGroup(body));
        });
    }

    @PostMapping(value = "/feature/toggle", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_feature_active(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            RolesFeature rolesFeature = rolesService.setRolesFeatureValue(body);
            response.message = "Funcionalidade \"" + rolesFeature.featureType.label + "\" foi alterada " +
                               " com sucesso para \"" + rolesFeature.roles.name + "\".";
        });
    }

    // assign paper
    @PostMapping(value = "/assign", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_assign(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            if(rolesService.assignRole(body)) {
                response.message = "Papel atribuído com sucesso.";
            } else {
                throw new RolesException("Não foi possível atribuir papel.");
            }
        });
    }

    // assigned paper
    @PostMapping(value = "/assigned", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_assigned(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("roles", rolesService.getAssignedPaper(body));
        });
    }

    // list paper profiles by group
    @PostMapping(value = "/participants/list", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_profile_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("participants", rolesService.listPaperProfile(body));
        });
    }

    // list all my roles
    @GetMapping(value = "", produces = "application/json")
    @ResponseBody
    public Response all_my_roles() {
        return Response.buildResponse(response -> {
            response.body.put("roles", rolesService.getAllRolesSession());
        });
    }

}
