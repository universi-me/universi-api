package me.universi.capacity.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import me.universi.api.entities.Response;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.enums.ContentType;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.service.CapacityService;
import me.universi.capacity.service.ContentService;
import me.universi.user.services.UserService;

@RestController
@RequestMapping("/api/capacity/content")
public class ContentController {
    private final CapacityService capacityService;
    private final ContentService contentService;

    public ContentController(CapacityService capacityService, ContentService contentService) {
        this.capacityService = capacityService;
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

            if(url == null || String.valueOf(url).isEmpty()) {
                throw new CapacityException("URL do conteúdo não informado.");
            }
            if(title == null || String.valueOf(title).isEmpty()) {
                throw new CapacityException("Título do conteúdo não informado.");
            }
            if(type == null || String.valueOf(type).isEmpty()) {
                throw new CapacityException("Tipo do conteúdo não informado.");
            }

            Content content = new Content();
            content.setAuthor(UserService.getInstance().getUserInSession().getProfile());
            content.setUrl(String.valueOf(url));
            content.setTitle(String.valueOf(title));

            if(type != null) {
                String typeStr = String.valueOf(type);
                if(!typeStr.isEmpty()) {
                    content.setType(ContentType.valueOf(typeStr));
                }
            }
            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    content.setImage(imageStr);
                }
            }
            if(description != null) {
                String descriptionStr = String.valueOf(description);
                if(!descriptionStr.isEmpty()) {
                    content.setDescription(descriptionStr);
                }
            }
            if(rating != null) {
                String ratingStr = String.valueOf(rating);
                if(!ratingStr.isEmpty()) {
                    content.setRating(Integer.parseInt(ratingStr));
                }
            }

            if(addCategoriesByIds != null) {
                capacityService.addOrRemoveCategoriesFromContentOrFolder(content, addCategoriesByIds, true, false);
            }

            if(addFoldersByIds != null) {
                capacityService.addOrRemoveFoldersFromContent(content, addFoldersByIds, true);
            }


            boolean result = contentService.saveOrUpdate(content);
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
            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

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

            Content content = contentService.findById(String.valueOf(contentId));
            if(content == null) {
                throw new CapacityException("Conteúdo não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(content.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new CapacityException("Você não tem permissão para editar este conteúdo.");
                }
            }

            if(url != null) {
                String urlStr = String.valueOf(url);
                if(!urlStr.isEmpty()) {
                    content.setUrl(urlStr);
                }
            }
            if(title != null) {
                String titleStr = String.valueOf(title);
                if(!titleStr.isEmpty()) {
                    content.setTitle(titleStr);
                }
            }
            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    content.setImage(imageStr);
                }
            }
            if(description != null) {
                String descriptionStr = String.valueOf(description);
                if(!descriptionStr.isEmpty()) {
                    content.setDescription(descriptionStr);
                }
            }
            if(rating != null) {
                String ratingStr = String.valueOf(rating);
                if(!ratingStr.isEmpty()) {
                    content.setRating(Integer.parseInt(ratingStr));
                }
            }
            if(type != null) {
                String typeStr = String.valueOf(type);
                if(!typeStr.isEmpty()) {
                    content.setType(ContentType.valueOf(typeStr));
                }
            }

            if(addCategoriesByIds != null) {
                capacityService.addOrRemoveCategoriesFromContentOrFolder(content, addCategoriesByIds, true, false);
            }
            if(removeCategoriesByIds != null) {
                capacityService.addOrRemoveCategoriesFromContentOrFolder(content, removeCategoriesByIds, false, false);
            }

            if(addFoldersByIds != null) {
                capacityService.addOrRemoveFoldersFromContent(content, addFoldersByIds, true);
            }
            if(removeFoldersByIds != null) {
                capacityService.addOrRemoveFoldersFromContent(content, removeFoldersByIds, false);
            }

            boolean result = contentService.saveOrUpdate(content);
            if(!result) {
                throw new CapacityException("Erro ao salvar o conteúdo.");
            }

            response.message = "Conteúdo atualizado com sucesso.";
        });
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
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
