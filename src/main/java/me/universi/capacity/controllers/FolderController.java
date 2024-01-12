package me.universi.capacity.controllers;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.universi.api.entities.Response;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.service.ContentService;
import me.universi.capacity.service.FolderService;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;

@RestController
@RequestMapping("/api/capacity/folder")
public class FolderController {
    private final GroupService groupService;
    private final ContentService contentService;
    private final FolderService folderService;

    public FolderController(GroupService groupService, ContentService contentService, FolderService folderService) {
        this.groupService = groupService;
        this.contentService = contentService;
        this.folderService = folderService;
    }

    @GetMapping("/all")
    public Response list() {
        return Response.buildResponse(response -> {
            response.body.put("folders", folderService.findAll());
        });
    }

    @PostMapping(value = "/contents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response contentsByFolder(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            response.body.put("contents", contentService.findByFolder(String.valueOf(folderId)));
        });
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            Folder folder = folderService.findById(String.valueOf(folderId));

            folderService.checkPermissions(folder, false);

            response.body.put("folder", folder);
        });
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response create(@RequestBody Map<String, Object> body) {
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
                folderService.addOrRemoveCategoriesFromContentOrFolder(folder, addCategoriesByIds, true, false);
            }

            if(addGrantedAccessGroupByIds != null) {
                folderService.addOrRemoveGrantedAccessGroup(folder, addGrantedAccessGroupByIds, true);
            }



            boolean result = folderService.saveOrUpdate(folder);
            if(!result) {
                throw new CapacityException("Erro ao salvar o pasta.");
            }

            response.message = "Pasta criada com sucesso.";
        });
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response edit(@RequestBody Map<String, Object> body) {
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

            Folder folder = folderService.findById(String.valueOf(folderId));

            folderService.checkPermissions(folder, true);

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
                folderService.addOrRemoveCategoriesFromContentOrFolder(folder, addCategoriesByIds, true, false);
            }
            if(removeCategoriesByIds != null) {
                folderService.addOrRemoveCategoriesFromContentOrFolder(folder, removeCategoriesByIds, false, false);
            }

            if(addGrantedAccessGroupByIds != null) {
                folderService.addOrRemoveGrantedAccessGroup(folder, addGrantedAccessGroupByIds, true);
            }
            if(removeGrantedAccessGroupByIds != null) {
                folderService.addOrRemoveGrantedAccessGroup(folder, removeGrantedAccessGroupByIds, false);
            }

            boolean result = folderService.saveOrUpdate(folder);
            if(!result) {
                throw new CapacityException("Erro ao editar o pasta.");
            }

            response.message = "Pasta atualizada com sucesso.";
        });
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response delete(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            if(folderId == null || String.valueOf(folderId).isEmpty()) {
                throw new CapacityException("ID da pasta não informado.");
            }

            Folder folder = folderService.findById(UUID.fromString(String.valueOf(folderId)));

            folderService.checkPermissions(folder, true);

            boolean result = folderService.delete(UUID.fromString(String.valueOf(folderId)));
            if(!result) {
                throw new CapacityException("Erro ao deletar pasta.");
            }

            response.message = "Pasta deletada com sucesso.";
        });
    }

    @PostMapping(value = "/content/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response addContent(@RequestBody Map<String, Object> body) {
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

            folderService.addOrRemoveContent(folderId, contentIds, true);

            response.message = "Conteúdo adicionado a pasta com sucesso.";
        });
    }

    @PostMapping(value = "/content/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response removeContent(@RequestBody Map<String, Object> body) {
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

            folderService.addOrRemoveContent(folderId, contentIds, false);

            response.message = "Conteúdo removido da pasta com sucesso.";
        });
    }

    @PostMapping(value = "/content/position", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response moveContent(@RequestBody Map<String, Object> body) {
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

            folderService.setNewPositionOfContent(folderId, contentId, toIndexInt);

            response.message = "Conteúdo ordenado na pasta com sucesso.";
        });
    }

    @PostMapping(value = "/assign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response assign(@RequestBody Map<String, Object> body){
        return Response.buildResponse( response -> {
            Object profilesIds = body.get("profilesIds");
            Object folderId = body.get("folderId");

            if (profilesIds == null || String.valueOf(profilesIds).isEmpty())
                throw new CapacityException("profilesIds é inválido.");
            if (folderId == null || String.valueOf(folderId).isEmpty())
                throw new CapacityException("folderId é inválido.");

            Folder folder = folderService.findById(String.valueOf(folderId));
            if (folder == null)
                throw new CapacityException("folderId é inválido");

            if (profilesIds instanceof Collection) {
                folderService.assignToMultipleProfiles((Collection<String>) profilesIds, folder);
            } else {
                folderService.assignToProfile(UUID.fromString(String.valueOf(profilesIds)) , folder);
            }
        });
    }

    @GetMapping(value = "/assigned", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response usersAssigned(@RequestBody Map<String, Object> body){
        return Response.buildResponse(response -> {

            Object folderId = body.get("folderId");

            if(folderId == null || String.valueOf(folderId).isEmpty() || folderService.findById((UUID.fromString(String.valueOf(folderId)))) == null)
                throw new CapacityException("folderId é inválido.");

           response.body.put("profilesIds", folderService.findAssignedProfiles((UUID.fromString(String.valueOf(folderId)))));
        });
    }

    @PostMapping(value = "/unassign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response unassign(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Object folderId = body.get("folderId");
            Object profilesIds = body.get("profilesIds");

            if (folderId == null || String.valueOf(folderId).isEmpty())
                throw new CapacityException("folderId é inválido.");

            Folder folder = folderService.findById(UUID.fromString(String.valueOf(folderId)));
            if (folder == null)
                throw new CapacityException("folderId é inválido.");

            boolean nullProfile = profilesIds == null;

            String stringProfile = profilesIds instanceof String
                ? (String)profilesIds
                : null;

            Collection<?> collectionProfiles = profilesIds instanceof Collection
                ? (Collection<?>) profilesIds
                : null;

            if (nullProfile || (stringProfile != null && stringProfile.isEmpty()) || (collectionProfiles != null && collectionProfiles.isEmpty()))
                throw new CapacityException("profilesIds é inválido.");

            if (stringProfile != null) {
                folderService.unassignFromProfile(UUID.fromString(stringProfile), folder);
            } else if (collectionProfiles != null) {
                folderService.unassignFromMultipleProfiles(
                    collectionProfiles.stream().map(p -> UUID.fromString(String.valueOf(p))).toList(),
                    folder
                );
            }
        });
    }
}
