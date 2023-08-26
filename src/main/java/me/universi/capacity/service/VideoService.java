package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.VideoCategory;
import me.universi.capacity.entidades.VideoPlaylist;
import me.universi.capacity.repository.VideoCategoryRepository;
import me.universi.capacity.repository.VideoPlaylistRepository;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import me.universi.capacity.entidades.Video;
import me.universi.capacity.exceptions.VideoException;
import me.universi.capacity.repository.VideoRepository;


@Service
public class VideoService implements VideoServiceInterface {
    
    private final VideoRepository videoRepo;

    private final VideoCategoryRepository videoCategoryRepository;

    private final VideoPlaylistRepository videoPlaylistRepository;

    public VideoService(VideoRepository videoRepo, VideoCategoryRepository videoCategoryRepository, VideoPlaylistRepository videoPlaylistRepository) {
        this.videoRepo = videoRepo;
        this.videoCategoryRepository = videoCategoryRepository;
        this.videoPlaylistRepository = videoPlaylistRepository;
    }


    public List<Video> getAllVideo(){ 
        List<Video> videoList = new ArrayList<>();
        videoRepo.findAll().forEach( video -> videoList.add(video));

        return videoList;
    } //Lista todos os vídeos existentes


    public Video findFirstById(UUID id){
        return videoRepo.findFirstById(id);
    } //Lista os vídeos pelo ID

    public Video findFirstById(String id){
        return videoRepo.findFirstById(UUID.fromString(id));
    }

    public boolean saveOrUpdateVideo(Video video) throws VideoException {

        Video updatedVideo = videoRepo.save(video);

        if (findFirstById(updatedVideo.getId()) != null){
            return true;
        }

        return false;
    } //Salvar o vídeo, ou se o ID já existir atualizar um vídeo

    public boolean deleteVideo(UUID id){
        videoRepo.deleteById(id);

        if (videoRepo.findById(id) != null){
            return true;
        }

        return false;
    } //Deleta o vídeo pelo ID

    public boolean deleteVideoCategory(UUID id){
        videoCategoryRepository.deleteById(id);

        if (videoCategoryRepository.findById(id) != null){
            return true;
        }

        return false;
    }

    public boolean deleteVideoPlaylist(UUID id){
        videoPlaylistRepository.deleteById(id);

        if (videoPlaylistRepository.findById(id) != null){
            return true;
        }

        return false;
    }

    public List<Video> getVideosByCategory(UUID categoryId) throws VideoException {
        VideoCategory videoCategory = videoCategoryRepository.findFirstById(categoryId);
        if(videoCategory == null) {
            throw new VideoException("Categoria não encontrada.");
        }
        return videoRepo.findByCategories(videoCategory);
    }

    public List<Video> getVideosByCategory(String categoryId) throws VideoException {
        return getVideosByCategory(UUID.fromString(categoryId));
    }

    public List<VideoCategory> getVideoAllCategory() {
        return videoCategoryRepository.findAll();
    }

    public Collection<Video> getVideosByPlaylist(UUID playlistId) throws VideoException {
        VideoPlaylist videoPlaylist = videoPlaylistRepository.findFirstById(playlistId);
        if(videoPlaylist == null) {
            throw new VideoException("Playlist não encontrada.");
        }
        return videoPlaylist.getVideos();
    }

    public Collection<Video> getVideosByPlaylist(String playlistId) throws VideoException {
        return getVideosByPlaylist(UUID.fromString(playlistId));
    }

    public boolean saveOrUpdateVideoCategory(VideoCategory category) throws VideoException {
        boolean titleExists = videoCategoryRepository.existsByName(category.getName());
        if (titleExists) {
            throw new VideoException("Categoria com título já existente.");
        }

        VideoCategory updatedVideoCategory = videoCategoryRepository.save(category);

        if (videoCategoryRepository.findFirstById(updatedVideoCategory.getId()) != null){
            return true;
        }

        return false;
    }

    public boolean saveOrUpdateVideoPlaylist(VideoPlaylist playlist) throws VideoException {

        VideoPlaylist updatedVideoCategory = videoPlaylistRepository.save(playlist);

        if (videoPlaylistRepository.findFirstById(updatedVideoCategory.getId()) != null){
            return true;
        }

        return false;
    }

    public List<VideoPlaylist> getVideoAllPlaylist() {
        return videoPlaylistRepository.findAll();
    }

    public VideoPlaylist findFirstPlaylistById(String playlistId) {
        return videoPlaylistRepository.findFirstById(UUID.fromString(playlistId));
    }

    public void addOrRemoveVideoFromPlaylist(Object playlistId, Object videoId, boolean isAdding) throws VideoException {
        if(playlistId == null) {
            throw new VideoException("Parametro playlistId é nulo.");
        }
        if(videoId == null) {
            throw new VideoException("Parametro videoId é nulo.");
        }

        Object videoIds = videoId;
        if(videoIds instanceof String) {
            videoIds = new ArrayList<String>() {{ add((String) videoId); }};
        }
        if(videoIds instanceof ArrayList) {
            for (String videoIdNow : (ArrayList<String>) videoIds) {
                if (videoIdNow == null || videoIdNow.isEmpty()) {
                    continue;
                }
                Video video = findFirstById(videoIdNow);
                if (video == null) {
                    throw new VideoException("Video não encontrado.");
                }
                addOrRemovePlaylistsFromVideo(video, playlistId, isAdding);
                boolean result = saveOrUpdateVideo(video);
                if (!result) {
                    throw new VideoException("Erro ao adicionar video a playlist.");
                }
            }
        }
    }

    public VideoCategory getCategoryById(UUID uuid) throws VideoException {
        VideoCategory videoCategory = videoCategoryRepository.findFirstById(uuid);
        if(videoCategory == null) {
            throw new VideoException("Categoria não encontrada.");
        }
        return videoCategory;
    }

    public VideoCategory getCategoryById(String uuid) throws VideoException {
        return getCategoryById(UUID.fromString(uuid));
    }

    public VideoPlaylist getPlaylistById(UUID uuid) throws VideoException {
        VideoPlaylist videoPlaylist = videoPlaylistRepository.findFirstById(uuid);
        if(videoPlaylist == null) {
            throw new VideoException("Playlist não encontrada.");
        }
        return videoPlaylist;
    }

    public VideoPlaylist getPlaylistById(String uuid) throws VideoException {
        return getPlaylistById(UUID.fromString(uuid));
    }

    public Collection<VideoPlaylist> getPlaylistByCategory(UUID videoCategory) throws VideoException {
        VideoCategory category = videoCategoryRepository.findFirstById(videoCategory);
        if(category == null) {
            throw new VideoException("Categoria não encontrada.");
        }
        return videoPlaylistRepository.findByCategories(category);
    }

    public Collection<VideoPlaylist> getPlaylistByCategory(String videoCategory) throws VideoException {
        return getPlaylistByCategory(UUID.fromString(videoCategory));
    }

    public void addOrRemoveCategoriesFromVideoOrPlaylist(Object videoOrPlaylist, Object categoriesId, boolean isAdding, boolean removeAllBefore) throws VideoException {
        Object categoriesIds = categoriesId;
        if(categoriesId instanceof String) {
            categoriesIds = new ArrayList<String>() {{ add((String) categoriesId); }};
        }
        if(categoriesIds instanceof ArrayList) {
            for(String categoryId : (ArrayList<String>) categoriesIds) {
                if(categoryId==null || categoryId.isEmpty()) {
                    continue;
                }
                VideoCategory category = getCategoryById(categoryId);
                if(category == null) {
                    throw new VideoException("Categoria não encontrada.");
                }
                if(videoOrPlaylist instanceof Video) {
                    if(((Video)videoOrPlaylist).getCategories() == null) {
                        ((Video)videoOrPlaylist).setCategories(new ArrayList<>());
                    }
                    if(removeAllBefore) {
                        ((Video)videoOrPlaylist).getCategories().clear();
                    }
                    if (isAdding) {
                        if (!((Video)videoOrPlaylist).getCategories().contains(category)) {
                            ((Video)videoOrPlaylist).getCategories().add(category);
                        }
                    } else {
                        ((Video)videoOrPlaylist).getCategories().remove(category);
                    }
                } else if(videoOrPlaylist instanceof VideoPlaylist) {
                    if(((VideoPlaylist)videoOrPlaylist).getCategories() == null) {
                        ((VideoPlaylist)videoOrPlaylist).setCategories(new ArrayList<>());
                    }
                    if(removeAllBefore) {
                        ((VideoPlaylist)videoOrPlaylist).getCategories().clear();
                    }
                    if (isAdding) {
                        if (!((VideoPlaylist)videoOrPlaylist).getCategories().contains(category)) {
                            ((VideoPlaylist)videoOrPlaylist).getCategories().add(category);
                        }
                    } else {
                        ((VideoPlaylist)videoOrPlaylist).getCategories().remove(category);
                    }
                }
            }
        }
    }

    public void addOrRemovePlaylistsFromVideo(Video video, Object playlistsId, boolean isAdding) throws VideoException {
        Object playlistsIds = playlistsId;
        if(playlistsIds instanceof String) {
            playlistsIds = new ArrayList<String>() {{ add((String) playlistsId); }};
        }
        if(playlistsIds instanceof ArrayList) {
            for(String playlistId : (ArrayList<String>)playlistsIds) {
                if(playlistId==null || playlistId.isEmpty()) {
                    continue;
                }
                VideoPlaylist playlist = getPlaylistById(playlistId);
                if(playlist == null) {
                    throw new VideoException("Playlist não encontrada.");
                }
                if(!UserService.getInstance().isSessionOfUser(playlist.getAuthor().getUser())) {
                    if(!UserService.getInstance().isUserAdmin(UserService.getInstance().getUserInSession())) {
                        throw new VideoException("Você não tem permissão para alterar essa playlist.");
                    }
                }
                if(playlist.getVideos() == null) {
                    playlist.setVideos(new ArrayList<>());
                }
                if(isAdding) {
                    if(!playlist.getVideos().contains(video)) {
                        playlist.getVideos().add(video);
                    }
                } else {
                    playlist.getVideos().remove(video);
                }
            }
        }
    }

}