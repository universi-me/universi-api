package me.universi.capacity.controllers;


import java.util.Map;
import java.util.UUID;

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
            if(categoryId == null) {
                throw new VideoException("ID da categoria não informado.");
            }

            response.body.put("videos", videoService.getVideosByCategory(UUID.fromString(String.valueOf(categoryId))));
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
            if(playlistId == null) {
                throw new VideoException("ID da categoria não informado.");
            }

            response.body.put("videos", videoService.getVideosByPlaylist(UUID.fromString(String.valueOf(playlistId))));
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
            if(videoId == null) {
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

            Object url = body.get("url");
            Object title = body.get("title");
            Object image = body.get("image");
            Object description = body.get("description");
            Object rating = body.get("rating");
            Object category = body.get("category");

            if(url == null) {
                throw new VideoException("URL do vídeo não informado.");
            }
            if(title == null) {
                throw new VideoException("Título do vídeo não informado.");
            }

            Video video = new Video();
            if(url != null) {
                video.setUrl(String.valueOf(url));
            }
            if(title != null) {
                video.setTitle(String.valueOf(title));
            }
            if(image != null) {
                video.setImage(String.valueOf(image));
            }
            if(description != null) {
                video.setDescription(String.valueOf(description));
            }
            if(rating != null) {
                video.setRating(Integer.parseInt(String.valueOf(rating)));
            }
            if(category != null) {
                video.setCategory(videoService.getCategoryById(UUID.fromString(String.valueOf(category))));
            }

            User user = UserService.getInstance().getUserInSession();
            Profile profile = user.getProfile();
            video.setAuthor(profile);

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
            if(videoId == null) {
                throw new VideoException("ID do vídeo não informado.");
            }

            Object url = body.get("url");
            Object title = body.get("title");
            Object image = body.get("image");
            Object description = body.get("description");
            Object rating = body.get("rating");
            Object category = body.get("category");

            Video video = videoService.findFirstById(String.valueOf(videoId));
            if(video == null) {
                throw new VideoException("Vídeo não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(video.getAuthor().getUser())) {
                throw new VideoException("Você não tem permissão para editar este vídeo.");
            }

            if(url != null) {
                video.setUrl(String.valueOf(url));
            }
            if(title != null) {
                video.setTitle(String.valueOf(title));
            }
            if(image != null) {
                video.setImage(String.valueOf(image));
            }
            if(description != null) {
                video.setDescription(String.valueOf(description));
            }
            if(rating != null) {
                video.setRating(Integer.parseInt(String.valueOf(rating)));
            }
            if(category != null) {
                video.setCategory(videoService.getCategoryById(UUID.fromString(String.valueOf(category))));
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
            if(videoId == null) {
                throw new VideoException("ID do vídeo não informado.");
            }

            Video video = videoService.findFirstById(String.valueOf(videoId));
            if(video == null) {
                throw new VideoException("Vídeo não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(video.getAuthor().getUser())) {
                throw new VideoException("Você não tem permissão para editar este vídeo.");
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
            if(categoryId == null) {
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

            if(name == null) {
                throw new VideoException("Parametro name não informado.");
            }

            VideoCategory category = new VideoCategory();
            if(name != null) {
                category.setName(String.valueOf(name));
            }
            if(image != null) {
                category.setImage(String.valueOf(image));
            }

            User user = UserService.getInstance().getUserInSession();
            Profile profile = user.getProfile();
            category.setAuthor(profile);

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
    public Response edit_categoty(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Object categoryId = body.get("id");
            if(categoryId == null) {
                throw new VideoException("ID da categoria não informado.");
            }

            Object name = body.get("name");
            Object image = body.get("image");

            VideoCategory videoCategory = videoService.getCategoryById(UUID.fromString(String.valueOf(categoryId)));
            if(videoCategory == null) {
                throw new VideoException("Categoria não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(videoCategory.getAuthor().getUser())) {
                throw new VideoException("Você não tem permissão para editar esta categoria.");
            }

            if(name != null) {
                videoCategory.setName(String.valueOf(name));
            }
            if(image != null) {
                videoCategory.setImage(String.valueOf(image));
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

            Object videoId = body.get("id");
            if(videoId == null) {
                throw new VideoException("ID da Categoria não informado.");
            }

            VideoCategory videoCategory = videoService.getCategoryById(UUID.fromString(String.valueOf(videoId)));
            if(videoCategory == null) {
                throw new VideoException("Categoria não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(videoCategory.getAuthor().getUser())) {
                throw new VideoException("Você não tem permissão para editar esta categoria.");
            }

            boolean result = videoService.deleteVideoCategory(UUID.fromString(String.valueOf(videoId)));
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

            Object categoryId = body.get("id");
            if(categoryId == null) {
                throw new VideoException("ID da playlist não informado.");
            }

            response.body.put("playlist", videoService.getPlaylistById(UUID.fromString(String.valueOf(categoryId))));
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

            Object name = body.get("name");
            Object image = body.get("image");
            Object description = body.get("description");
            Object rating = body.get("rating");
            Object category = body.get("category");

            if(name == null) {
                throw new VideoException("Parametro name não informado.");
            }

            VideoPlaylist playlist = new VideoPlaylist();

            playlist.setName(String.valueOf(name));

            if(image != null) {
                playlist.setImage(String.valueOf(image));
            }
            if(description != null) {
                playlist.setDescription(String.valueOf(description));
            }
            if(rating != null) {
                playlist.setRating(Integer.parseInt(String.valueOf(rating)));
            }
            if(category != null) {
                playlist.setCategory(videoService.getCategoryById(UUID.fromString(String.valueOf(category))));
            }

            User user = UserService.getInstance().getUserInSession();
            Profile profile = user.getProfile();
            playlist.setAuthor(profile);

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

            Object categoryId = body.get("id");
            if(categoryId == null) {
                throw new VideoException("ID da categoria não informado.");
            }

            Object name = body.get("name");
            Object image = body.get("image");
            Object description = body.get("description");
            Object rating = body.get("rating");
            Object category = body.get("category");

            VideoPlaylist videoPlaylist = videoService.getPlaylistById(UUID.fromString(String.valueOf(categoryId)));
            if(videoPlaylist == null) {
                throw new VideoException("Playlist não encontrado.");
            }

            if(!UserService.getInstance().isSessionOfUser(videoPlaylist.getAuthor().getUser())) {
                throw new VideoException("Você não tem permissão para editar esta playlist.");
            }

            if(name != null) {
                videoPlaylist.setName(String.valueOf(name));
            }
            if(image != null) {
                videoPlaylist.setImage(String.valueOf(image));
            }
            if(description != null) {
                videoPlaylist.setDescription(String.valueOf(description));
            }
            if(rating != null) {
                videoPlaylist.setRating(Integer.parseInt(String.valueOf(rating)));
            }
            if(category != null) {
                videoPlaylist.setCategory(videoService.getCategoryById(UUID.fromString(String.valueOf(category))));
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

            Object videoId = body.get("id");
            if(videoId == null) {
                throw new VideoException("ID da Playlist não informado.");
            }

            VideoPlaylist videoPlaylist = videoService.getPlaylistById(UUID.fromString(String.valueOf(videoId)));

            if(!UserService.getInstance().isSessionOfUser(videoPlaylist.getAuthor().getUser())) {
                throw new VideoException("Você não tem permissão para editar esta playlist.");
            }

            boolean result = videoService.deleteVideoPlaylist(UUID.fromString(String.valueOf(videoId)));
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
            Object videoId    = body.get("videoId");

            videoService.addOrRemoveVideoFromPlaylist(String.valueOf(playlistId), String.valueOf(videoId), true);

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
            Object videoId    = body.get("videoId");

            videoService.addOrRemoveVideoFromPlaylist(String.valueOf(playlistId), String.valueOf(videoId), false);

            response.message = "Video removido da playlist com sucesso.";
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }
        return response;
    }

}
