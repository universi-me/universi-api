package me.universi.papers.repositories;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import me.universi.group.entities.Group;
import me.universi.papers.entities.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, UUID> {
    Optional<Paper> findFirstById(UUID id);
    Collection<Paper> findAllByGroup(Group group);
}
