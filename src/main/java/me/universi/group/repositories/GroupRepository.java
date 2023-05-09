package me.universi.group.repositories;

import me.universi.group.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findFirstById(Long id);
    Optional<Group> findFirstByNickname(String nickname);
    Optional<Group> findFirstByRootGroupAndNickname(boolean rootGroup, String nickname);
    List<Group> findByPublicGroup(boolean grupoPublico);
    @Query(value = "SELECT id_group FROM subgroup WHERE id_subgroup = :GroupId LIMIT 1", nativeQuery = true)
    Optional<Long> findParentGroupId(@Param("GroupId") Long id);
    Collection<Group> findTop5ByNameContainingIgnoreCase(String nome);

    Optional<Group> findByIdAndAdminId(Long groupId, Long profileId);

    Boolean existsByIdAndAdminId(Long groupId, Long adminId);
}
