package me.universi.competence.controller;

import me.universi.api.entities.Response;
import me.universi.competence.services.CompetenceTypeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompetenceTypeController {
    private final CompetenceTypeService competenceTypeService;

    public CompetenceTypeController(CompetenceTypeService competenceTypeService) {
        this.competenceTypeService = competenceTypeService;
    }

    @PostMapping(value = "/admin/competencetype/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
       return competenceTypeService.create(body);
    }

    @PostMapping(value = "/admin/competencetype/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        return competenceTypeService.update(body);
    }

    @PostMapping(value = "/admin/competencetype/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        return competenceTypeService.remove(body);
    }

    @PostMapping(value = "/competencetype/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        return competenceTypeService.get(body);
    }

    @PostMapping(value = "/competencetype/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response findAll(@RequestBody Map<String, Object> body) {
        return competenceTypeService.findAll(body);
    }
}
