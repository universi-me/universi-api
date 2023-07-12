package me.universi.capacitacao.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.universi.capacitacao.entidades.Video;
import me.universi.capacitacao.repository.VideosRepository;


@Service
public class VideoService implements VideoServiceInterface{
    
    @Autowired
    VideosRepository videoRepo;

    public List<Video> getAllVideo(){ 
        List<Video> videoList = new ArrayList<>();
        videoRepo.findAll().forEach( video -> videoList.add(video));

        return videoList;
    } //Lista todos os vídeos existentes


    public Video getVideoById(Long id){
        return videoRepo.findById(id).get();
    } //Lista os vídeos pelo ID

    public boolean saveOrUpdateVideo(Video video) {
        Video updatedVideo = videoRepo.save(video);

        if (videoRepo.findById(updatedVideo.getId()) != null){
            return true;
        }

        return false;
    } //Salvar o vídeo, ou se o ID já existir atualizar um vídeo

    public boolean deleteVideo(Long id){
        videoRepo.deleteById(id);

        if (videoRepo.findById(id) != null){
            return true;
        }

        return false;
    } //Deleta o vídeo pelo ID

    public List<Video> getVideosByCategory(String category) {
        return videoRepo.findByCategory(category);
    } //Listar vídeo por categoria

    public List<Video> getVideosDestaqueByCategory(String category) {
        List<Video> videos = videoRepo.getVideosByCategory(category);
        List<Video> videosDestaque = new ArrayList<>();
        Collections.shuffle(videos); // Embaralhar a lista de vídeos
        for (Video video : videos) {
            if (video.getRating() == 5 && videosDestaque.size() < 10) {
                videosDestaque.add(video);
            }
        }
        return videosDestaque;
    }

    public List<Video> getVideosByPlaylist(String playlist) {
        return videoRepo.findByPlaylist(playlist);
    } //Listar vídeo por categoria
}