package me.universi.capacitacao.service;

import java.util.List;
import me.universi.capacitacao.entidades.Video;

public interface VideoServiceInterface {
    List<Video> getVideosByCategory(String category);
    List<Video> getVideosByPlaylist(String playlist);
}