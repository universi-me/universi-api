package me.universi.capacity.service;

import java.util.List;

import me.universi.capacity.entidades.Video;

public interface VideoServiceInterface {
    List<Video> getVideosByCategory(String category);
    List<Video> getVideosByPlaylist(String playlist);
}