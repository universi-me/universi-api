package me.universi.group.repositories;

import me.universi.group.entities.Group;
import me.universi.group.entities.GroupType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findFirstById(UUID id);
    Optional<Group> findFirstByNickname(String nickname);
    Optional<Group> findFirstByParentGroupIsNullAndNicknameIgnoreCase(String nickname);
    Optional<Group> findFirstByParentGroupIsNull();
    List<Group> findByPublicGroup(boolean grupoPublico);

    List<Group> findByType( GroupType type );
    boolean existsByType( GroupType type );

    Collection<Group> findTop5ByNameContainingIgnoreCase(String nome);

    Optional<Group> findFirstByIdAndAdminId(UUID groupId, UUID profileId);

    Boolean existsByIdAndParticipantsId(UUID groupId, UUID profileId);
}
