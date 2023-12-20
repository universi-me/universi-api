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
import me.universi.capacity.entidades.Category;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.service.CategoryService;
import me.universi.capacity.service.ContentService;
import me.universi.capacity.service.FolderService;
import me.universi.user.services.UserService;

@RestController
@RequestMapping("/api/capacity/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final ContentService contentService;
    private final FolderService folderService;

    public CategoryController(CategoryService categoryService, ContentService contentService, FolderService folderService) {
        this.categoryService = categoryService;
        this.contentService = contentService;
        this.folderService = folderService;
    }

    @GetMapping("/all")
    public Response list() {
        return Response.buildResponse(response -> {
            response.body.put("categories", categoryService.findAll());
        });
    }

    @PostMapping(value = "/contents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response contentsByCategory(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("contents", contentService.findByCategory(String.valueOf(categoryId)));
        });
    }

    @PostMapping(value = "/folders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response foldersByCategory(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("folders", folderService.findByCategory(String.valueOf(categoryId)));
        });
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("category", categoryService.findById(UUID.fromString(String.valueOf(categoryId))));
        });
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object name = body.get("name");
            Object image = body.get("image");

            if(name == null || String.valueOf(name).isEmpty()) {
                throw new CapacityException("Parametro name não informado.");
            }

            Category category = new Category();
            category.setName(String.valueOf(name));

            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    category.setImage(imageStr);
                }
            }

            category.setAuthor(UserService.getInstance().getUserInSession().getProfile());

            boolean result = categoryService.saveOrUpdate(category);
            if(!result) {
                throw new CapacityException("Erro ao salvar o categoria.");
            }

            response.message = "Categoria criada com sucesso.";
        });
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            Object name =  body.get("name");
            Object image = body.get("image");

            Category category = categoryService.findById(String.valueOf(categoryId));
            if(category == null) {
                throw new CapacityException("Categoria não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(category.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new CapacityException("Você não tem permissão para editar esta categoria.");
                }
            }

            if(name != null) {
                String nameStr = String.valueOf(name);
                if(!nameStr.isEmpty()) {
                    category.setName(nameStr);
                }
            }
            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    category.setImage(imageStr);
                }
            }

            boolean result = categoryService.saveOrUpdate(category);
            if(!result) {
                throw new CapacityException("Erro ao editar o categoria.");
            }

            response.message = "Categoria atualizada com sucesso.";
        });
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response delete(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da Categoria não informado.");
            }

            Category category = categoryService.findById(UUID.fromString(String.valueOf(categoryId)));
            if(category == null) {
                throw new CapacityException("Categoria não encontrada.");
            }

            if(!UserService.getInstance().isSessionOfUser(category.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new CapacityException("Você não tem permissão para editar esta categoria.");
                }
            }

            boolean result = categoryService.delete(UUID.fromString(String.valueOf(categoryId)));
            if(!result) {
                throw new CapacityException("Erro ao deletar Categoria.");
            }

            response.message = "Categoria deletada com sucesso.";
        });
    }
}
