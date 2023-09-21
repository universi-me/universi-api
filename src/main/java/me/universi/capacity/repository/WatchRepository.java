package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface WatchRepository extends JpaRepository<Watch, UUID> {
    Watch findFirstById(UUID id);
    Watch findFirstByProfileIdAndContentId(UUID profileId, UUID contentId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM watch WHERE content_id = :ContentId", nativeQuery = true)
    void deleteByContentId(@Param("ContentId") UUID contentId);
}