package me.universi.group.repositories;


import me.universi.group.entities.GroupEmailFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupEmailFilterRepository extends JpaRepository<GroupEmailFilter, UUID> {
}
