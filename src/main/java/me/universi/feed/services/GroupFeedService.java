package me.universi.feed.services;


import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.exceptions.PostNotFoundException;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.repositories.GroupPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupFeedService {

    private final GroupPostRepository groupPostRepository;

    @Autowired
    public GroupFeedService(GroupPostRepository groupPostRepository) {
        this.groupPostRepository = groupPostRepository;
    }

    public List<GroupPost> getGroupPosts(String groupId) {
        Optional<List<GroupPost>> posts = groupPostRepository.findByGroupIdAndDeletedIsFalse(groupId);
        return posts.orElseThrow(() -> new PostNotFoundException("No posts found for the specified group."));
    }

    public GroupPost createGroupPost(String groupId, GroupPostDTO groupPostDTO) {
        GroupPost groupPost = new GroupPost(groupId, groupPostDTO.getContent(), groupPostDTO.getAuthorId(), false);
        return groupPostRepository.save(groupPost);
    }

    public boolean deleteGroupPost(String groupId, String postId) {
        Optional<GroupPost> existingPost = groupPostRepository.findById(postId);
        if (existingPost.isPresent() && existingPost.get().getGroupId().equals(groupId) && !existingPost.get().isDeleted()) {
            existingPost.get().setDeleted(true);
            groupPostRepository.save(existingPost.get());
            return true;
        } else {
            throw new PostNotFoundException("Post not found or does not belong to the specified group.");
        }
    }
}