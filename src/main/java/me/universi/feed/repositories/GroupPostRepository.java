package me.universi.feed.repositories;

import me.universi.feed.entities.GroupPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GroupPostRepository extends MongoRepository<GroupPost, String> {

    List<GroupPost> findByGroupId(String groupId);

}