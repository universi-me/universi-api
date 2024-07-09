package me.universi.feed.repositories;

import java.util.List;
import java.util.Optional;
import me.universi.feed.entities.GroupPostComment;
import me.universi.feed.entities.GroupPostReaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupPostCommentRepository extends MongoRepository<GroupPostComment, String> {
    Optional<List<GroupPostComment>> findByGroupPostIdAndDeletedIsFalse(String groupPostId);

    Optional<GroupPostComment> findFirstByIdAndDeletedFalse(String id);

    Optional<GroupPostComment> findFirstByGroupPostIdAndAuthorIdAndDeletedIsFalse(String groupPostId, String authorId);
}
