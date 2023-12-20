package me.universi.capacity.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.universi.api.entities.Response;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.service.ContentService;
import me.universi.user.services.UserService;

@RestController
@RequestMapping("/api/capacity/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/all")
    public Response list() {
        return Response.buildResponse(response -> {
            response.body.put("contents", contentService.findAll());
        });
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object contentId = body.get("id");
            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            Content content = contentService.findById(String.valueOf(contentId));
            if(content == null) {
                throw new CapacityException("Conteúdo não encontrado.");
            }

            response.body.put("content", content);
        });
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object url =         body.get("url");
            Object title =       body.get("title");
            Object image =       body.get("image");
            Object description = body.get("description");
            Object rating =      body.get("rating");
            Object type =        body.get("type");

            // id or array of ids
            Object addCategoriesByIds = body.get("addCategoriesByIds");
            Object addFoldersByIds =    body.get("addFoldersByIds");

            boolean result = contentService.handleCreate(title, url, image, description, rating, type, addCategoriesByIds, addFoldersByIds);
            if(!result) {
                throw new CapacityException("Erro ao salvar o conteúdo.");
            }

            response.message = "Conteúdo criado com sucesso.";
        });
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object contentId = body.get("id");
            Object url =         body.get("url");
            Object title =       body.get("title");
            Object image =       body.get("image");
            Object description = body.get("description");
            Object rating =      body.get("rating");
            Object type =        body.get("type");

            // id or array of ids
            Object addCategoriesByIds =    body.get("addCategoriesByIds");
            Object removeCategoriesByIds = body.get("removeCategoriesByIds");
            Object addFoldersByIds =       body.get("addFoldersByIds");
            Object removeFoldersByIds =    body.get("removeFoldersByIds");

            boolean result = contentService.handleEdit(contentId, url, title, image, description, rating, type, addCategoriesByIds, removeCategoriesByIds, addFoldersByIds, removeFoldersByIds);
            if(!result) {
                throw new CapacityException("Erro ao salvar o conteúdo.");
            }

            response.message = "Conteúdo atualizado com sucesso.";
        });
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response delete(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object contentId = body.get("id");
            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            Content content = contentService.findById(String.valueOf(contentId));
            if(content == null) {
                throw new CapacityException("Conteúdo não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(content.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new CapacityException("Você não tem permissão para apagar este conteúdo.");
                }
            }

            boolean result = contentService.delete(UUID.fromString(String.valueOf(contentId)));
            if(!result) {
                throw new CapacityException("Erro ao deletar o conteúdo.");
            }

            response.message = "Conteúdo deletado com sucesso.";
        });
    }

    @PostMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response status(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object contentId = body.get("contentId");

            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            ContentStatus contentStatus = contentService.findStatusById((String)contentId);

            response.body.put("contentStatus", contentStatus);
        });
    }

    @PostMapping(value = "/status/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response edit_status(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object contentId = body.get("contentId");
            Object status = body.get("contentStatusType");

            if (contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }
            if (status == null || String.valueOf(status).isEmpty()) {
                throw new CapacityException("Status do conteúdo não informado.");
            }

            ContentStatusType contentStatusType = ContentStatusType.valueOf(String.valueOf(status));

            ContentStatus contentStatus = contentService.setStatus(String.valueOf(contentId), contentStatusType);

            response.body.put("contentStatus", contentStatus);
            response.message = "Status do conteúdo atualizado com sucesso.";
        });
    }
}
