package me.universi.capacity.controllers;


import java.util.Map;
import java.util.UUID;

import me.universi.Sys;
import me.universi.api.entities.Response;
import me.universi.capacity.entidades.VideoCategory;
import me.universi.capacity.entidades.VideoPlaylist;
import me.universi.profile.entities.Profile;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import me.universi.capacity.entidades.Video;
import me.universi.capacity.exceptions.VideoException;
import me.universi.capacity.service.VideoService;


@RestController
@RequestMapping("/api/capacity")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/videos")
    public Response videoList() {
        Response response = new Response(); // default
        try {

            response.body.put("videos", videoService.getAllVideo());
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

            response.body.put("categories", videoService.getVideoAllCategory());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @GetMapping("/playlists")
    public Response playlistList() {
        Response response = new Response(); // default
        try {

            response.body.put("playlists", videoService.getVideoAllPlaylist());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/category/videos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_video_by_category(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new VideoException("ID da categoria não informado.");
            }

            response.body.put("videos", videoService.getVideosByCategory(String.valueOf(categoryId)));
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/category/playlists", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_playlist_by_category(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object categoryId = body.get("id");
            if(categoryId == null || String.valueOf(categoryId).isEmpty()) {
                throw new VideoException("ID da categoria não informado.");
            }

            response.body.put("playlists", videoService.getPlaylistByCategory(String.valueOf(categoryId)));
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/playlist/videos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response list_video_by_playlist(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object playlistId = body.get("id");
            if(playlistId == null || String.valueOf(playlistId).isEmpty()) {
                throw new VideoException("ID da categoria não informado.");
            }

            response.body.put("videos", videoService.getVideosByPlaylist(String.valueOf(playlistId)));
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/video/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get_video(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object videoId = body.get("id");
            if(videoId == null || String.valueOf(videoId).isEmpty()) {
                throw new VideoException("ID do vídeo não informado.");
            }

            Video video = videoService.findFirstById(String.valueOf(videoId));
            if(video == null) {
                throw new VideoException("Vídeo não encontrado.");
            }

            response.body.put("video", video);
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/video/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create_video(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object url =         body.get("url");
            Object title =       body.get("title");
            Object image =       body.get("image");
            Object description = body.get("description");
            Object rating =      body.get("rating");

            // id or array of ids
            Object addCategoriesByIds =    body.get("addCategoriesByIds");
            Object addPlaylistsByIds =     body.get("addPlaylistsByIds");

            if(url == null || String.valueOf(url).isEmpty()) {
                throw new VideoException("URL do vídeo não informado.");
            }
            if(title == null || String.valueOf(title).isEmpty()) {
                throw new VideoException("Título do vídeo não informado.");
            }

            Video video = new Video();
            video.setUrl(String.valueOf(url));
            video.setTitle(String.valueOf(title));

            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    video.setImage(imageStr);
                }
            }
            if(description != null) {
                String descriptionStr = String.valueOf(description);
                if(!descriptionStr.isEmpty()) {
                    video.setDescription(descriptionStr);
                }
            }
            if(rating != null) {
                String ratingStr = String.valueOf(rating);
                if(!ratingStr.isEmpty()) {
                    video.setRating(Integer.parseInt(ratingStr));
                }
            }

            if(addCategoriesByIds != null) {
                videoService.addOrRemoveCategoriesFromVideoOrPlaylist(video, addCategoriesByIds, true, false);
            }

            if(addPlaylistsByIds != null) {
                videoService.addOrRemovePlaylistsFromVideo(video, addPlaylistsByIds, true);
            }

            video.setAuthor(UserService.getInstance().getUserInSession().getProfile());

            boolean result = videoService.saveOrUpdateVideo(video);
            if(!result) {
                throw new VideoException("Erro ao salvar o vídeo.");
            }

            response.message = "Vídeo criado com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/video/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response edit_video(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object videoId = body.get("id");
            if(videoId == null || String.valueOf(videoId).isEmpty()) {
                throw new VideoException("ID do vídeo não informado.");
            }

            Object url =         body.get("url");
            Object title =       body.get("title");
            Object image =       body.get("image");
            Object description = body.get("description");
            Object rating =      body.get("rating");

            // id or array of ids
            Object addCategoriesByIds =    body.get("addCategoriesByIds");
            Object removeCategoriesByIds = body.get("removeCategoriesByIds");
            Object addPlaylistsByIds =     body.get("addPlaylistsByIds");
            Object removePlaylistsByIds =  body.get("removePlaylistsByIds");

            Video video = videoService.findFirstById(String.valueOf(videoId));
            if(video == null) {
                throw new VideoException("Vídeo não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(video.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new VideoException("Você não tem permissão para editar este vídeo.");
                }
            }

            if(url != null) {
                String urlStr = String.valueOf(url);
                if(!urlStr.isEmpty()) {
                    video.setUrl(urlStr);
                }
            }
            if(title != null) {
                String titleStr = String.valueOf(title);
                if(!titleStr.isEmpty()) {
                    video.setTitle(titleStr);
                }
            }
            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    video.setImage(imageStr);
                }
            }
            if(description != null) {
                String descriptionStr = String.valueOf(description);
                if(!descriptionStr.isEmpty()) {
                    video.setDescription(descriptionStr);
                }
            }
            if(rating != null) {
                String ratingStr = String.valueOf(rating);
                if(!ratingStr.isEmpty()) {
                    video.setRating(Integer.parseInt(ratingStr));
                }
            }

            if(addCategoriesByIds != null) {
                videoService.addOrRemoveCategoriesFromVideoOrPlaylist(video, addCategoriesByIds, true, false);
            }
            if(removeCategoriesByIds != null) {
                videoService.addOrRemoveCategoriesFromVideoOrPlaylist(video, removeCategoriesByIds, false, false);
            }

            if(addPlaylistsByIds != null) {
                videoService.addOrRemovePlaylistsFromVideo(video, addPlaylistsByIds, true);
            }
            if(removePlaylistsByIds != null) {
                videoService.addOrRemovePlaylistsFromVideo(video, removePlaylistsByIds, false);
            }

            boolean result = videoService.saveOrUpdateVideo(video);
            if(!result) {
                throw new VideoException("Erro ao salvar o vídeo.");
            }

            response.message = "Vídeo atualizado com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/video/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete_video(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object videoId = body.get("id");
            if(videoId == null || String.valueOf(videoId).isEmpty()) {
                throw new VideoException("ID do vídeo não informado.");
            }

            Video video = videoService.findFirstById(String.valueOf(videoId));
            if(video == null) {
                throw new VideoException("Vídeo não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(video.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new VideoException("Você não tem permissão para apagar este vídeo.");
                }
            }

            boolean result = videoService.deleteVideo(UUID.fromString(String.valueOf(videoId)));
            if(!result) {
                throw new VideoException("Erro ao deletar o vídeo.");
            }

            response.message = "Vídeo deletado com sucesso.";
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
                throw new VideoException("ID da categoria não informado.");
            }

            response.body.put("category", videoService.getCategoryById(UUID.fromString(String.valueOf(categoryId))));
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
                throw new VideoException("Parametro name não informado.");
            }

            VideoCategory category = new VideoCategory();
            category.setName(String.valueOf(name));

            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    category.setImage(imageStr);
                }
            }

            category.setAuthor(UserService.getInstance().getUserInSession().getProfile());

            boolean result = videoService.saveOrUpdateVideoCategory(category);
            if(!result) {
                throw new VideoException("Erro ao salvar o categoria.");
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
                throw new VideoException("ID da categoria não informado.");
            }

            Object name =  body.get("name");
            Object image = body.get("image");

            VideoCategory videoCategory = videoService.getCategoryById(String.valueOf(categoryId));
            if(videoCategory == null) {
                throw new VideoException("Categoria não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(videoCategory.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new VideoException("Você não tem permissão para editar esta categoria.");
                }
            }

            if(name != null) {
                String nameStr = String.valueOf(name);
                if(!nameStr.isEmpty()) {
                    videoCategory.setName(nameStr);
                }
            }
            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    videoCategory.setImage(imageStr);
                }
            }

            boolean result = videoService.saveOrUpdateVideoCategory(videoCategory);
            if(!result) {
                throw new VideoException("Erro ao editar o categoria.");
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
                throw new VideoException("ID da Categoria não informado.");
            }

            VideoCategory videoCategory = videoService.getCategoryById(UUID.fromString(String.valueOf(categoryId)));
            if(videoCategory == null) {
                throw new VideoException("Categoria não encontrada.");
            }

            if(!UserService.getInstance().isSessionOfUser(videoCategory.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new VideoException("Você não tem permissão para editar esta categoria.");
                }
            }

            boolean result = videoService.deleteVideoCategory(UUID.fromString(String.valueOf(categoryId)));
            if(!result) {
                throw new VideoException("Erro ao deletar Categoria.");
            }

            response.message = "Categoria deletada com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/playlist/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get_playlist(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object playlistId = body.get("id");
            if(playlistId == null || String.valueOf(playlistId).isEmpty()) {
                throw new VideoException("ID da playlist não informado.");
            }

            response.body.put("playlist", videoService.getPlaylistById(String.valueOf(playlistId)));
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/playlist/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create_playlist(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object name =        body.get("name");
            Object image =       body.get("image");
            Object description = body.get("description");
            Object rating =      body.get("rating");

            // id or array of ids
            Object addCategoriesByIds =    body.get("addCategoriesByIds");

            if(name == null || String.valueOf(name).isEmpty()) {
                throw new VideoException("Parametro name não informado.");
            }

            VideoPlaylist playlist = new VideoPlaylist();

            playlist.setName(String.valueOf(name));

            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    playlist.setImage(imageStr);
                }
            }
            if(description != null) {
                String descriptionStr = String.valueOf(description);
                if(!descriptionStr.isEmpty()) {
                    playlist.setDescription(descriptionStr);
                }
            }
            if(rating != null) {
                String ratingStr = String.valueOf(rating);
                if(!ratingStr.isEmpty()) {
                    playlist.setRating(Integer.parseInt(ratingStr));
                }
            }

            if(addCategoriesByIds != null) {
                videoService.addOrRemoveCategoriesFromVideoOrPlaylist(playlist, addCategoriesByIds, true, false);
            }

            playlist.setAuthor(UserService.getInstance().getUserInSession().getProfile());

            boolean result = videoService.saveOrUpdateVideoPlaylist(playlist);
            if(!result) {
                throw new VideoException("Erro ao salvar o playlist.");
            }

            response.message = "Playlist criada com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/playlist/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response edit_playlist(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object playlistId = body.get("id");
            if(playlistId == null || String.valueOf(playlistId).isEmpty()) {
                throw new VideoException("ID da playlist não informado.");
            }

            Object name =        body.get("name");
            Object image =       body.get("image");
            Object description = body.get("description");
            Object rating =      body.get("rating");

            // id or array of ids
            Object addCategoriesByIds =    body.get("addCategoriesByIds");
            Object removeCategoriesByIds = body.get("removeCategoriesByIds");

            VideoPlaylist videoPlaylist = videoService.getPlaylistById(UUID.fromString(String.valueOf(playlistId)));
            if(videoPlaylist == null) {
                throw new VideoException("Playlist não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(videoPlaylist.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new VideoException("Você não tem permissão para editar esta playlist.");
                }
            }

            if(name != null) {
                String nameStr = String.valueOf(name);
                if(!nameStr.isEmpty()) {
                    videoPlaylist.setName(nameStr);
                }
            }
            if(image != null) {
                String imageStr = String.valueOf(image);
                if(!imageStr.isEmpty()) {
                    videoPlaylist.setImage(imageStr);
                }
            }
            if(description != null) {
                String descriptionStr = String.valueOf(description);
                if(!descriptionStr.isEmpty()) {
                    videoPlaylist.setDescription(descriptionStr);
                }
            }
            if(rating != null) {
                String ratingStr = String.valueOf(rating);
                if(!ratingStr.isEmpty()) {
                    videoPlaylist.setRating(Integer.parseInt(ratingStr));
                }
            }

            if(addCategoriesByIds != null) {
                videoService.addOrRemoveCategoriesFromVideoOrPlaylist(videoPlaylist, addCategoriesByIds, true, false);
            }
            if(removeCategoriesByIds != null) {
                videoService.addOrRemoveCategoriesFromVideoOrPlaylist(videoPlaylist, removeCategoriesByIds, false, false);
            }

            boolean result = videoService.saveOrUpdateVideoPlaylist(videoPlaylist);
            if(!result) {
                throw new VideoException("Erro ao editar o playlist.");
            }

            response.message = "Playlist atualizada com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/playlist/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delete_playlist(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object playlistId = body.get("id");
            if(playlistId == null || String.valueOf(playlistId).isEmpty()) {
                throw new VideoException("ID da Playlist não informado.");
            }

            VideoPlaylist videoPlaylist = videoService.getPlaylistById(UUID.fromString(String.valueOf(playlistId)));

            if(!UserService.getInstance().isSessionOfUser(videoPlaylist.getAuthor().getUser())) {
                if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                    throw new VideoException("Você não tem permissão para apagar esta playlist.");
                }
            }

            boolean result = videoService.deleteVideoPlaylist(UUID.fromString(String.valueOf(playlistId)));
            if(!result) {
                throw new VideoException("Erro ao deletar Playlist.");
            }

            response.message = "Playlist deletada com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/playlist/video/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response playlist_add_video(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object playlistId = body.get("id");

            // id or array of ids
            Object videoIds    = body.get("videoIds");

            if(playlistId == null || String.valueOf(playlistId).isEmpty()) {
                throw new VideoException("ID da Playlist não informado.");
            }
            if(videoIds == null) {
                throw new VideoException("ID do Video não informado.");
            }

            videoService.addOrRemoveVideoFromPlaylist(playlistId, videoIds, true);

            response.message = "Video adicionado a playlist com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

    @PostMapping(value = "/playlist/video/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response playlist_remove_video(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object playlistId = body.get("id");

            // id or array of ids
            Object videoIds    = body.get("videoIds");

            if(playlistId == null || String.valueOf(playlistId).isEmpty()) {
                throw new VideoException("ID da Playlist não informado.");
            }
            if(videoIds == null) {
                throw new VideoException("ID do Video não informado.");
            }

            videoService.addOrRemoveVideoFromPlaylist(playlistId, videoIds, false);

            response.message = "Video removido da playlist com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

}
