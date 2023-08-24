package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.VideoPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoPlaylistRepository extends JpaRepository<VideoPlaylist, UUID> {
    VideoPlaylist findFirstById(UUID id);
    List<VideoPlaylist> findByName(String name);
}