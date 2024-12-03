package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, UUID> {
    boolean existsByTitle(String title);
    boolean existsByUrl(String url);
    List<Content> findByCategories(Category category);
    @Query(value = "SELECT * FROM content INNER JOIN folder_contents ON content.id=folder_contents.contents_id WHERE folder_contents.folders_id=:FolderId AND content.deleted=false ORDER BY order_num ASC", nativeQuery = true)
    List<Content> findContentsInFolderByOrderPosition(@Param("FolderId") UUID folderId);
}