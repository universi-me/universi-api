package me.universi.capacity.controllers;

import java.util.*;

import me.universi.capacity.entidades.Content;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.universi.api.entities.Response;
import me.universi.capacity.dto.WatchProfileProgressDTO;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.service.ContentService;
import me.universi.capacity.service.FolderService;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
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
            Object folderReference = body.get("reference");
            if((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) {
                throw new CapacityException("ID e referência da pasta não informados.");
            }

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);
            response.body.put("contents", contentService.findByFolder(folder.getId()));
        });
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            Object folderReference = body.get("reference");

            if((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) {
                throw new CapacityException("ID e referência da pasta não informados.");
            }

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);

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
                throw new CapacityException("Parametro name não informados.");
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

            folder.setReference(folderService.generateAvailableReference());

            boolean result = folderService.saveOrUpdate(folder);
            if(!result) {
                throw new CapacityException("Erro ao salvar o pasta.");
            }

            response.body.put("contentId", folder.getId());

            response.message = "Pasta criada com sucesso.";
        });
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            Object folderReference = body.get("reference");

            if((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) {
                throw new CapacityException("ID e referência da pasta não informados.");
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

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);

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
                folder.setDescription(descriptionStr);
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
            Object folderReference = body.get("reference");

            if((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) {
                throw new CapacityException("ID nem referência da pasta não informados.");
            }

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);

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
            Object folderReference = body.get("reference");

            // id or array of ids
            Object contentIds    = body.get("contentIds");

            if((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) {
                throw new CapacityException("ID e referência da pasta não informados.");
            }
            if(contentIds == null) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);
            folderService.addOrRemoveContent(folder.getId().toString(), contentIds, true);

            response.message = "Conteúdo adicionado a pasta com sucesso.";
        });
    }

    @PostMapping(value = "/content/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response removeContent(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object folderId = body.get("id");
            Object folderReference = body.get("reference");

            // id or array of ids
            Object contentIds    = body.get("contentIds");

            if((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) {
                throw new CapacityException("ID e referência da pasta não informados.");
            }
            if(contentIds == null) {
                throw new CapacityException("ID do conteúdo não informado.");
            }

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);
            folderService.addOrRemoveContent(folder.getId().toString(), contentIds, false);

            response.message = "Conteúdo removido da pasta com sucesso.";
        });
    }

    @PostMapping(value = "/content/position", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response moveContent(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Object folderId = body.get("folderId");
            Object folderReference = body.get("reference");
            Object contentId = body.get("contentId");
            Object toIndex = body.get("toIndex");

            if((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) {
                throw new CapacityException("ID e referência da pasta não informados.");
            }
            if(contentId == null || String.valueOf(contentId).isEmpty()) {
                throw new CapacityException("ID do conteúdo não informado.");
            }
            if(toIndex == null || String.valueOf(toIndex).isEmpty()) {
                throw new CapacityException("Índice não informado.");
            }

            int toIndexInt = Integer.parseInt(String.valueOf(toIndex));

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);
            folderService.setNewPositionOfContent(folder.getId().toString(), contentId, toIndexInt);

            response.message = "Conteúdo ordenado na pasta com sucesso.";
        });
    }

    @PostMapping(value = "/assign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response assign(@RequestBody Map<String, Object> body){
        return Response.buildResponse( response -> {
            Object profilesIds = body.get("profilesIds");
            Object folderId = body.get("folderId");
            Object folderReference = body.get("reference");

            if (profilesIds == null || String.valueOf(profilesIds).isEmpty())
                throw new CapacityException("profilesIds é inválido.");
            if((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) {
                throw new CapacityException("ID e referência da pasta não informados.");
            }

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);
            if (folder == null)
                throw new CapacityException("folderId é inválido");

            if (profilesIds instanceof Collection) {
                folderService.assignToMultipleProfiles((Collection<String>) profilesIds, folder);
            } else {
                folderService.assignToProfile(UUID.fromString(String.valueOf(profilesIds)) , folder);
            }
        });
    }

    @PostMapping(value = "/assigned", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response usersAssigned(@RequestBody Map<String, Object> body){
        return Response.buildResponse(response -> {

            Object folderId = body.get("folderId");
            Object folderReference = body.get("reference");

            if(((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty())) || folderService.findByIdOrReference(folderId, folderReference) == null)
                throw new CapacityException("folderId e reference são inválido.");

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);
            response.body.put("profilesIds", folderService.findAssignedProfiles(folder.getId()));
        });
    }

    @PostMapping(value = "/unassign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response unassign(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Object folderId = body.get("folderId");
            Object folderReference = body.get("reference");
            Object profilesIds = body.get("profilesIds");

            if ((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty()))
                throw new CapacityException("folderId e reference são inválidos.");

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);

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

    @PostMapping(value = "/assigned-by", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response assignedBy(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Object profileId = body.get("profileId");
            Object profileUsername = body.get("username");

            response.body.put("folders", folderService.getAssignedBy(profileId, profileUsername));
        });
    }

    @PostMapping(value = "/favorite", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response favorite(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Object folderId = body.get("folderId");
            Object folderReference = body.get("reference");

            if ((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty()))
                throw new CapacityException("folderId e reference são inválidos.");

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);
            folderService.favorite(folder);

            response.message = "Conteúdo favoritado com sucesso!";
        });
    }

    @PostMapping(value = "/unfavorite", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response unfavorite(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Object folderId = body.get("folderId");
            Object folderReference = body.get("reference");

            if ((folderId == null || String.valueOf(folderId).isEmpty()) && (folderReference == null || String.valueOf(folderReference).isEmpty()))
                throw new CapacityException("folderId é inválido.");

            Folder folder = folderService.findByIdOrReference(folderId, folderReference);
            folderService.unfavorite(folder);

            response.message = "Conteúdo desfavoritado com sucesso!";
        });
    }

    @PostMapping(value = "/watch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response watch(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Profile profile = ProfileService.getInstance().getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));
            Folder folder = folderService.findByIdOrReference(body.get("folderId"), body.get("folderReference"));

            if (!folderService.canCheckProfileProgress(profile, folder)) {
                response.status = 403;
                throw new CapacityException("Você não pode checar o progresso desse usuário para esse conteúdo");
            }

            List<WatchProfileProgressDTO> contentWatches = folder.getContents().stream()
                .map(c -> new WatchProfileProgressDTO(profile, c))
                .toList();

            response.body.put("watching", profile);
            response.body.put("folder", folder);
            response.body.put("contentWatches", contentWatches);
        });
    }

    @PostMapping(value = "/duplicate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response duplicate(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
           Object folderId = body.get("contentId");
           Object targetGroupId = body.get("targetGroupId");
           Object targetGroupPath = body.get("targetGroupPath");

           if(folderId == null)
               throw new CapacityException("O id do conteúdo informado é inválido");

           if(targetGroupId == null && targetGroupPath == null)
               throw new CapacityException("O id e caminho do grupo alvo informado é inválido");

            Folder originalfolder = folderService.findById(UUID.fromString(String.valueOf(folderId)));
            Group targetGroup = groupService.getGroupByGroupIdOrGroupPath(targetGroupId, targetGroupPath);

            User user = UserService.getInstance().getUserInSession();

            groupService.verifyPermissionToEditGroup(targetGroup, user);

            Folder folder = new Folder();

            folder.setName("Cópia de "+originalfolder.getName());
            folder.setAuthor(user.getProfile());
            folder.setOwnerGroup(targetGroup);
            folder.setImage(originalfolder.getImage());
            folder.setDescription(originalfolder.getDescription());
            folder.setPublicFolder(originalfolder.isPublicFolder());
            folder.setReference(folderService.generateAvailableReference());
            folder.setRating(0);

            boolean saveResult = folderService.saveOrUpdate(folder);
            if(!saveResult)
                throw new CapacityException("Houve um erro ao salvar a cópia do conteúdo");

            if(originalfolder.getContents() != null && !originalfolder.getContents().isEmpty())
                for(Content c : originalfolder.getContents()){
                    if(folder.getContents() == null)
                        folder.setContents(new ArrayList<>());
                    folder.getContents().add(c);
                    c.getFolders().add(folder);
                    contentService.saveOrUpdate(c);
                    folderService.saveOrUpdate(folder);
                }

           response.message = "Conteúdo salvo com sucesso!";
        });
    }
}
