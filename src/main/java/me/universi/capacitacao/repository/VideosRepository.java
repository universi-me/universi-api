package me.universi.capacitacao.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import me.universi.capacitacao.entidades.Video;

public interface VideosRepository extends JpaRepository<Video, Long> {
    List<Video> findByCategory(String category);
    List<Video> getVideosByCategory(String category);
    List<Video> findByPlaylist(String playlist);
    List<Video> getVideosByPlaylist(String playlist);
}