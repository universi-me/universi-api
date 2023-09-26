package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    Folder findFirstById(UUID id);
    List<Folder> findByName(String name);
    List<Folder> findByCategories(Category category);
    @Modifying
    @Transactional
    @Query(value = "UPDATE folder_contents SET order_num = :OrderNum WHERE folders_id = :FolderId AND contents_id = :ContentId", nativeQuery = true)
    void setPositionOfContentInFolder(@Param("FolderId") UUID folderId, @Param("ContentId") UUID contentId, @Param("OrderNum") int num);
    @Query(value = "SELECT order_num FROM folder_contents WHERE folders_id = :FolderId AND contents_id = :ContentId LIMIT 1", nativeQuery = true)
    int getPositionOfContentInFolder(@Param("FolderId") UUID folderId, @Param("ContentId") UUID contentId);
}