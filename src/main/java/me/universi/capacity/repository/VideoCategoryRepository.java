package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoCategoryRepository extends JpaRepository<VideoCategory, UUID> {
    VideoCategory findFirstById(UUID id);
    List<VideoCategory> findByName(String name);

    boolean existsByName(String name);
}