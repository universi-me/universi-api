package me.universi.capacity.controllers;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.universi.api.entities.Response;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.entidades.Watch;
import me.universi.capacity.enums.WatchStatus;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.service.CapacityService;


@RestController
@RequestMapping("/api/capacity")
public class CapacityController {

    private final CapacityService capacityService;

    public CapacityController(CapacityService capacityService) {
        this.capacityService = capacityService;
    }

    @GetMapping("/contents")
    public Response contentList() {
        Response response = new Response(); // default
        try {

            response.body.put("contents", capacityService.getAllContents());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @GetMapping("/categories")
    public Response categoryList() {
        Response response = new Response(); // default
        try {

            response.body.put("categories", capacityService.getAllCategories());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @GetMapping("/folders")
    public Response foldersList() {
        Response response = new Response(); // default
        try {

            response.body.put("folders", capacityService.getAllFolders());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/category/contents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_content_by_category(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("contents", capacityService.findContentsByCategory(String.valueOf(categoryId)));
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/category/folders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_folder_by_category(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("folders", capacityService.findFoldersByCategory(String.valueOf(categoryId)));
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/folder/contents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_content_by_folder(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            response.body.put("contents", capacityService.findContentsByFolder(String.valueOf(folderId)));
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/content/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get_content(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object contentId = body.get("id");
            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            Content content = capacityService.findContentById(String.valueOf(contentId));
            if(content == null) {
                throw new CapacityException("Conteúdo não encontrado.");
            }

            response.body.put("content", content);
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/content/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create_content(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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

            Content content = new Content();
            content.setAuthor(UserService.getInstance().getUserInSession().getProfile());
            content.setUrl(String.valueOf(url));
            content.setTitle(String.valueOf(title));
            content.setType(String.valueOf(type));

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


            boolean result = capacityService.saveOrUpdateContent(content);
            if(!result) {
                throw new CapacityException("Erro ao salvar o conteúdo.");
            }

            response.message = "Conteúdo criado com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/content/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response edit_content(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object contentId = body.get("id");
            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            Object url =         body.get("url");
            Object title =       body.get("title");
            Object image =       body.get("image");
            Object description = body.get("description");
            Object rating =      body.get("rating");

            // id or array of ids
            Object addCategoriesByIds =    body.get("addCategoriesByIds");
            Object removeCategoriesByIds = body.get("removeCategoriesByIds");
            Object addFoldersByIds =       body.get("addFoldersByIds");
            Object removeFoldersByIds =    body.get("removeFoldersByIds");

            Content content = capacityService.findContentById(String.valueOf(contentId));
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

            boolean result = capacityService.saveOrUpdateContent(content);
            if(!result) {
                throw new CapacityException("Erro ao salvar o conteúdo.");
            }

            response.message = "Conteúdo atualizado com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/content/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete_content(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object contentId = body.get("id");
            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            Content content = capacityService.findContentById(String.valueOf(contentId));
            if(content == null) {
                throw new CapacityException("Conteúdo não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(content.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new CapacityException("Você não tem permissão para apagar este conteúdo.");
                }
            }

            boolean result = capacityService.deleteContent(UUID.fromString(String.valueOf(contentId)));
            if(!result) {
                throw new CapacityException("Erro ao deletar o conteúdo.");
            }

            response.message = "Conteúdo deletado com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/category/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get_category(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new CapacityException("ID da categoria não informado.");
            }

            response.body.put("category", capacityService.findCategoryById(UUID.fromString(String.valueOf(categoryId))));
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/category/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create_category(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/category/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response edit_category(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/category/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete_category(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/folder/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get_folder(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            Folder folder = capacityService.findFolderById(String.valueOf(folderId));

            capacityService.checkFolderPermissions(folder, false);

            response.body.put("folder", folder);
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/folder/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create_folder(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object name =           body.get("name");
            Object image =          body.get("image");
            Object description =    body.get("description");
            Object rating =         body.get("rating");
            Object publicFolder =   body.get("publicFolder");

            // id or array of ids
            Object addCategoriesByIds =            body.get("addCategoriesByIds");
            Object addGrantedAccessGroupByIds =    body.get("addGrantedAccessGroupByIds");

            if(name == null || String.valueOf(name).isEmpty()) {
                throw new CapacityException("Parametro name não informado.");
            }

            Folder folder = new Folder();

            folder.setName(String.valueOf(name));
            folder.setAuthor(UserService.getInstance().getUserInSession().getProfile());

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
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/folder/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response edit_folder(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/folder/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete_folder(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/folder/content/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response folder_add_content(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/folder/content/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response folder_remove_content(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/folder/content/order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response folder_move_content(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

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

            capacityService.orderContentInFolder(folderId, contentId, toIndexInt);

            response.message = "Conteúdo ordenado na pasta com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/content/watch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response content_watch(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {
            Object contentId = body.get("contentId");

            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            Watch watch = capacityService.findWatchByContentId((String)contentId);

            response.body.put("watch", watch);
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/content/watch/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response content_watch_edit(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object contentId = body.get("contentId");
            Object status = body.get("watchStatus");

            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }
            if(status == null || String.valueOf(status).isEmpty()) {
                throw new CapacityException("Status do conteúdo não informado.");
            }

            WatchStatus watchStatus = WatchStatus.valueOf(String.valueOf(status));

            Watch watch = capacityService.setWatchStatus(String.valueOf(contentId), watchStatus);

            response.body.put("watch", watch);
            response.message = "Status do conteúdo atualizado com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

}
