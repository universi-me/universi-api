package me.universi.capacity.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.universi.capacity.entidades.Video;

public interface VideosRepository extends JpaRepository<Video, Long> {
    List<Video> findByCategory(String category);
    List<Video> getVideosByCategory(String category);
    List<Video> findByPlaylist(String playlist);
    List<Video> getVideosByPlaylist(String playlist);
}