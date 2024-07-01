package me.universi.feed.repositories;

import java.util.List;
import java.util.Optional;
import me.universi.feed.entities.GroupPostReaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupPostReactionRepository extends MongoRepository<GroupPostReaction, String> {
    Optional<List<GroupPostReaction>> findByGroupPostIdAndDeletedIsFalse(String groupPostId);

    Optional<GroupPostReaction> findFirstByGroupPostIdAndId(String groupPostId, String postReactionId);

    Optional<GroupPostReaction> findFirstByGroupPostIdAndAuthorIdAndDeletedIsFalse(String groupPostId, String authorId);
}
