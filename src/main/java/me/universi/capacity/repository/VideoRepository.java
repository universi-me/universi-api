package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.capacity.entidades.Video;

public interface VideoRepository extends JpaRepository<Video, UUID> {
    Video findFirstById(UUID id);

    boolean existsByTitle(String title);
    boolean existsByUrl(String url);

    List<Video> findByCategories(VideoCategory videoCategory);
}