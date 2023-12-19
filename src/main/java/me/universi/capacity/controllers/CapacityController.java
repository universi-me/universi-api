package me.universi.capacity.controllers;


import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import me.universi.api.entities.Response;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.enums.ContentType;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.service.CapacityService;


@RestController
@RequestMapping("/api/capacity")
public class CapacityController {

    private final CapacityService capacityService;
    private final GroupService groupService;

    public CapacityController(CapacityService capacityService, GroupService groupService) {
        this.capacityService = capacityService;
        this.groupService = groupService;
    }

    @GetMapping("/categories")
    public Response categoryList() {
        return Response.buildResponse(response -> {
            response.body.put("categories", capacityService.getAllCategories());
        });
    }

    @GetMapping("/folders")
    public Response foldersList() {
        return Response.buildResponse(response -> {
            response.body.put("folders", capacityService.getAllFolders());
        });
    }

    @PostMapping(value = "/category/contents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_content_by_category(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("contents", capacityService.findContentsByCategory(String.valueOf(categoryId)));

        });
    }

    @PostMapping(value = "/category/folders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_folder_by_category(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("folders", capacityService.findFoldersByCategory(String.valueOf(categoryId)));

        });
    }

    @PostMapping(value = "/folder/contents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_content_by_folder(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            response.body.put("contents", capacityService.findContentsByFolder(String.valueOf(folderId)));

        });
    }

    @PostMapping(value = "/category/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get_category(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("category", capacityService.findCategoryById(UUID.fromString(String.valueOf(categoryId))));
            response.success = true;

        });
    }

    @PostMapping(value = "/category/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create_category(@RequestBody Map<String, Object> body) {
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

            boolean result = capacityService.saveOrUpdateCategory(category);
            if(!result) {
                throw new CapacityException("Erro ao salvar o categoria.");
            }

            response.message = "Categoria criada com sucesso.";

        });
    }

    @PostMapping(value = "/category/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response edit_category(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            Object name =  body.get("name");
            Object image = body.get("image");

            Category category = capacityService.findCategoryById(String.valueOf(categoryId));
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

            boolean result = capacityService.saveOrUpdateCategory(category);
            if(!result) {
                throw new CapacityException("Erro ao editar o categoria.");
            }

            response.message = "Categoria atualizada com sucesso.";

        });
    }

    @PostMapping(value = "/category/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete_category(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da Categoria não informado.");
            }

            Category category = capacityService.findCategoryById(UUID.fromString(String.valueOf(categoryId)));
            if(category == null) {
                throw new CapacityException("Categoria não encontrada.");
            }

            if(!UserService.getInstance().isSessionOfUser(category.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new CapacityException("Você não tem permissão para editar esta categoria.");
                }
            }

            boolean result = capacityService.deleteCategory(UUID.fromString(String.valueOf(categoryId)));
            if(!result) {
                throw new CapacityException("Erro ao deletar Categoria.");
            }

            response.message = "Categoria deletada com sucesso.";

        });
    }

    @PostMapping(value = "/folder/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get_folder(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            Folder folder = capacityService.findFolderById(String.valueOf(folderId));

            capacityService.checkFolderPermissions(folder, false);

            response.body.put("folder", folder);

        });
    }

    @PostMapping(value = "/folder/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create_folder(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object name =           body.get("name");
            Object image =          body.get("image");
            Object description =    body.get("description");
            Object rating =         body.get("rating");
            Object publicFolder =   body.get("publicFolder");
            Object groupId =        body.get("groupId");
            Object groupPath =      body.get("groupPath");

            // id or array of ids
            Object addCategoriesByIds =            body.get("addCategoriesByIds");
            Object addGrantedAccessGroupByIds =    body.get("addGrantedAccessGroupByIds");

            if(name == null || String.valueOf(name).isEmpty()) {
                throw new CapacityException("Parametro name não informado.");
            }

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);
            if(group == null) {
                throw new CapacityException("Grupo não encontrado.");
            }

            User user = UserService.getInstance().getUserInSession();

            groupService.verifyPermissionToEditGroup(group, user);

            Folder folder = new Folder();

            folder.setName(String.valueOf(name));
            folder.setAuthor(user.getProfile());
            folder.setOwnerGroup(group);

            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    folder.setImage(imageStr);
                }
            }
            if(description != null) {
                String descriptionStr = String.valueOf(description);
                if(!descriptionStr.isEmpty()) {
                    folder.setDescription(descriptionStr);
                }
            }
            if(rating != null) {
                String ratingStr = String.valueOf(rating);
                if(!ratingStr.isEmpty()) {
                    folder.setRating(Integer.parseInt(ratingStr));
                }
            }
            if(publicFolder != null) {
                String publicFolderStr = String.valueOf(publicFolder);
                if(!publicFolderStr.isEmpty()) {
                    folder.setPublicFolder(Boolean.parseBoolean(publicFolderStr));
                }
            }

            if(addCategoriesByIds != null) {
                capacityService.addOrRemoveCategoriesFromContentOrFolder(folder, addCategoriesByIds, true, false);
            }

            if(addGrantedAccessGroupByIds != null) {
                capacityService.addOrRemoveGrantedAccessGroupFromFolder(folder, addGrantedAccessGroupByIds, true);
            }



            boolean result = capacityService.saveOrUpdateFolder(folder);
            if(!result) {
                throw new CapacityException("Erro ao salvar o pasta.");
            }

            response.message = "Pasta criada com sucesso.";

        });
    }

    @PostMapping(value = "/folder/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response edit_folder(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            Object name =           body.get("name");
            Object image =          body.get("image");
            Object description =    body.get("description");
            Object rating =         body.get("rating");
            Object publicFolder =   body.get("publicFolder");

            // id or array of ids
            Object addCategoriesByIds =            body.get("addCategoriesByIds");
            Object removeCategoriesByIds =         body.get("removeCategoriesByIds");
            Object addGrantedAccessGroupByIds =    body.get("addGrantedAccessGroupByIds");
            Object removeGrantedAccessGroupByIds = body.get("removeGrantedAccessGroupByIds");

            Folder folder = capacityService.findFolderById(String.valueOf(folderId));

            capacityService.checkFolderPermissions(folder, true);

            if(name != null) {
                String nameStr = String.valueOf(name);
                if(!nameStr.isEmpty()) {
                    folder.setName(nameStr);
                }
            }
            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    folder.setImage(imageStr);
                }
            }
            if(description != null) {
                String descriptionStr = String.valueOf(description);
                if(!descriptionStr.isEmpty()) {
                    folder.setDescription(descriptionStr);
                }
            }
            if(rating != null) {
                String ratingStr = String.valueOf(rating);
                if(!ratingStr.isEmpty()) {
                    folder.setRating(Integer.parseInt(ratingStr));
                }
            }
            if(publicFolder != null) {
                String publicFolderStr = String.valueOf(publicFolder);
                if(!publicFolderStr.isEmpty()) {
                    folder.setPublicFolder(Boolean.parseBoolean(publicFolderStr));
                }
            }

            if(addCategoriesByIds != null) {
                capacityService.addOrRemoveCategoriesFromContentOrFolder(folder, addCategoriesByIds, true, false);
            }
            if(removeCategoriesByIds != null) {
                capacityService.addOrRemoveCategoriesFromContentOrFolder(folder, removeCategoriesByIds, false, false);
            }

            if(addGrantedAccessGroupByIds != null) {
                capacityService.addOrRemoveGrantedAccessGroupFromFolder(folder, addGrantedAccessGroupByIds, true);
            }
            if(removeGrantedAccessGroupByIds != null) {
                capacityService.addOrRemoveGrantedAccessGroupFromFolder(folder, removeGrantedAccessGroupByIds, false);
            }

            boolean result = capacityService.saveOrUpdateFolder(folder);
            if(!result) {
                throw new CapacityException("Erro ao editar o pasta.");
            }

            response.message = "Pasta atualizada com sucesso.";

        });
    }

    @PostMapping(value = "/folder/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete_folder(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            Folder folder = capacityService.findFolderById(UUID.fromString(String.valueOf(folderId)));

            capacityService.checkFolderPermissions(folder, true);

            boolean result = capacityService.deleteFolder(UUID.fromString(String.valueOf(folderId)));
            if(!result) {
                throw new CapacityException("Erro ao deletar pasta.");
            }

            response.message = "Pasta deletada com sucesso.";

        });
    }

    @PostMapping(value = "/folder/content/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response folder_add_content(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");

            // id or array of ids
            Object contentIds    = body.get("contentIds");

            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }
            if(contentIds == null) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            capacityService.addOrRemoveContentFromFolder(folderId, contentIds, true);

            response.message = "Conteúdo adicionado a pasta com sucesso.";

        });
    }

    @PostMapping(value = "/folder/content/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response folder_remove_content(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");

            // id or array of ids
            Object contentIds    = body.get("contentIds");

            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }
            if(contentIds == null) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            capacityService.addOrRemoveContentFromFolder(folderId, contentIds, false);

            response.message = "Conteúdo removido da pasta com sucesso.";
        });
    }

    @PostMapping(value = "/folder/content/position", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response folder_move_content(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Object folderId = body.get("folderId");
            Object contentId = body.get("contentId");
            Object toIndex = body.get("toIndex");

            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }
            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }
            if(toIndex == null || String.valueOf(toIndex).isEmpty()) {
                throw new CapacityException("Índice não informado.");
            }

            int toIndexInt = Integer.parseInt(String.valueOf(toIndex));

            capacityService.setNewPositionOfContentInFolder(folderId, contentId, toIndexInt);

            response.message = "Conteúdo ordenado na pasta com sucesso.";
        });
    }

    // assign folder to profile or profiles
    @PostMapping(value = "/folder/assign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response assign_folder(@RequestBody Map<String, Object> body){

        return Response.buildResponse( response -> {
            Object profilesIds = body.get("profilesIds");
            Object folderId = body.get("folderId");

            if (profilesIds == null || String.valueOf(profilesIds).isEmpty())
                throw new CapacityException("profilesIds é inválido.");
            if (folderId == null || String.valueOf(folderId).isEmpty())
                throw new CapacityException("folderId é inválido.");

            Folder folder = capacityService.findFolderById(String.valueOf(folderId));
            if (folder == null)
                throw new CapacityException("folderId é inválido");

            if (profilesIds instanceof Collection) {
                capacityService.assignFolderToMultipleProfiles((Collection<String>) profilesIds, folder);
            } else {
                capacityService.assignFolderToProfile(UUID.fromString(String.valueOf(profilesIds)) , folder);
            }
        });
    }

    // assigned profiles to folder
    @GetMapping(value = "/folder/assigned", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response users_assigned_to_folder(@RequestBody Map<String, Object> body){
        return Response.buildResponse(response -> {

            Object folderId = body.get("folderId");

            if(folderId == null || String.valueOf(folderId).isEmpty() || capacityService.findFolderById((UUID.fromString(String.valueOf(folderId)))) == null)
                throw new CapacityException("folderId é inválido.");

           response.body.put("profilesIds", capacityService.findAssignedProfilesByFolder((UUID.fromString(String.valueOf(folderId)))));
        });
    }

}
