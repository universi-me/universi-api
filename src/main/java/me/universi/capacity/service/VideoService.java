package me.universi.capacity.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.universi.capacity.entidades.Video;
import me.universi.capacity.exceptions.VideoException;
import me.universi.capacity.repository.VideosRepository;


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

    public boolean saveOrUpdateVideo(Video video) throws VideoException {
        boolean titleExists = videoRepo.existsByTitle(video.getTitle());
        boolean urlExists = videoRepo.existsByUrl(video.getUrl());
        if (titleExists) {
            throw new VideoException("Vídeo com título já existente.");
        }

        if (urlExists) {
            throw new VideoException("Vídeo com url já existente.");
        }

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

    public List<Video> getVideosByPlaylist(String playlist) {
        return videoRepo.findByPlaylist(playlist);
    } //Listar vídeo por categoria
}