package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.capacity.entidades.Video;

public interface VideosRepository extends JpaRepository<Video, UUID> {
    Video findFirstById(UUID id);
    List<Video> findByCategory(String category);
    List<Video> getVideosByCategory(String category);
    List<Video> findByPlaylist(String playlist);
    List<Video> getVideosByPlaylist(String playlist);
    boolean existsByTitle(String title);
    boolean existsByUrl(String url);
}