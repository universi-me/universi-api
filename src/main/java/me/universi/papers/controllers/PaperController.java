package me.universi.papers.controllers;

import java.util.Map;
import me.universi.api.entities.Response;
import me.universi.papers.entities.Paper;
import me.universi.papers.entities.PaperFeature;
import me.universi.papers.exceptions.PaperException;
import me.universi.papers.services.PaperService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/paper")
public class PaperController {
    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Paper paper = paperService.createPaper(body);
            response.body.put("paper", paper);
            response.message = "Papel \"" + paper.name + "\" criado com sucesso.";
        });
    }

    @PostMapping(value = "/edit", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("paper", paperService.editPaper(body));
            response.message = "Papel editado com sucesso.";
        });
    }

    @PostMapping(value = "/list", produces = "application/json")
    @ResponseBody
    public Response paper_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("papers", paperService.listPaper(body));
        });
    }

    @PostMapping(value = "/feature/toggle", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_feature_active(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            PaperFeature paperFeature = paperService.setValuePaperFeature(body);
            response.message = "Funcionalidade \"" + paperFeature.featureType.label + "\" foi alterada " +
                               " com sucesso para \"" + paperFeature.paper.name + "\".";
        });
    }

    // assign paper
    @PostMapping(value = "/assign", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_assign(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            if(paperService.assignPaper(body)) {
                response.message = "Papel atribuído com sucesso.";
            } else {
                throw new PaperException("Não foi possível atribuir papel.");
            }
        });
    }

    // assigned paper
    @PostMapping(value = "/assigned", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_assigned(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("paper", paperService.getAssignedPaper(body));
        });
    }

    // list paper profiles by group
    @PostMapping(value = "/participants/list", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response paper_profile_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            response.body.put("participants", paperService.listPaperProfile(body));
        });
    }

}
