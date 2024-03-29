package me.universi.roles.controllers;

import java.util.Map;
import me.universi.api.entities.Response;
import me.universi.roles.entities.Roles;
import me.universi.roles.entities.RolesFeature;
import me.universi.roles.entities.RolesProfile;
import me.universi.roles.enums.Permission;
import me.universi.roles.services.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/roles")
public class RolesController {
    private final RolesService rolesService;

    @Autowired
    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response roles_create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Roles roles = rolesService.createRole(body);
            response.body.put("roles", roles);
            response.message = "Papel \"" + roles.name + "\" criado com sucesso.";
        });
    }

    @PostMapping(value = "/edit", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response roles_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Roles roles = rolesService.editRole(body);
            response.body.put("roles", roles);
            response.message = "Papel \""+ roles.name +"\" editado com sucesso.";
        });
    }

    @PostMapping(value = "/list", produces = "application/json")
    @ResponseBody
    public Response roles_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("roles", rolesService.listRolesGroup(body));
        });
    }

    @PostMapping(value = "/feature/toggle", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response roles_feature_active(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            RolesFeature rolesFeature = rolesService.setRolesFeatureValue(body);
            response.message = "Funcionalidade " + rolesFeature.featureType.label + " foi alterada " +
                               " com sucesso para "+ Permission.getPermissionName(rolesFeature.permission) +" em \"" + rolesFeature.roles.name + "\".";
        });
    }

    // assign roles
    @PostMapping(value = "/assign", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response roles_assign(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            RolesProfile rolesProfile = rolesService.assignRole(body);
            response.message = "Papel \""+ rolesProfile.roles.name +"\" atribuído com sucesso para \""+ rolesProfile.profile.getFirstname() +"\".";
        });
    }

    // assigned roles
    @PostMapping(value = "/assigned", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response roles_assigned(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("roles", rolesService.getAssignedRoles(body));
        });
    }

    // list roles profiles by group
    @PostMapping(value = "/participants/list", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response roles_profile_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("participants", rolesService.listRolesProfile(body));
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
