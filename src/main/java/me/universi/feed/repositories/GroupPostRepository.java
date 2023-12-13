package me.universi.feed.repositories;

import java.util.Optional;
import me.universi.feed.entities.GroupPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GroupPostRepository extends MongoRepository<GroupPost, String> {
    Optional<List<GroupPost>> findByGroupId(String groupId);

    Optional<List<GroupPost>> findByGroupIdAndDeletedIsFalse(String groupId);

}