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
public class VideoService implements VideoServiceInterface{
    
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
        return videoRepo.findByCategory(videoCategory);
    } //Listar vídeo por categoria

    public List<VideoCategory> getVideoAllCategory() {
        return videoCategoryRepository.findAll();
    }

    public Collection<Video> getVideosByPlaylist(UUID playlistId) throws VideoException {
        VideoPlaylist videoPlaylist = videoPlaylistRepository.findFirstById(playlistId);
        if(videoPlaylist == null) {
            throw new VideoException("Playlist não encontrada.");
        }
        return videoPlaylist.getVideos();
    } //Listar vídeo por categoria

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

    public void addOrRemoveVideoFromPlaylist(String playlistId, String videoId, boolean isAdding) throws VideoException {
        if(playlistId == null) {
            throw new VideoException("Parametro playlistId é nulo.");
        }
        if(videoId == null) {
            throw new VideoException("Parametro videoId é nulo.");
        }

        VideoPlaylist playlist = findFirstPlaylistById(playlistId);
        if(playlist == null) {
            throw new VideoException("Playlist não encontrada.");
        }

        Video video = findFirstById(videoId);
        if(video == null) {
            throw new VideoException("Video não encontrado.");
        }

        if(!UserService.getInstance().isSessionOfUser(playlist.getAuthor().getUser())) {
            throw new VideoException("Você não tem permissão para alterar essa playlist.");
        }

        if(isAdding) {
            playlist.getVideos().remove(video);
            playlist.getVideos().add(video);
        } else {
            playlist.getVideos().remove(video);
        }

        boolean result = saveOrUpdateVideo(video);
        if(!result) {
            throw new VideoException("Erro ao adicionar video a playlist.");
        }
    }

    public VideoCategory getCategoryById(UUID uuid) throws VideoException {
        VideoCategory videoCategory = videoCategoryRepository.findFirstById(uuid);
        if(videoCategory == null) {
            throw new VideoException("Categoria não encontrada.");
        }
        return videoCategory;
    }

    public VideoPlaylist getPlaylistById(UUID uuid) throws VideoException {
        VideoPlaylist videoPlaylist = videoPlaylistRepository.findFirstById(uuid);
        if(videoPlaylist == null) {
            throw new VideoException("Playlist não encontrada.");
        }
        return videoPlaylist;
    }
}