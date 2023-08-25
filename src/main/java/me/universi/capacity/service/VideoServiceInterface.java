package me.universi.capacity.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.Video;
import me.universi.capacity.exceptions.VideoException;

public interface VideoServiceInterface {
    List<Video> getVideosByCategory(UUID categoryId) throws VideoException;
    Collection<Video> getVideosByPlaylist(UUID playlistId) throws VideoException;
}