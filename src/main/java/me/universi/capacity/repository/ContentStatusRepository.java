package me.universi.capacity.repository;

import java.util.UUID;

import me.universi.capacity.entidades.ContentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ContentStatusRepository extends JpaRepository<ContentStatus, UUID> {
    ContentStatus findFirstById(UUID id);
    ContentStatus findFirstByProfileIdAndContentId(UUID profileId, UUID contentId);
    @Modifying
    @Transactional
    @Query( value = "UPDATE ContentStatus cs SET cs.deleted = true WHERE cs.content.id = :ContentId" )
    void deleteByContentId(@Param("ContentId") UUID contentId);
}
